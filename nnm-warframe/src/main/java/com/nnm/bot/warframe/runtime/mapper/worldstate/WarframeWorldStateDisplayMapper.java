package com.nnm.bot.warframe.runtime.mapper.worldstate;

import cn.hutool.core.text.CharSequenceUtil;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WarframeWorldStateUnresolvedReferenceDTO;
import com.nnm.bot.warframe.runtime.constant.WarframeBountySupplementStatus;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.AlertVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.ArbitrationMissionVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.ArchimedeaVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.ArchimedeaMissionVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.BountyVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.SupplementalBountyVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.BountyRewardPoolVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.CalendarDayVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.CalendarEventVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.ChallengeVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.CircuitRewardVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.ClanInitiativeVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.ClanRewardVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.ConstructionVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.CycleVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.DealVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.FlashSaleVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.InvasionVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.JobVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.MissionVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.NewsVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.OfferingVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.OutpostVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.DescendiaFloorVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.DescendiaWeekVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.PersistentEnemyVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.ProductVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.RewardVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.SortieVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.TraderVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.VariantVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.VaultProductVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.VaultTraderVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.WorldStateEntryVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.WorldTimestampVO;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 在引用解析完成后按命令选择展示字段；不改变管理端、订阅匹配使用的完整内部投影。 仲裁图片预热也调用此入口，防止缓存图片与实时文字使用不同的数据协议。
 */
public final class WarframeWorldStateDisplayMapper {
    private static final Pattern INTERNAL_REWARD_REFERENCE = Pattern.compile("/[^,，\\s]+");

    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeWorldStateDisplayMapper() {}

    /**
     * 将当前动态数据转换为玩家可见内容，未命中的引用保留原值；未完全解析时附带待确认状态。
     *
     * @param command 命令。
     * @param data 已解析的当前动态数据。
     * @return 世界状态的玩家展示内容。
     */
    public static WarframeWorldStateVO from(String command, Map<String, Object> data) {
        return from(command, data, List.of());
    }

    /**
     * 在保留字段级未完成状态的同时转换玩家命令展示数据。
     *
     * @param command 命令。
     * @param data 最终解析投影。
     * @param unresolved 静态引用解析保留的审核记录。
     * @return 世界状态的玩家展示内容。
     */
    public static WarframeWorldStateVO from(String command, Map<String, Object> data,
        List<WarframeWorldStateUnresolvedReferenceDTO> unresolved) {
        Map<String, Object> cleanData = sanitize(data);
        List<Map<?, ?>> rows = rows(cleanData.get("items"));
        List<?> items = switch (command) {
            case "alerts" -> map(rows,
                row -> new AlertVO(readable(text(row, "node"), "未知节点"), readable(text(row, "mission"), "未知任务"),
                    readable(text(row, "faction"), "未知阵营"), level(row), reward(row, "reward", "rewardItems"),
                    text(row, "expiry"), text(row, "remaining")));
            case "sorties", "archon-hunts" -> map(rows,
                row -> new SortieVO(readable(text(row, "boss"), "未知目标"), text(row, "expiry"), text(row, "remaining"),
                    map(rows(row.get("variants")), variant -> new VariantVO(readable(text(variant, "node"), "未知节点"),
                        readable(text(variant, "missionType"), "未知任务"), readable(text(variant, "modifier"), "未知条件")))));
            case "arbitration", "arbitration-table", "today-arbitration",
                "efficient-arbitration" -> map(rows,
                    row -> new ArbitrationMissionVO(readable(text(row, "node"), "未知节点"), text(row, "nodeReference"),
                        readable(first(text(row, "missionType"), text(row, "type")), "未知任务"),
                        readable(text(row, "faction"), null), text(row, "essenceAmount"), text(row, "efficiencyGrade"),
                        text(row, "essence"), text(row, "activation"), text(row, "expiry")));
            case "fissures", "steel-fissures",
                "void-storms" -> map(rows,
                    row -> new MissionVO(readable(text(row, "node"), "未知节点"),
                        readable(first(text(row, "missionType"), text(row, "type")), "未知任务"),
                        readable(text(row, "tier"), "未知裂缝"), readable(text(row, "essence"), null), text(row, "expiry"),
                        text(row, "remaining")));
            case "cycles", "duviri-cycle" -> map(rows.stream()
                .filter(row -> !command.equals("duviri-cycle") || "双衍王境".equals(text(row, "name"))).toList(), row -> {
                    Map<?, ?> cycle = row.get("value") instanceof Map<?, ?> value ? value : Map.of();
                    return new CycleVO(readable(text(row, "name"), "未知区域"),
                        readable(first(text(cycle, "value"), text(row, "value")), "未知状态"), text(cycle, "expiry"),
                        text(cycle, "remaining"));
                });
            case "void-trader" -> map(rows, row -> new TraderVO(readable(text(row, "location"), "未知地点"),
                Boolean.TRUE.equals(row.get("active")) ? "已到达" : "未到达", text(row, "activation"), text(row, "expiry"),
                map(rows(row.get("inventory")), product -> new ProductVO(readable(text(product, "item"), "未知物品"),
                    text(product, "ducats"), text(product, "credits")))));
            case "daily-deals" -> map(rows, row -> new DealVO(readable(text(row, "item"), "未知物品"), text(row, "credits"),
                text(row, "platinum"), text(row, "stock"), text(row, "sold"), text(row, "remaining")));
            case "invasions" -> map(rows.stream().filter(row -> !Boolean.TRUE.equals(row.get("completed"))).toList(),
                row -> new InvasionVO(readable(text(row, "node"), "未知节点"), text(row, "completion"),
                    reward(row, "attackerReward", "attackerRewards"), reward(row, "defenderReward", "defenderRewards"),
                    String.join("、", values(row.get("attackerMissions")).stream().map(Object::toString).toList()),
                    String.join("、", values(row.get("defenderMissions")).stream().map(Object::toString).toList())));
            case "steel-path" -> map(rows,
                row -> new OfferingVO(text(row, "node"),
                    readable(first(text(row, "reward"), text(row, "item")), "未知奖励"), text(row, "price"),
                    text(row, "offeringStatus"), text(row, "expiry"), text(row, "remaining")));
            case "ostrons", "solaris", "entrati", "cavia", "holdfasts", "hex-bounties", "syndicate-missions" -> map(
                rows,
                row -> new BountyVO(readable(text(row, "syndicate"), "未知集团"), nodes(row), text(row, "expiry"),
                    text(row, "remaining"), map(rows(row.get("jobs")), WarframeWorldStateDisplayMapper::job),
                    map(rows(row.get("supplementalJobs")),
                        job -> new SupplementalBountyVO(readable(text(job, "nodeName"), "未收录节点"),
                            readable(text(job, "missionType"), "未收录任务"), readable(text(job, "challengeName"), "未收录条件"),
                            readable(text(job, "challengeDescription"), null), readable(text(job, "allyName"), null))),
                    map(rows(row.get("staticRewardPools")), pool -> new BountyRewardPoolVO(
                        readable(text(pool, "name"), "静态赏金池"),
                        rows(pool.get("rewards")).stream()
                            .map(reward -> readable(text(reward, "displayName"), text(reward, "itemKey"))).toList())),
                    supplementNotice(text(row, "supplementStatus"))));
            case "nightwave" -> map(rows,
                row -> new ChallengeVO(readable(text(row, "title"), "未命名任务"),
                    readable(text(row, "description"), "暂无任务说明"), text(row, "reputation"),
                    Boolean.TRUE.equals(row.get("daily")) ? "每日" : "每周"));
            case "news" ->
                map(rows, row -> new NewsVO(text(row, "title"), text(row, "link"), text(row, "publishedAt")));
            case "construction-progress" ->
                map(rows, row -> new ConstructionVO(text(row, "name"), text(row, "progress")));
            case "sentient-outposts" -> map(rows,
                row -> new OutpostVO(readable(text(row, "node"), "未知节点"), text(row, "systemName"),
                    text(row, "baseMissionType"), text(row, "baseFaction"), text(row, "activation"),
                    text(row, "expiry"), text(row, "remaining"), text(row, "windowSource"), text(row, "description")));
            case "clan-initiative" -> map(rows,
                row -> new ClanInitiativeVO(text(row, "week"), readable(text(row, "location"), "未知区域"),
                    map(rows(row.get("rewards")), reward -> new ClanRewardVO(readable(text(reward, "item"), "未知奖励"),
                        text(reward, "count"), text(reward, "points"), text(reward, "claimed")))));
            case "circuit-normal", "circuit-hard" ->
                map(rows, row -> new CircuitRewardVO(readable(text(row, "item"), "未知选项")));
            case "calendar" -> map(rows, row -> new CalendarDayVO(text(row, "date"), calendarEvents(row)));
            case "vault-trader" -> map(rows,
                row -> new VaultTraderVO(readable(text(row, "location"), "未知地点"), text(row, "activation"),
                    text(row, "expiry"),
                    map(rows(row.get("inventory")),
                        product -> new VaultProductVO(readable(text(product, "item"), "未知物品"),
                            text(product, "primePrice"), text(product, "regularPrice")))));
            case "timestamp" -> map(rows, row -> new WorldTimestampVO(text(row, "timestamp")));
            case "flash-sales" -> map(rows,
                row -> new FlashSaleVO(readable(text(row, "item"), "未知物品"), text(row, "discount"),
                    text(row, "regularOverride"), text(row, "premiumOverride"), text(row, "expiry"),
                    text(row, "remaining")));
            case "persistent-enemies" -> map(rows,
                row -> new PersistentEnemyVO(readable(text(row, "agentType"), "未知敌人"), text(row, "rank"),
                    text(row, "healthPercent"),
                    readable(first(text(row, "lastDiscoveredLocation"), text(row, "locationTag")), "未知地点"),
                    text(row, "lastDiscoveredTime"), row.get("discovered") instanceof Boolean value ? value : null));
            case "archimedeas", "deep-archimedeas",
                "hex-archimedeas" -> map(rows,
                    row -> new ArchimedeaVO(readable(first(text(row, "type"), text(row, "typeKey")), "未知科研"),
                        text(row, "expiry"), text(row, "remaining"),
                        values(row.get("personalModifiers")).stream()
                            .map(value -> readable(String.valueOf(value), "未知词缀")).toList(),
                        map(rows(row.get("missions")),
                            mission -> new ArchimedeaMissionVO(readable(text(mission, "faction"), "未知阵营"),
                                readable(first(text(mission, "missionType"), text(mission, "missionTypeKey")), "未知任务"),
                                readable(text(mission, "deviation"), "未知偏差"), values(mission.get("risks")).stream()
                                    .map(value -> readable(String.valueOf(value), "未知风险")).toList()))));
            case "descents" -> map(rows,
                row -> new DescendiaWeekVO(text(row, "expiry"), text(row, "rewardStatus"),
                    map(rows(row.get("challenges")),
                        challenge -> new DescendiaFloorVO(text(challenge, "index"), text(challenge, "challenge"),
                            text(challenge, "type"), descendiaReward(challenge.get("normalReward")),
                            descendiaReward(challenge.get("steelPathReward"))))));
            case "events" ->
                map(rows,
                    row -> new WorldStateEntryVO(readable(text(row, "title"), "未命名活动"),
                        readable(text(row, "description"), null), readable(text(row, "node"), null),
                        text(row, "progress"), text(row, "goal"),
                        map(rows(row.get("rewards")), reward -> new RewardVO(readable(text(reward, "item"), "未知奖励"),
                            text(reward, "count"), null, null, null)),
                        null, text(row, "expiry"), text(row, "remaining")));
            case "global-upgrades" -> map(rows, row -> new WorldStateEntryVO(readable(text(row, "upgradeType"), "未知增益"),
                readable(text(row, "operationType"), null), null, null, null, List.of(),
                first(text(row, "formattedValue"), text(row, "value")), text(row, "expiry"), text(row, "remaining")));
            default -> throw new IllegalArgumentException("Missing world state display: " + command);
        };
        return new WarframeWorldStateVO(items,
            first(text(cleanData, "notice"), Boolean.TRUE.equals(cleanData.get("stale")) ? "上游刷新暂未成功，当前为缓存数据" : null),
            WarframeWorldStateResolutionMapper.pending(cleanData, unresolved));
    }

    /**
     * 将同日挑战与互斥奖励保留为同一个日历日的事件列表。
     *
     * @param day 已解析的游戏内日期。
     * @return 当日有效事件；没有原始内部物品键的假译名。
     */
    private static List<CalendarEventVO> calendarEvents(Map<?, ?> day) {
        List<CalendarEventVO> events = new java.util.ArrayList<>();
        for (Map<?, ?> challenge : rows(day.get("challenges"))) {
            String title = text(challenge, "title");
            if ("未收录数据".equals(title))
                title = null;
            events.add(new CalendarEventVO(readable(first(title, text(challenge, "challenge")), "未收录挑战"),
                text(challenge, "description"), null));
        }
        for (Map<?, ?> reward : rows(day.get("rewards")))
            events.add(
                new CalendarEventVO(null, text(reward, "description"), readable(text(reward, "item"), "奖励名称尚未收录")));
        return List.copyOf(events);
    }

    /**
     * 根据赏金补充状态生成中文提示，避免把未合并任务当作官方结果。
     *
     * @param status 状态。
     * @return 补充提示对应的文本。
     */
    private static String supplementNotice(String status) {
        return WarframeBountySupplementStatus.fromCode(status).notice();
    }

    private static String descendiaReward(Object value) {
        if (!(value instanceof Map<?, ?> group))
            return null;
        boolean guaranteed = Boolean.TRUE.equals(group.get("guaranteed"));
        return rows(group.get("options")).stream()
            .filter(option -> guaranteed || Boolean.TRUE.equals(option.get("confirmed"))).map(option -> {
                String name = text(option, "name");
                String count = text(option, "count");
                return name + (count != null && !"1".equals(count) ? " × " + count : "")
                    + (guaranteed ? "（固定）" : "（人工确认）");
            }).findFirst().orElse(null);
    }

    /**
     * 将已经组装的单条赏金展示字段转换为任务 VO。
     *
     * @param row 当前数据行。
     * @return 单条赏金任务的展示内容。
     */
    private static JobVO job(Map<?, ?> row) {
        List<RewardVO> phaseRewards =
            rows(row.get("stages")).stream()
                .flatMap(stage -> rows(stage.get("rewards")).stream()
                    .map(reward -> bountyReward(reward, first(text(stage, "stageLabel"), text(stage, "stage")))))
                .toList();
        var rewards = phaseRewards.isEmpty()
            ? map(rows(row.get("rewards")), reward -> bountyReward(reward, text(reward, "stage"))) : phaseRewards;
        return new JobVO(readable(text(row, "type"), "赏金任务"), readable(text(row, "description"), null), level(row),
            text(row, "minMR"), values(row.get("standingStages")), readable(text(row, "locationTag"), "未知区域"),
            text(row, "remaining"), rewards, text(row, "rewardRotation"), readable(text(row, "rewardNotice"), null));
    }

    private static RewardVO bountyReward(Map<?, ?> reward, String phase) {
        return new RewardVO(first(text(reward, "displayName"), readable(text(reward, "item"), "未知奖励")),
            text(reward, "count"), rewardChance(reward), phase, readable(text(reward, "rarity"), null));
    }

    /**
     * 格式化已解析奖励的概率，缺省时保留明确提示。
     *
     * @param reward 奖励。
     * @return 奖励几率对应的文本。
     */
    private static String rewardChance(Map<?, ?> reward) {
        Object probability = reward.get("probability");
        return probability instanceof Number number
            ? new BigDecimal(number.toString()).movePointRight(2).stripTrailingZeros().toPlainString()
            : text(reward, "chance");
    }

    /**
     * 从已组装的展示条目中提取节点名称列表。
     *
     * @param row 当前数据行。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> nodes(Map<?, ?> row) {
        if (row.get("missions") instanceof List<?> missions && !missions.isEmpty()) {
            return rows(missions).stream().map(mission -> {
                String node = first(text(mission, "node"), text(mission, "nodeKey"));
                String system = text(mission, "systemName");
                String type = text(mission, "baseMissionType");
                return Stream.of(node, system, type == null ? null : type + "（星图）").filter(Objects::nonNull)
                    .collect(Collectors.joining(" · "));
            }).toList();
        }
        List<String> nodes = values(row.get("nodes")).stream().filter(Objects::nonNull).map(Object::toString)
            .map(value -> readable(value, null)).filter(Objects::nonNull).distinct().toList();
        String node = readable(text(row, "node"), null);
        return CollUtil.isEmpty(nodes) && node != null ? List.of(node) : nodes;
    }

    /**
     * 按奖励摘要与奖励条目生成统一的展示文本。
     *
     * @param row 当前数据行。
     * @param summary 摘要。
     * @param items 当前物品或成员列表。
     * @return 奖励对应的文本。
     */
    private static String reward(Map<?, ?> row, String summary, String items) {
        // 已解析的奖励摘要保留数量和现金，避免同时重复展示 reward 与 rewardItems。
        String summaryText = readable(text(row, summary), null);
        List<String> itemNames =
            (row.get(items) instanceof List<?> list ? list : List.of()).stream().filter(Objects::nonNull).map(value -> {
                if (value instanceof Map<?, ?> entry) {
                    String name = readable(text(entry, "item"), "未知奖励");
                    String count = text(entry, "count");
                    return name + (count == null ? "" : " x" + count);
                }
                return readable(value.toString(), "未知奖励");
            }).filter(Objects::nonNull).distinct().toList();
        // 部分物品仍是原始键时，不能按顺序把已解析名称错配给未解析奖励。
        if (summaryText != null && itemNames.size() == 1 && !isInternalReference(itemNames.getFirst())
            && INTERNAL_REWARD_REFERENCE.matcher(summaryText).results().count() == 1) {
            summaryText = WarframeRewardSummaryFormatter.replaceInternalReferences(summaryText, itemNames);
        }
        String itemText = String.join("、", itemNames);
        if (summaryText != null && INTERNAL_REWARD_REFERENCE.matcher(summaryText).find()) {
            String currentSummary = summaryText;
            List<String> parsedNames = itemNames.stream().filter(name -> !isInternalReference(name))
                .filter(name -> !currentSummary.contains(name.replaceFirst(" x\\d+$", ""))).toList();
            if (!parsedNames.isEmpty())
                return summaryText + "；已解析物品：" + String.join("、", parsedNames);
        }
        return first(summaryText, itemText);
    }

    /**
     * 将任务的最低与最高敌人等级格式化为展示范围。
     *
     * @param row 当前数据行。
     * @return 等级对应的文本。
     */
    private static String level(Map<?, ?> row) {
        String min = text(row, "minEnemyLevel");
        String max = text(row, "maxEnemyLevel");
        return min != null && max != null ? min + "-" + max : first(min, max);
    }

    /**
     * 按既定顺序取得首个有效值。
     *
     * @param first 优先使用的首个值。
     * @param second 首个值不可用时使用的后续值。
     * @return 按约定优先顺序取得的首个有效文本；均缺省时为空。
     */
    private static String first(String first, String second) {
        return CharSequenceUtil.isBlank(first) ? second : first;
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @param row 当前数据行。
     * @param key 当前记录或字段的查找键。
     * @return 按当前对象字段组成的文本内容。
     */
    private static String text(Map<?, ?> row, String key) {
        Object value = row.get(key);
        return value == null || CharSequenceUtil.isBlank(value.toString()) ? null : value.toString();
    }

    /**
     * 将已解析数据转换为可读的展示内容。
     *
     * @param value 待校验或规范化的文本。
     * @param fallback 缺省。
     * @return 将已解析数据转换为可读的展示内容对应的文本。
     */
    private static String readable(String value, String fallback) {
        if (CharSequenceUtil.isBlank(value)) {
            return fallback;
        }
        return CharSequenceUtil.trim(value);
    }

    /**
     * 清理玩家展示数据的游戏颜色标记，并以已解析物品替换奖励摘要内的内部键。
     *
     * @param data 已解析的动态数据。
     * @return 清理后的展示数据。
     */
    private static Map<String, Object> sanitize(Map<String, Object> data) {
        if (CollUtil.isEmpty(data))
            return data == null ? Map.of() : data;
        Map<String, Object> result = new LinkedHashMap<>();
        data.forEach((key, value) -> result.put(key, sanitizeValue(value, key)));
        return result;
    }

    /**
     * 递归处理展示字段；未命中的原始引用仍作为字段值展示。
     *
     * @param value 展示字段值。
     * @param field 字段名。
     * @return 清理后的展示字段值。
     */
    private static Object sanitizeValue(Object value, String field) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new LinkedHashMap<>();
            map.forEach((key, entry) -> {
                String name = String.valueOf(key);
                result.put(name, sanitizeValue(resolvedRewardSummary(map, name, entry), name));
            });
            return result;
        }
        if (value instanceof List<?> list)
            return list.stream().map(entry -> sanitizeValue(entry, field)).toList();
        return value instanceof String text ? WarframeWorldStateMarkupCleaner.clean(text) : value;
    }

    /**
     * 当奖励条目已有可读名称时，优先使用同条记录中已解析物品替换奖励摘要内的内部键。
     *
     * @param row 同一条任务的全部展示字段。
     * @param field 当前字段名。
     * @param value 当前字段值。
     * @return 已解析的摘要或原值。
     */
    private static Object resolvedRewardSummary(Map<?, ?> row, String field, Object value) {
        if (!(value instanceof String text) || !("reward".equals(field) || field.endsWith("Reward"))
            || !INTERNAL_REWARD_REFERENCE.matcher(text).find())
            return value;
        String itemsField = "reward".equals(field) ? "rewardItems" : field + "s";
        List<?> items = values(row.get(itemsField));
        List<String> names = items.stream().map(
            item -> item instanceof Map<?, ?> product ? text(product, "item") : item instanceof String raw ? raw : null)
            .toList();
        // 仅单个结构化物品与单个内部键一一对应时，才允许按顺序替换。
        if (names.size() != 1
            || names.stream().anyMatch(name -> CharSequenceUtil.isBlank(name) || isInternalReference(name))
            || INTERNAL_REWARD_REFERENCE.matcher(text).results().count() != 1)
            return value;
        return WarframeRewardSummaryFormatter.replaceInternalReferences(text, names);
    }

    /**
     * 判断输入是否为游戏内部资源引用。
     *
     * @param value 待校验或规范化的文本。
     * @return 满足输入是否为游戏内部资源引用时为真，否则为假。
     */
    private static boolean isInternalReference(String value) {
        String normalized = value == null ? "" : CharSequenceUtil.trim(value);
        return normalized.startsWith("/") || normalized.startsWith("MT_") || normalized.startsWith("SORTIE_")
            || normalized.startsWith("VoidT") || normalized.startsWith("OSTRONS_") || normalized.startsWith("SOLARIS_")
            || normalized.startsWith("ENTRATI_") || normalized.startsWith("Narmer") || normalized.startsWith("PVP")
            || normalized.endsWith("Reference") || normalized.matches("[A-Z][A-Z0-9_]+");
    }

    /**
     * 取得可安全遍历的数据集合。
     *
     * @param value 待处理的字段值。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<?> values(Object value) {
        return value instanceof List<?> list ? list : List.of();
    }

    /**
     * 从已组装的展示值中提取可逐行转换的字段映射列表。
     *
     * @param value 待处理的字段值。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<Map<?, ?>> rows(Object value) {
        return values(value).stream().filter(Map.class::isInstance).<Map<?, ?>>map(item -> (Map<?, ?>)item).toList();
    }

    /**
     * 将输入数据映射为当前业务所需的数据对象。
     *
     * @param rows 当前待处理的数据行。
     * @param mapper 组件已经配置的 Jackson 映射器。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static <T> List<T> map(List<Map<?, ?>> rows, Function<Map<?, ?>, T> mapper) {
        return rows.stream().map(mapper).toList();
    }
}
