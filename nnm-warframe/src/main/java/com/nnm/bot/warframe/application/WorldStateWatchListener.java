package com.nnm.bot.warframe.application;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.port.BotChannelInstanceIdentityResolver;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.constant.BotChannelStatus;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.message.model.BotProactiveGroupMessage;
import com.nnm.bot.core.message.sender.BotProactiveMessageSenderRouter;
import com.nnm.bot.warframe.application.plugin.AbstractWorldStateCommandPlugin.WorldStateCommandOutput;
import com.nnm.bot.warframe.application.plugin.WarframeCommandData;
import com.nnm.bot.warframe.domain.entity.WarframeGroupWatchRule;
import com.nnm.bot.warframe.domain.model.WorldState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 在 WorldState 更新后批量检查群组成员的蹲任务规则。
 *
 * <p>规则先通过数据库 CAS 更新幂等游标，再按群组聚合并选择一个在线机器人发送。
 * 单个群组发送失败不会阻断其他群组；发送失败不回滚游标，避免刷新重试造成重复提醒。</p>
 */
@Slf4j
@Component
public class WorldStateWatchListener {

    private static final int MAX_LOG_VALUE_LENGTH = 160;
    private static final Map<String, String> WATCH_TYPE_ALIASES = Map.ofEntries(
            Map.entry("警报", "alerts"),
            Map.entry("裂缝", "fissures"),
            Map.entry("钢铁裂缝", "steel-fissures"),
            Map.entry("九重天裂缝", "void-storms"),
            Map.entry("虚空风暴", "void-storms"),
            Map.entry("仲裁", "arbitration"),
            Map.entry("每日特惠", "daily-deals"),
            Map.entry("入侵", "invasions"),
            Map.entry("突击", "sorties"),
            Map.entry("执刑官猎杀", "archon-hunts"),
            Map.entry("奸商", "void-trader"),
            Map.entry("平原", "cycles"),
            Map.entry("周期", "cycles"),
            Map.entry("电波", "nightwave"),
            Map.entry("希图斯", "ostrons"),
            Map.entry("索拉里斯", "solaris"),
            Map.entry("英择谛", "entrati"));

    @Resource
    private WarframeGroupWatchRuleService ruleService;
    @Resource
    private WarframeReferenceProjectionEnricher projectionEnricher;
    @Resource
    private BotGroupBotRepository groupBotRepository;
    @Resource
    private BotGroupMemberRepository groupMemberRepository;
    @Resource
    private BotChannelInstanceProvider instanceProvider;
    @Resource
    private BotChannelInstanceIdentityResolver instanceIdentityResolver;
    @Resource
    private BotChannelRegistry channelRegistry;
    @Resource
    private BotProactiveMessageSenderRouter messageSenderRouter;

    @EventListener
    public void onWorldStateUpdated(WorldStateUpdatedEvent event) {
        if (event == null || event.previous() == null || event.current() == null) {
            return;
        }
        List<WatchCandidate> candidates = candidates(event.current());
        Map<Long, GroupNotification> notifications = new LinkedHashMap<>();
        for (WarframeGroupWatchRule rule : ruleService.findEnabledRules()) {
            try {
                List<WatchCandidate> matches = candidates.stream()
                        .filter(candidate -> matches(rule, candidate))
                        .toList();
                if (matches.isEmpty()) {
                    continue;
                }
                String matchKey = stableMatchKey(matches);
                if (!ruleService.updateLastMatchKey(rule.getId(), matchKey)) {
                    continue;
                }
                log.info(
                        "Warframe watch rule matched: groupId={}, memberId={}, ruleId={}, "
                                + "notifyEnabled={}, watchType={}, watchKey={}, watchValue={}, matches={}",
                        rule.getGroupId(),
                        rule.getMemberId(),
                        rule.getId(),
                        rule.isNotifyEnabled(),
                        rule.getWatchType(),
                        rule.getWatchKey(),
                        rule.getWatchValue(),
                        matches.stream().map(WatchCandidate::summary).toList());
                if (rule.isNotifyEnabled()) {
                    notifications.computeIfAbsent(
                                    rule.getGroupId(), GroupNotification::new)
                            .add(rule.getMemberId(), matches);
                }
            } catch (Exception exception) {
                // 单条规则的数据问题不能阻断其他群组成员继续处理。
                log.warn("Warframe watch rule processing failed: ruleId={}", rule.getId(), exception);
            }
        }
        sendNotifications(notifications.values());
    }

    static boolean matches(
            WarframeGroupWatchRule rule,
            WatchCandidate candidate) {
        if (StrUtil.isNotBlank(rule.getWatchType())
                && !"*".equals(rule.getWatchType().trim())
                && !normalizeType(rule.getWatchType()).equals(normalizeType(candidate.type()))) {
            return false;
        }
        String expected = normalize(rule.getWatchValue());
        if (expected.isEmpty()) {
            return false;
        }
        if (StrUtil.isBlank(rule.getWatchKey()) || "*".equals(rule.getWatchKey().trim())) {
            return normalize(candidate.values().toString()).contains(expected);
        }
        Object actual = valueByKey(candidate.values(), rule.getWatchKey());
        return actual != null && normalize(String.valueOf(actual)).contains(expected);
    }

    private static String stableMatchKey(List<WatchCandidate> matches) {
        String content = matches.stream()
                .map(WatchCandidate::stableKey)
                .sorted()
                .distinct()
                .reduce((left, right) -> left + "|" + right)
                .orElse("");
        return sha256(content);
    }

    private List<WatchCandidate> candidates(WorldState state) {
        List<TypedProjection> projections = projections(state);
        List<WorldStateCommandOutput> outputs = projectionEnricher.enrichAll(
                projections.stream().map(TypedProjection::output).toList());
        List<WatchCandidate> result = new ArrayList<>();
        for (int index = 0; index < outputs.size(); index++) {
            add(result, projections.get(index).type(), outputs.get(index));
        }
        return result;
    }

    private void sendNotifications(Collection<GroupNotification> notifications) {
        if (notifications.isEmpty()) {
            return;
        }
        Set<Long> groupIds = notifications.stream()
                .map(GroupNotification::groupId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<Long> memberIds = notifications.stream()
                .flatMap(notification -> notification.memberIds().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, BotGroupMember> members = groupMemberRepository
                .findAllByIdInAndEnabledTrue(memberIds).stream()
                .collect(Collectors.toMap(BotGroupMember::getId, Function.identity()));
        Map<Long, List<BotGroupBot>> bindingsByGroup = groupBotRepository
                .findAllByGroupIdInAndEnabledTrue(groupIds).stream()
                .collect(Collectors.groupingBy(
                        BotGroupBot::getGroupId, LinkedHashMap::new, Collectors.toList()));
        Map<Long, BotChannelInstanceRecord> instances = loadInstancesByDatabaseId();

        for (GroupNotification notification : notifications) {
            try {
                Optional<SelectedChannel> selected = selectChannel(
                        bindingsByGroup.getOrDefault(notification.groupId(), List.of()),
                        instances);
                if (selected.isEmpty()) {
                    log.warn("No connected bot for Warframe watch notification: groupId={}",
                            notification.groupId());
                    continue;
                }
                SelectedChannel channel = selected.get();
                List<String> mentions = channel.platform() == BotPlatform.ONEBOT
                        ? notification.memberIds().stream()
                                .map(members::get)
                                .filter(java.util.Objects::nonNull)
                                .map(BotGroupMember::getQqId)
                                .filter(StrUtil::isNotBlank)
                                .distinct()
                                .toList()
                        : List.of();
                messageSenderRouter.sendGroupText(
                        channel.platform(),
                        new BotProactiveGroupMessage(
                                channel.instance().botAccountId(),
                                channel.binding().getScopeKey(),
                                notification.message(),
                                mentions));
            } catch (Exception exception) {
                log.warn("Warframe watch notification failed: groupId={}",
                        notification.groupId(), exception);
            }
        }
    }

    private Map<Long, BotChannelInstanceRecord> loadInstancesByDatabaseId() {
        Map<Long, BotChannelInstanceRecord> result = new LinkedHashMap<>();
        for (BotChannelInstanceRecord instance : instanceProvider.findAll()) {
            if (!instance.enabled()) {
                continue;
            }
            instanceIdentityResolver.resolveId(instance.platform(), instance.botAccountId())
                    .ifPresent(id -> result.put(id, instance));
        }
        return result;
    }

    private Optional<SelectedChannel> selectChannel(
            List<BotGroupBot> bindings,
            Map<Long, BotChannelInstanceRecord> instances) {
        return bindings.stream()
                .map(binding -> selectedChannel(binding, instances.get(binding.getBotInstanceId())))
                .flatMap(Optional::stream)
                .filter(this::connected)
                .sorted(Comparator
                        .comparingInt((SelectedChannel channel) ->
                                channel.platform() == BotPlatform.ONEBOT ? 0 : 1)
                        .thenComparing(channel -> channel.binding().getId()))
                .findFirst();
    }

    private static Optional<SelectedChannel> selectedChannel(
            BotGroupBot binding, BotChannelInstanceRecord instance) {
        if (instance == null
                || StrUtil.isBlank(binding.getPlatform())
                || StrUtil.isBlank(binding.getScopeKey())) {
            return Optional.empty();
        }
        try {
            BotPlatform platform = BotPlatform.valueOf(
                    binding.getPlatform().trim().toUpperCase(Locale.ROOT));
            if (platform != instance.platform()) {
                return Optional.empty();
            }
            return Optional.of(new SelectedChannel(binding, instance, platform));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private boolean connected(SelectedChannel channel) {
        return channelRegistry.find(new BotChannelKey(
                        channel.platform(), channel.instance().botAccountId()))
                .map(snapshot -> snapshot.status() == BotChannelStatus.CONNECTED)
                .orElse(false);
    }

    private static List<TypedProjection> projections(WorldState state) {
        return List.of(
                new TypedProjection("alerts", WarframeCommandData.alerts(state)),
                new TypedProjection("sorties", WarframeCommandData.sorties(state, false)),
                new TypedProjection("archon-hunts", WarframeCommandData.sorties(state, true)),
                new TypedProjection("void-trader", WarframeCommandData.voidTrader(state)),
                new TypedProjection("arbitration", WarframeCommandData.arbitration(state, false)),
                new TypedProjection("daily-deals", WarframeCommandData.dailyDeals(state)),
                new TypedProjection("invasions", WarframeCommandData.invasions(state)),
                new TypedProjection("fissures", WarframeCommandData.fissures(
                        state, mission -> !mission.hard() && !mission.voidStorm(), "fissures")),
                new TypedProjection("steel-fissures", WarframeCommandData.fissures(
                        state, mission -> mission.hard() && !mission.voidStorm(), "steel-fissures")),
                new TypedProjection("void-storms", WarframeCommandData.fissures(
                        state, WorldState.ActiveMission::voidStorm, "void-storms")),
                new TypedProjection("steel-path", WarframeCommandData.steelPath(state)),
                new TypedProjection("cycles", WarframeCommandData.cycles(state)),
                new TypedProjection("nightwave", WarframeCommandData.nightwave(state)),
                new TypedProjection("ostrons", WarframeCommandData.syndicate(
                        state, "ostrons", value -> contains(value, "ostrons", "cetus", "希图斯"))),
                new TypedProjection("solaris", WarframeCommandData.syndicate(
                        state, "solaris", value -> contains(value, "solaris", "vallis", "索拉里斯"))),
                new TypedProjection("entrati", WarframeCommandData.syndicate(
                        state, "entrati", value -> contains(value, "entrati", "deimos", "英择谛"))));
    }

    private static void add(
            List<WatchCandidate> result,
            String type,
            WorldStateCommandOutput output) {
        Object value = output.data().get("items");
        if (!(value instanceof List<?> items)) {
            return;
        }
        for (Object item : items) {
            if (!(item instanceof Map<?, ?> source)) {
                continue;
            }
            Map<String, Object> values = new LinkedHashMap<>();
            source.forEach((key, entry) -> values.put(String.valueOf(key), entry));
            result.add(new WatchCandidate(type, values));
        }
    }

    private static boolean contains(String value, String... candidates) {
        String normalized = normalize(value);
        for (String candidate : candidates) {
            if (normalized.contains(normalize(candidate))) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value) {
        return StrUtil.emptyIfNull(value).trim().toLowerCase(Locale.ROOT);
    }

    static String normalizeType(String value) {
        String normalized = normalize(value);
        return WATCH_TYPE_ALIASES.getOrDefault(normalized, normalized);
    }

    /**
     * 规则字段允许使用管理端常见的 snake_case、camelCase 或中文名称。
     * WorldState 投影统一使用 camelCase，因此这里做一次只读的规范化查找。
     */
    private static Object valueByKey(Map<String, Object> values, String requestedKey) {
        String normalizedRequestedKey = normalizeKey(requestedKey);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (normalizeKey(entry.getKey()).equals(normalizedRequestedKey)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static String normalizeKey(String value) {
        String normalized = normalize(value);
        return switch (normalized) {
            case "任务类型" -> "missiontype";
            case "节点" -> "node";
            case "奖励" -> "reward";
            case "地点" -> "location";
            default -> normalized.replaceAll("[\\s_-]+", "");
        };
    }

    private static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    record WatchCandidate(String type, Map<String, Object> values) {

        private String stableKey() {
            for (String key : List.of(
                    "id", "node", "title", "name", "item", "missionType", "type", "location")) {
                Object value = values.get(key);
                if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
                    return type + ":" + key + ":" + value;
                }
            }
            return type + ":" + sha256(values.toString());
        }

        private String summary() {
            String value = values.toString();
            return value.length() <= MAX_LOG_VALUE_LENGTH
                    ? value
                    : value.substring(0, MAX_LOG_VALUE_LENGTH) + "...";
        }

        private String messageLine() {
            List<String> parts = new ArrayList<>();
            for (String key : List.of(
                    "node", "missionType", "item", "reward", "location", "title", "name")) {
                Object value = values.get(key);
                if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
                    parts.add(String.valueOf(value));
                }
            }
            return "- " + displayType(type) + "："
                    + (parts.isEmpty() ? summary() : String.join(" / ", parts));
        }
    }

    private static String displayType(String type) {
        return switch (type) {
            case "alerts" -> "警报";
            case "sorties" -> "突击";
            case "archon-hunts" -> "执刑官猎杀";
            case "void-trader" -> "虚空商人";
            case "arbitration" -> "仲裁";
            case "daily-deals" -> "每日特惠";
            case "invasions" -> "入侵";
            case "fissures" -> "裂缝";
            case "steel-fissures" -> "钢铁裂缝";
            case "void-storms" -> "虚空风暴";
            case "steel-path" -> "钢铁奖励";
            case "cycles" -> "世界周期";
            case "nightwave" -> "电波";
            case "ostrons" -> "希图斯";
            case "solaris" -> "索拉里斯";
            case "entrati" -> "英择谛";
            default -> type;
        };
    }

    private record TypedProjection(String type, WorldStateCommandOutput output) {
    }

    private record SelectedChannel(
            BotGroupBot binding,
            BotChannelInstanceRecord instance,
            BotPlatform platform) {
    }

    private static final class GroupNotification {

        private final long groupId;
        private final Set<Long> memberIds = new LinkedHashSet<>();
        private final Map<String, WatchCandidate> candidates = new LinkedHashMap<>();

        private GroupNotification(long groupId) {
            this.groupId = groupId;
        }

        private void add(long memberId, List<WatchCandidate> matches) {
            memberIds.add(memberId);
            matches.forEach(candidate -> candidates.putIfAbsent(
                    candidate.stableKey(), candidate));
        }

        private long groupId() {
            return groupId;
        }

        private Set<Long> memberIds() {
            return memberIds;
        }

        private String message() {
            return "Warframe 蹲任务命中\n" + candidates.values().stream()
                    .map(WatchCandidate::messageLine)
                    .collect(Collectors.joining("\n"));
        }
    }
}
