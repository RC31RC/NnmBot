package com.nnm.bot.warframe.persistent.curated.service.watch.impl;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.BountyCycleJobDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.SyndicateMissionDTO;
import java.util.Map.Entry;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotChannelTarget;
import com.nnm.bot.core.channel.port.BotChannelInstanceIdentityResolver;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.channel.runtime.BotChannelTargetSelector;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotChannelStatus;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.message.model.BotProactiveGroupMessage;
import com.nnm.bot.core.message.sender.BotProactiveMessageSenderRouter;
import com.nnm.bot.warframe.persistent.curated.service.watch.WarframeWorldStateWatchService;
import com.nnm.bot.warframe.persistent.curated.config.watch.WarframeSubscriptionProperties;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeWatchMatchDTO;
import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationService;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeGroupWatchRuleDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.persistent.curated.service.watch.WarframeGroupWatchRuleService;
import com.nnm.bot.warframe.persistent.curated.cache.watch.WarframeSubscriptionEventMemory;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeReferenceProjectionEnrichmentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 在 WorldStateDTO 更新后批量检查群组成员的蹲任务规则。
 *
 * <p>规则先通过数据库行锁认领独立事件身份，再按群组聚合并选择一个在线机器人发送。
 * 单个群组发送失败不会阻断其他群组；发送失败不回滚游标，避免刷新重试造成重复提醒。</p>
 */
@Slf4j
@Service
public class WarframeWorldStateWatchServiceImpl implements WarframeWorldStateWatchService {

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
            Map.entry("平原", "cetus-night"),
            Map.entry("特惠", "daily-deals"),
            Map.entry("钢精", "steel-path"),
            Map.entry("活动", "events"),
            Map.entry("圣所", "cavia"),
            Map.entry("扎里曼", "holdfasts"),
            Map.entry("六人组", "hex-bounties"),
            Map.entry("六人组箱", "hex-caches"),
            Map.entry("周期", "cycles"),
            Map.entry("电波", "nightwave"),
            Map.entry("科研", "archimedeas"),
            Map.entry("深层科研", "deep-archimedeas"),
            Map.entry("时光科研", "hex-archimedeas"),
            Map.entry("希图斯", "ostrons"),
            Map.entry("索拉里斯", "solaris"),
            Map.entry("英择谛", "entrati"));

    @Resource
    private WarframeGroupWatchRuleService ruleService;
    @Resource
    private WarframeSubscriptionEventMemory eventMemory;
    @Resource
    private WarframeReferenceProjectionEnrichmentService projectionEnricher;
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
    @Resource
    private ObjectProvider<ClusterBotRuntime> clusterProvider;
    @Resource
    private BotChannelTargetSelector targetSelector;
    @Resource
    private WarframeSubscriptionProperties subscriptionProperties;
    @Resource
    private WarframeArbitrationService arbitrationService;
    @Resource
    private Clock warframeClock;

    /**
     * 根据最新世界状态匹配订阅并向可路由的群组发送通知。
     *
     * @param current 最近成功发布的世界状态。
     */
    @Override
    public void notifyMatches(WorldStateDTO current) {
        if (current == null) {
            return;
        }
        List<WatchCandidate> candidates = candidates(current);
        List<WarframeGroupWatchRuleDTO> rules = ruleService.findEnabledRules();
        Map<Long, Boolean> locallyRoutable = new LinkedHashMap<>();
        Set<Long> notificationGroups = rules.stream().filter(WarframeGroupWatchRuleDTO::isNotifyEnabled)
                .map(WarframeGroupWatchRuleDTO::getGroupId).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(notificationGroups)) {
            Map<Long, BotChannelInstanceRecord> instances = loadInstancesByDatabaseId();
            Map<Long, List<BotGroupBot>> bindings = groupBotRepository.findAllByGroupIdInAndEnabledTrue(notificationGroups)
                    .stream().collect(Collectors.groupingBy(BotGroupBot::getGroupId));
            bindings.forEach((groupId, values) -> locallyRoutable.put(groupId,
                    selectChannel(values, instances).isPresent()));
        }
        Map<Long, GroupNotification> notifications = new LinkedHashMap<>();
        for (WarframeGroupWatchRuleDTO rule : rules) {
            try {
                // A node without a sending connection must not consume the database cursor.
                if (rule.isNotifyEnabled()
                        && !locallyRoutable.getOrDefault(rule.getGroupId(), false)) continue;
                List<WatchCandidate> matches = candidates.stream()
                        .filter(candidate -> matches(rule, candidate))
                        .toList();
                if (CollUtil.isEmpty(matches)) {
                    continue;
                }
                List<WarframeWatchMatchDTO> pending = eventMemory.pending(rule.getMatchedEventsJson(), matches.stream()
                        .map(candidate -> new WarframeWatchMatchDTO(candidate.stableKey(), candidate.expiry())).toList());
                if (CollUtil.isEmpty(pending)) continue;
                Set<String> claimed = ruleService.claimMatches(rule.getId(), pending);
                matches = matches.stream().filter(candidate -> claimed.contains(candidate.stableKey())).toList();
                if (CollUtil.isEmpty(matches)) continue;
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
            } catch (RuntimeException exception) {
                // 单条规则的数据问题不能阻断其他群组成员继续处理。
                log.warn("Warframe watch rule processing failed: ruleId={}", rule.getId(), exception);
            }
        }
        sendNotifications(notifications.values());
    }

    /**
     * 判断数据是否符合指定匹配条件。
     *
     * @param rule 规则。
     * @param candidate 候选。
     * @return 满足数据是否符合指定匹配条件时为真，否则为假。
     */
    static boolean matches(
            WarframeGroupWatchRuleDTO rule,
            WatchCandidate candidate) {
        String requestedType = normalizeType(rule.getWatchType());
        String actualType = normalizeType(candidate.type());
        boolean allFissures = "filter".equals(rule.getWatchKey()) && "fissures".equals(requestedType)
                && Set.of("fissures", "steel-fissures", "void-storms").contains(actualType);
        if (!"*".equals(requestedType) && StrUtil.isNotBlank(requestedType)
                && !requestedType.equals(actualType) && !allFissures) return false;
        String expected = normalize(rule.getWatchValue());
        if (StrUtil.isBlank(expected)) return false;
        if ("filter".equals(rule.getWatchKey())) {
            if ("*".equals(expected)) return true;
            for (String alternative : expected.split("\\|")) {
                if (matchesFilter(alternative, candidate)) return true;
            }
            return false;
        }
        if (StrUtil.isBlank(rule.getWatchKey()) || "*".equals(rule.getWatchKey().trim())) {
            return normalize(candidate.values().toString()).contains(expected);
        }
        Object actual = valueByKey(candidate.values(), rule.getWatchKey());
        return actual != null && normalize(String.valueOf(actual)).contains(expected);
    }

    /**
     * 判断是否满足匹配筛选条件。
     *
     * @param filter 筛选。
     * @param candidate 候选。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean matchesFilter(String filter, WatchCandidate candidate) {
        String remaining = filter;
        boolean hard = "steel-fissures".equals(candidate.type()) || Boolean.TRUE.equals(candidate.values().get("hard"));
        if (remaining.contains("普通")) {
            if (hard) return false;
            remaining = remaining.replace("普通", "");
        }
        if (remaining.contains("钢铁")) {
            if (!hard) return false;
            remaining = remaining.replace("钢铁", "");
        }
        Map<String, List<String>> tiers = Map.of(
                "古纪", List.of("lith", "tier1", "古纪", "requiem", "安魂", "omnia", "全能"),
                "前纪", List.of("meso", "tier2", "前纪"),
                "中纪", List.of("neo", "tier3", "中纪"),
                "后纪", List.of("axi", "tier4", "后纪"),
                "安魂", List.of("requiem", "tier5", "安魂"),
                "全能", List.of("omnia", "tier6", "全能"));
        for (Entry<String,List<String>> tier : tiers.entrySet()) {
            if (!remaining.contains(tier.getKey())) continue;
            String actual = normalize(Objects.toString(candidate.values().get("tier"), ""));
            if (tier.getValue().stream().noneMatch(actual::contains)) return false;
            remaining = remaining.replace(tier.getKey(), "");
        }
        if (StrUtil.isEmpty(remaining)) return true;
        String node = normalize(Objects.toString(candidate.values().get("node"), ""));
        String nodeName = normalize(Objects.toString(candidate.values().get("nodeName"), ""));
        String system = normalize(Objects.toString(candidate.values().get("systemName"), ""));
        String planet = normalize(Objects.toString(candidate.values().get("planet"), ""));
        String mission = normalize(Objects.toString(candidate.values().get("missionType"),
                Objects.toString(candidate.values().get("type"), "")));
        // 条件中的星球与任务必须各自连续匹配，不能逐字拼凑出不存在的任务。
        if (StrUtil.isNotBlank(mission) && remaining.endsWith(mission)) {
            String location = remaining.substring(0, remaining.length() - mission.length());
            return StrUtil.isEmpty(location) || node.contains(location) || nodeName.contains(location)
                    || system.contains(location) || planet.contains(location);
        }
        // 九重天按节点名订阅；H2 星云等数字身份不能被拆成单字模糊匹配。
        return node.contains(remaining) || nodeName.contains(remaining)
                || system.contains(remaining) || planet.contains(remaining);
    }

    /**
     * 按输入条件选择候选记录。
     *
     * @param state 已经解析的当前世界状态。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WatchCandidate> candidates(WorldStateDTO state) {
        List<TypedProjection> projections = new ArrayList<>(projections(state));
        Instant now = warframeClock.instant();
        Instant horizon = now.plus(subscriptionProperties.getAdvanceNotice());
        // 仲裁由已采纳排期提供；PHP 可能完全不下发 Arbitration。
        try {
            ArbitrationScheduleDTO schedule = arbitrationService.query(QueryType.UPCOMING);
            if (schedule != null) {
                List<Map<String, Object>> rows = schedule.entries().stream()
                        .filter(entry -> entry.activation() != null && !entry.activation().isAfter(horizon)
                                && entry.expiry() != null && entry.expiry().isAfter(now))
                        .map(entry -> Map.<String, Object>of("id", entry.id(), "node", entry.node(),
                                "missionType", entry.missionType(), "planet", entry.planet(),
                                "activationInstant", entry.activation().toString(), "expiryInstant", entry.expiry().toString()))
                        .toList();
                projections.add(new TypedProjection("arbitration", new WarframeWorldStateCommandVO("仲裁", Map.of("items", rows))));
            }
        } catch (RuntimeException exception) {
            log.warn("Approved arbitration schedule unavailable for subscription matching", exception);
        }
        // 给投影附加原始协议身份，再执行本地化；显示名或剩余分钟变化不改变事件身份。
        projections = projections.stream().map(WarframeWorldStateWatchServiceImpl::withEventKeys).toList();
        List<WarframeWorldStateCommandVO> outputs = projectionEnricher.enrichAll(
                projections.stream().map(TypedProjection::output).toList());
        List<WatchCandidate> result = new ArrayList<>();
        for (int index = 0; index < outputs.size(); index++) {
            add(result, projections.get(index).type(), outputs.get(index));
        }
        if (state.cetusCycle() != null && state.cetusCycle().isDay()
                && state.cetusCycle().expiry() != null && state.cetusCycle().expiry().isAfter(now)
                && !state.cetusCycle().expiry().isAfter(horizon)) {
            result.add(new WatchCandidate("cetus-night", Map.of("node", "夜灵平原", "missionType", "夜晚即将开始",
                    "expiryInstant", state.cetusCycle().expiry().toString(),
                    "eventKey", sha256("cetus-night:" + state.cetusCycle().expiry()))));
        }
        return result.stream().filter(candidate -> eligible(candidate, now)).toList();
    }

    /**
     * 判断是否满足符合条件条件。
     *
     * @param candidate 候选。
     * @param now 本次操作使用的当前时间。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private boolean eligible(WatchCandidate candidate, Instant now) {
        if (candidate.expiry() != null && !candidate.expiry().isAfter(now)) return false;
        if ("void-trader".equals(candidate.type()) && !Boolean.TRUE.equals(candidate.values().get("active"))) return false;
        if ("invasions".equals(candidate.type())) {
            if (Boolean.TRUE.equals(candidate.values().get("completed"))) return false;
            String rewards = normalize(Objects.toString(candidate.values().get("attackerRewards"), "")
                    + Objects.toString(candidate.values().get("defenderRewards"), "")
                    + Objects.toString(candidate.values().get("attackerReward"), "")
                    + Objects.toString(candidate.values().get("defenderReward"), ""));
            return subscriptionProperties.getSpecialInvasionRewards().stream().map(WarframeWorldStateWatchServiceImpl::normalize)
                    .anyMatch(rewards::contains);
        }
        return true;
    }

    /**
     * 附带事件键集。
     *
     * @param projection 投影。
     * @return 类型化投影对象。
     */
    private static TypedProjection withEventKeys(TypedProjection projection) {
        Object value = projection.output().data().get("items");
        if (!(value instanceof List<?> rows)) return projection;
        List<Map<String, Object>> items = new ArrayList<>();
        for (Object row : rows) {
            if (!(row instanceof Map<?, ?> source)) continue;
            Map<String, Object> fields = new LinkedHashMap<>();
            source.forEach((key, entry) -> fields.put(String.valueOf(key), entry));
            Map<String, Object> identity = new TreeMap<>();
            for (String key : List.of("id", "node", "location", "item", "reward", "title", "type", "missionType",
                    "activationInstant", "expiryInstant", "hard", "voidStorm", "tier", "challenge")) {
                if (fields.containsKey(key)) identity.put(key, fields.get(key));
            }
            fields.put("eventKey", sha256(projection.type() + ":" + identity));
            items.add(fields);
        }
        Map<String, Object> data = new LinkedHashMap<>(projection.output().data());
        data.put("items", items);
        return new TypedProjection(projection.type(), new WarframeWorldStateCommandVO(
                projection.output().text(), data, projection.output().unresolvedReferences()));
    }

    /**
     * 为各群组选择可用机器人并发送通知，单个群组失败不阻断其他群组。
     * @param notifications 通知集合。
     */
    private void sendNotifications(Collection<GroupNotification> notifications) {
        if (CollUtil.isEmpty(notifications)) {
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
                                .filter(Objects::nonNull)
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
            } catch (RuntimeException exception) {
                log.warn("Warframe watch notification failed: groupId={}",
                        notification.groupId(), exception);
            }
        }
    }

    /**
     * 加载实例按数据库标识。
     *
     * @return 以本方法约定的业务键组织的数据映射。
     */
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

    /**
     * 选择能够处理当前通知的机器人通道。
     *
     * @param bindings 绑定集合。
     * @param instances 实例。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    private Optional<SelectedChannel> selectChannel(
            List<BotGroupBot> bindings,
            Map<Long, BotChannelInstanceRecord> instances) {
        Optional<BotChannelTarget> selected = targetSelector.candidates(
                        bindings, instances, Set.of(BotChannelCapability.TEXT_MESSAGE))
                .stream()
                .filter(target -> connected(new SelectedChannel(
                        target.binding(), target.instance(), target.instance().platform())))
                .findFirst();
        return selected.map(target -> new SelectedChannel(
                target.binding(), target.instance(), target.instance().platform()));
    }

    /**
     * 判断是否满足连接条件。
     *
     * @param channel 通道。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private boolean connected(SelectedChannel channel) {
        BotChannelKey key = new BotChannelKey(channel.platform(), channel.instance().botAccountId());
        ClusterBotRuntime cluster = clusterProvider.getObject();
        if (!cluster.isLocalOwner(key)) return false;
        return channelRegistry.find(key)
                .map(snapshot -> snapshot.status() == BotChannelStatus.CONNECTED)
                .orElse(false);
    }

    /**
     * 组织当前世界状态各业务分区的展示投影。
     *
     * @param state 已经解析的当前世界状态。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<TypedProjection> projections(WorldStateDTO state) {
        return List.of(
                new TypedProjection("alerts", WarframeWorldStateProjector.alerts(state)),
                new TypedProjection("sorties", WarframeWorldStateProjector.sorties(state, false)),
                new TypedProjection("archon-hunts", WarframeWorldStateProjector.sorties(state, true)),
                new TypedProjection("void-trader", WarframeWorldStateProjector.voidTrader(state)),
                new TypedProjection("daily-deals", WarframeWorldStateProjector.dailyDeals(state)),
                new TypedProjection("events", WarframeWorldStateProjector.events(state)),
                new TypedProjection("cavia", bountyJobs(state, "EntratiLabSyndicate", false)),
                new TypedProjection("holdfasts", bountyJobs(state, "ZarimanSyndicate", false)),
                new TypedProjection("hex-bounties", bountyJobs(state, "HexSyndicate", false)),
                new TypedProjection("hex-caches", bountyJobs(state, "HexSyndicate", true)),
                new TypedProjection("invasions", WarframeWorldStateProjector.invasions(state)),
                new TypedProjection("fissures", WarframeWorldStateProjector.fissures(
                        state, mission -> !mission.hard() && !mission.voidStorm(), "fissures")),
                new TypedProjection("steel-fissures", WarframeWorldStateProjector.fissures(
                        state, mission -> mission.hard() && !mission.voidStorm(), "steel-fissures")),
                new TypedProjection("void-storms", WarframeWorldStateProjector.fissures(
                        state, WorldStateDTO.ActiveMissionDTO::voidStorm, "void-storms")),
                new TypedProjection("steel-path", WarframeWorldStateProjector.steelPath(state)),
                new TypedProjection("cycles", WarframeWorldStateProjector.cycles(state)),
                new TypedProjection("nightwave", WarframeWorldStateProjector.nightwave(state)),
                new TypedProjection("archimedeas", WarframeWorldStateProjector.archimedeas(
                        state, "科研", conquest -> true)),
                new TypedProjection("deep-archimedeas", WarframeWorldStateProjector.archimedeas(
                        state, "深层科研",
                        conquest -> "CTLAB".equals(normalize(conquest.typeKey())))),
                new TypedProjection("hex-archimedeas", WarframeWorldStateProjector.archimedeas(
                        state, "时光科研",
                        conquest -> "CTHEX".equals(normalize(conquest.typeKey())))),
                new TypedProjection("descents", WarframeWorldStateProjector.descents(state)),
                new TypedProjection("global-upgrades", WarframeWorldStateProjector.globalUpgrades(state)),
                new TypedProjection("flash-sales", WarframeWorldStateProjector.flashSales(state)),
                new TypedProjection("persistent-enemies", WarframeWorldStateProjector.persistentEnemies(state)),
                new TypedProjection("ostrons", WarframeWorldStateProjector.syndicate(
                        state, "ostrons", value -> contains(value, "ostrons", "cetus", "希图斯"))),
                new TypedProjection("solaris", WarframeWorldStateProjector.syndicate(
                        state, "solaris", value -> contains(value, "solaris", "vallis", "索拉里斯"))),
                new TypedProjection("entrati", WarframeWorldStateProjector.syndicate(
                        state, "entrati", value -> contains(value, "entrati", "deimos", "英择谛"))));
    }

    /**
     * oracle 行序表达赏金档次，只按最高档判断“高级”，不把静态奖励池位置冒充当前等级。
     *
     * @param state 已经解析的当前世界状态。
     * @param syndicate 集团。
     * @param caches 缓存。
     * @return 当前命令的标题、状态及展示条目。
     */
    static WarframeWorldStateCommandVO bountyJobs(WorldStateDTO state, String syndicate, boolean caches) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (SyndicateMissionDTO mission : state.syndicateMissions()) {
            if (!syndicate.equals(mission.syndicate())) continue;
            for (BountyCycleJobDTO job : mission.supplementalJobs()) {
                boolean matches = caches
                        ? job.missionTypeKey() != null && job.challenge() != null
                                && job.challenge().endsWith("/VaniaSafeCracker")
                        : "MT_EXTERMINATION".equals(job.missionTypeKey())
                                && job.tier() > 0 && job.tier() == job.totalTiers();
                // 未解析的节点和挑战不能凭文字相似度触发订阅。
                if (!matches || job.nodeName() == null || job.challengeName() == null) continue;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", mission.id());
                row.put("node", job.node());
                row.put("nodeName", job.nodeName());
                row.put("missionType", job.missionType());
                row.put("challenge", job.challenge());
                row.put("title", job.challengeName());
                row.put("tier", job.tier());
                row.put("activationInstant", mission.activation() == null ? "" : mission.activation().toString());
                row.put("expiryInstant", mission.expiry() == null ? "" : mission.expiry().toString());
                items.add(row);
            }
        }
        return new WarframeWorldStateCommandVO(syndicate, Map.of("items", items));
    }

    /**
     * 加入一条数据并维护当前容器。
     *
     * @param result 当前正在组装的业务结果。
     * @param type 目标业务类型。
     * @param output 需要补全或转换的输出数据。
     */
    private static void add(
            List<WatchCandidate> result,
            String type,
            WarframeWorldStateCommandVO output) {
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

    /**
     * 判断文本或集合是否包含指定内容。
     *
     * @param value 待校验或规范化的文本。
     * @param candidates 候选。
     * @return 满足文本或集合是否包含指定内容时为真，否则为假。
     */
    private static boolean contains(String value, String... candidates) {
        String normalized = normalize(value);
        for (String candidate : candidates) {
            if (normalized.contains(normalize(candidate))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 规范化输入，消除查询或比较时的格式差异。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化后的文本，供比较或查找使用。
     */
    private static String normalize(String value) {
        return StrUtil.emptyIfNull(value).trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 规范化外部类型标识，便于同一业务规则比较。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化外部类型标识，便于同一业务规则比较对应的文本。
     */
    static String normalizeType(String value) {
        String normalized = normalize(value);
        return WATCH_TYPE_ALIASES.getOrDefault(normalized, normalized);
    }

    /**
     * 规则字段允许使用管理端常见的 snake_case、camelCase 或中文名称。
     *   WorldStateDTO 投影统一使用 camelCase，因此这里做一次只读的规范化查找。
     *
     * @param values 待处理的值集合。
     * @param requestedKey 请求的键。
     * @return 按当前字段合同转换的值，保留允许的缺省状态。
     */
    private static Object valueByKey(Map<String, Object> values, String requestedKey) {
        String normalizedRequestedKey = normalizeKey(requestedKey);
        for (Entry<String,Object> entry : values.entrySet()) {
            if (normalizeKey(entry.getKey()).equals(normalizedRequestedKey)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 规范化业务键，消除首尾空白和大小写差异。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化业务键，消除首尾空白和大小写差异对应的文本。
     */
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

    /**
     * 计算内容的 SHA-256 摘要。
     *
     * @param value 待校验或规范化的文本。
     * @return 输入内容的 SHA-256 十六进制摘要。
     */
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

        /**
         * 读取上游显式提供的稳定业务键。
         *
         * @return 上游提供的稳定业务键；缺省时为空。
         */
        String stableKey() {
            if (values.get("eventKey") instanceof String key) return key;
            for (String key : List.of(
                    "id", "node", "title", "name", "item", "missionType", "type", "location")) {
                Object value = values.get(key);
                if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
                    return sha256(type + ":" + key + ":" + value + ":" + Objects.toString(values.get("expiryInstant"), ""));
                }
            }
            return type + ":" + sha256(values.toString());
        }

        /**
         * 读取当前任务或事件的有效期结束时间。
         *
         * @return 时间点对象。
         */
        private Instant expiry() {
            Object value = values.get("expiryInstant");
            return value instanceof String text && StrUtil.isNotBlank(text) ? Instant.parse(text) : null;
        }

        /**
         * 生成当前业务数据的简要说明。
         *
         * @return 当前业务数据的简要说明对应的文本。
         */
        private String summary() {
            String value = values.toString();
            return value.length() <= MAX_LOG_VALUE_LENGTH
                    ? value
                    : value.substring(0, MAX_LOG_VALUE_LENGTH) + "...";
        }

        /**
         * 构造通知消息的一行展示文本。
         *
         * @return 构造通知消息的一行展示文本对应的文本。
         */
        private String messageLine() {
            List<String> parts = new ArrayList<>();
            Object node = values.getOrDefault("nodeName", values.get("node"));
            if (node != null && StrUtil.isNotBlank(String.valueOf(node))) parts.add(String.valueOf(node));
            for (String key : List.of( "missionType", "item", "reward", "location", "title", "name")) {
                Object value = values.get(key);
                if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
                    parts.add(String.valueOf(value));
                }
            }
            return "- " + displayType(type) + "："
                    + (CollUtil.isEmpty(parts) ? summary() : String.join(" / ", parts));
        }
    }

    /**
     * 将订阅类型代码转换为通知使用的中文名称。
     * @param type 目标业务类型。
     * @return 展示类型对应的文本。
     */
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
            case "cetus-night" -> "平原夜晚预告";
            case "events" -> "活动";
            case "cavia" -> "圣所";
            case "holdfasts" -> "扎里曼";
            case "hex-bounties" -> "六人组";
            case "hex-caches" -> "六人组箱";
            case "nightwave" -> "电波";
            case "descents" -> "深层下降";
            case "ostrons" -> "希图斯";
            case "solaris" -> "索拉里斯";
            case "entrati" -> "英择谛";
            default -> type;
        };
    }

    private record TypedProjection(String type, WarframeWorldStateCommandVO output) {
    }

    private record SelectedChannel(
            BotGroupBot binding,
            BotChannelInstanceMetadata instance,
            BotPlatform platform) {
    }

    private static final class GroupNotification {

        private final long groupId;
        private final Set<Long> memberIds = new LinkedHashSet<>();
        private final Map<String, WatchCandidate> candidates = new LinkedHashMap<>();

        /**
         * 构造分组通知，并保存初始化参数。
         *
         * @param groupId 订阅所属群组的本地主键。
         */
        private GroupNotification(long groupId) {
            this.groupId = groupId;
        }

        /**
         * 加入一条数据并维护当前容器。
         *
         * @param memberId 订阅所属群成员的本地主键。
         * @param matches 匹配集合。
         */
        private void add(long memberId, List<WatchCandidate> matches) {
            memberIds.add(memberId);
            matches.forEach(candidate -> candidates.putIfAbsent(
                    candidate.stableKey(), candidate));
        }

        /**
         * 读取本批通知所属群组的本地主键。
         * @return 当前操作计算或读取的数值。
         */
        private long groupId() {
            return groupId;
        }

        /**
         * 读取本批通知需要提醒的群成员主键集合。
         * @return 按当前条件筛选或转换后的数据集合。
         */
        private Set<Long> memberIds() {
            return memberIds;
        }

        /**
         * 组装当前事件对应的通知消息。
         *
         * @return 组装当前事件对应的通知消息对应的文本。
         */
        private String message() {
            return "Warframe 蹲任务命中\n" + candidates.values().stream()
                    .map(WatchCandidate::messageLine)
                    .collect(Collectors.joining("\n"));
        }
    }
}
