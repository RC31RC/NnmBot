package com.nnm.bot.warframe.runtime.mapper.worldstate;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO;
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
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.ConclaveVO;
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
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.PersistentEnemyVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.ProductVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.RewardVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.SimarisTargetVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.SortieVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.TraderVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.UnresolvedReferenceVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.VariantVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.VaultProductVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.VaultTraderVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.WorldStateEntryVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.WorldTimestampVO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 在引用解析完成后按命令选择展示字段；不改变管理端、订阅匹配使用的完整内部投影。
 * 仲裁图片预热也调用此入口，防止缓存图片与实时文字使用不同的数据协议。
 */
public final class WarframeWorldStateDisplayMapper {
    private static final Pattern INTERNAL_REWARD_REFERENCE =
            Pattern.compile("/[^,，\\s]+");

    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeWorldStateDisplayMapper() {}

    /**
     * 将输入对象转换为当前业务数据对象。
     *
     * @param command 命令。
     * @param data 当前操作的数据。
     * @return 世界状态的页面展示内容。
     */
    public static WarframeWorldStateVO from(String command, Map<String, Object> data) {
        List<UnresolvedReferenceVO> unresolved = new ArrayList<>();
        Map<String, Object> cleanData = sanitize(data, command, "", unresolved);
        return from(command, cleanData, unresolved);
    }

    /**
     * 将输入对象转换为当前业务数据对象。
     *
     * @param command 命令。
     * @param data 当前操作的数据。
     * @param unresolvedReferences 未解析引用集合。
     * @return 世界状态的页面展示内容。
     */
    public static WarframeWorldStateVO from(
            String command,
            Map<String, Object> data,
            List<UnresolvedReferenceVO> unresolvedReferences) {
        List<UnresolvedReferenceVO> unresolved = new ArrayList<>(
                unresolvedReferences == null ? List.of() : unresolvedReferences);
        Map<String, Object> cleanData = sanitize(data, command, "", unresolved);
        List<Map<?, ?>> rows = rows(cleanData.get("items"));
        List<?> items = switch (command) {
            case "alerts" -> map(rows, row -> new AlertVO(readable(text(row, "node"), "未知节点"),
                    readable(text(row, "mission"), "未知任务"),
                    readable(text(row, "faction"), "未知阵营"), level(row), reward(row, "reward", "rewardItems"),
                    text(row, "expiry"), text(row, "remaining")));
            case "sorties", "archon-hunts" -> map(rows, row -> new SortieVO(
                    readable(text(row, "boss"), "未知目标"),
                    text(row, "expiry"), text(row, "remaining"),
                    map(rows(row.get("variants")), variant -> new VariantVO(
                            readable(text(variant, "node"), "未知节点"),
                            readable(text(variant, "missionType"), "未知任务"),
                            readable(text(variant, "modifier"), "未知条件")))));
            case "arbitration", "arbitration-table", "today-arbitration", "efficient-arbitration" ->
                    map(rows, row -> new ArbitrationMissionVO(readable(text(row, "node"), "未知节点"),
                            readable(first(text(row, "missionType"), text(row, "type")), "未知任务"),
                            readable(text(row, "faction"), null), text(row, "essence"),
                            text(row, "activation"), text(row, "expiry")));
            case "fissures", "steel-fissures", "void-storms" ->
                    map(rows, row -> new MissionVO(readable(text(row, "node"), "未知节点"),
                            readable(first(text(row, "missionType"), text(row, "type")), "未知任务"),
                            readable(text(row, "tier"), "未知裂缝"),
                            readable(text(row, "essence"), null),
                            text(row, "expiry"), text(row, "remaining")));
            case "cycles", "duviri-cycle" -> map(rows.stream()
                    .filter(row -> !command.equals("duviri-cycle")
                            || "双衍王境".equals(text(row, "name"))).toList(), row -> {
                        Map<?, ?> cycle = row.get("value") instanceof Map<?, ?> value ? value : Map.of();
                return new CycleVO(readable(text(row, "name"), "未知区域"),
                        readable(first(text(cycle, "value"), text(row, "value")), "未知状态"),
                                text(cycle, "expiry"), text(cycle, "remaining"));
                    });
            case "void-trader" -> map(rows, row -> new TraderVO(readable(text(row, "location"), "未知地点"),
                    Boolean.TRUE.equals(row.get("active")) ? "已到达" : "未到达",
                    text(row, "activation"), text(row, "expiry"),
                    map(rows(row.get("inventory")), product -> new ProductVO(
                            readable(text(product, "item"), "未知物品"),
                            text(product, "ducats"), text(product, "credits")))));
            case "daily-deals" -> map(rows, row -> new DealVO(readable(text(row, "item"), "未知物品"),
                    text(row, "credits"),
                    text(row, "platinum"), text(row, "stock"), text(row, "sold"), text(row, "remaining")));
            case "invasions" -> map(rows.stream().filter(row ->
                    !Boolean.TRUE.equals(row.get("completed"))).toList(), row -> new InvasionVO(
                    readable(text(row, "node"), "未知节点"), text(row, "completion"),
                    reward(row, "attackerReward", "attackerRewards"),
                    reward(row, "defenderReward", "defenderRewards")));
            case "steel-path" -> map(rows, row -> new OfferingVO(readable(text(row, "node"), "未知节点"),
                    readable(text(row, "reward"), "未知奖励"),
                    text(row, "expiry"), text(row, "remaining")));
            case "ostrons", "solaris", "entrati", "cavia", "holdfasts", "hex-bounties",
                    "syndicate-missions" -> map(rows, row -> new BountyVO(
                    readable(text(row, "syndicate"), "未知集团"),
                    nodes(row), text(row, "expiry"), text(row, "remaining"),
                    map(rows(row.get("jobs")), WarframeWorldStateDisplayMapper::job),
                    map(rows(row.get("supplementalJobs")), job -> new SupplementalBountyVO(
                            readable(text(job, "nodeName"), "未收录节点"),
                            readable(text(job, "missionType"), "未收录任务"),
                            readable(text(job, "challengeName"), "未收录条件"),
                            readable(text(job, "challengeDescription"), null),
                            readable(text(job, "allyName"), null))),
                    map(rows(row.get("staticRewardPools")), pool -> new BountyRewardPoolVO(
                            readable(text(pool, "name"), "静态赏金池"),
                            values(pool.get("rewards")).stream().map(value -> readable(String.valueOf(value), "未收录奖励")).toList())),
                    supplementNotice(text(row, "supplementStatus"))));
            case "nightwave" -> map(rows, row -> new ChallengeVO(readable(text(row, "title"), "未命名任务"),
                    readable(text(row, "description"), "暂无任务说明"),
                    text(row, "reputation"), Boolean.TRUE.equals(row.get("daily")) ? "每日" : "每周"));
            case "conclave" -> map(rows, row -> new ConclaveVO(
                    readable(text(row, "title"), "未命名挑战"),
                    readable(text(row, "description"), "暂无挑战说明"),
                    readable(text(row, "mode"), "未知模式"),
                    readable(text(row, "category"), Boolean.TRUE.equals(row.get("daily")) ? "每日" : "每周"),
                    text(row, "amount"), text(row, "standing"),
                    text(row, "expiry"), text(row, "remaining")));
            case "news" -> map(rows, row -> new NewsVO(text(row, "title"),
                    text(row, "link"), text(row, "publishedAt")));
            case "construction-progress" -> map(rows, row ->
                    new ConstructionVO(text(row, "name"), text(row, "progress")));
            case "simaris" -> map(rows, row -> new SimarisTargetVO(
                    readable(text(row, "title"), "未提供目标"),
                    Boolean.TRUE.equals(row.get("active")) ? "研究进行中" : "上次完成的公共研究"));
            case "sentient-outposts" -> map(rows, row -> new OutpostVO(
                    readable(text(row, "node"), "未知节点"), text(row, "activation"),
                    text(row, "expiry"), text(row, "remaining")));
            case "clan-initiative" -> map(rows, row -> new ClanInitiativeVO(
                    text(row, "week"), readable(text(row, "location"), "未知区域"),
                    map(rows(row.get("rewards")), reward -> new ClanRewardVO(
                            readable(text(reward, "item"), "未知奖励"), text(reward, "count"),
                            text(reward, "points"), text(reward, "claimed")))));
            case "circuit-normal", "circuit-hard" -> map(rows, row ->
                    new CircuitRewardVO(readable(text(row, "item"), "未知选项")));
            case "calendar" -> map(rows, row -> new CalendarDayVO(text(row, "date"),
                    text(row, "iteration"), map(rows(row.get("events")), event -> new CalendarEventVO(
                            readable(text(event, "title"), null), readable(text(event, "description"), null),
                            readable(text(event, "reward"), null),
                            readable(text(event, "dialogueName"), null)))));
            case "vault-trader" -> map(rows, row -> new VaultTraderVO(
                    readable(text(row, "location"), "未知地点"), text(row, "activation"), text(row, "expiry"),
                    map(rows(row.get("inventory")), product -> new VaultProductVO(
                            readable(text(product, "item"), "未知物品"), text(product, "primePrice"),
                            text(product, "regularPrice")))));
            case "timestamp" -> map(rows, row -> new WorldTimestampVO(text(row, "timestamp")));
            case "flash-sales" -> map(rows, row -> new FlashSaleVO(
                    readable(text(row, "item"), "未知物品"), text(row, "discount"),
                    text(row, "regularOverride"), text(row, "premiumOverride"),
                    text(row, "expiry"), text(row, "remaining")));
            case "persistent-enemies" -> map(rows, row -> new PersistentEnemyVO(
                    readable(text(row, "agentType"), "未知敌人"), text(row, "rank"),
                    text(row, "healthPercent"), readable(first(text(row, "lastDiscoveredLocation"),
                            text(row, "locationTag")), "未知地点"), text(row, "lastDiscoveredTime"),
                    row.get("discovered") instanceof Boolean value ? value : null));
            case "archimedeas", "deep-archimedeas", "hex-archimedeas" ->
                    map(rows, row -> new ArchimedeaVO(
                            readable(first(text(row, "type"), text(row, "typeKey")), "未知科研"),
                            text(row, "expiry"), text(row, "remaining"),
                            values(row.get("personalModifiers")).stream()
                                    .map(value -> readable(String.valueOf(value), "未知词缀")).toList(),
                            map(rows(row.get("missions")), mission -> new ArchimedeaMissionVO(
                                    readable(text(mission, "faction"), "未知阵营"),
                                    readable(first(text(mission, "missionType"),
                                            text(mission, "missionTypeKey")), "未知任务"),
                                    readable(text(mission, "deviation"), "未知偏差"),
                                            values(mission.get("risks")).stream()
                                    .map(value -> readable(String.valueOf(value), "未知风险")).toList()))));
            case "descents" -> map(rows, row -> new WorldStateEntryVO(
                    "深层下降",
                    "种子：" + readable(text(row, "seed"), "未知"),
                    null, null, null,
                    map(rows(row.get("challenges")), challenge -> new RewardVO(
                            readable(text(challenge, "challenge"), "未知挑战"),
                            text(challenge, "index"), null, null, null)),
                    null, null, null));
            case "events" -> map(rows, row -> new WorldStateEntryVO(
                    readable(text(row, "title"), "未命名活动"),
                    readable(text(row, "description"), null),
                    readable(text(row, "node"), null),
                    text(row, "progress"),
                    text(row, "goal"),
                    map(rows(row.get("rewards")), reward -> new RewardVO(
                            readable(text(reward, "item"), "未知奖励"),
                            text(reward, "count"), null, null, null)),
                    null, text(row, "expiry"), text(row, "remaining")));
            case "global-upgrades" -> map(rows, row -> new WorldStateEntryVO(
                    readable(text(row, "upgradeType"), "未知增益"),
                    readable(text(row, "operationType"), null),
                    null, null, null, List.of(),
                    text(row, "value"), text(row, "expiry"), text(row, "remaining")));
            default -> throw new IllegalArgumentException("Missing world state display: " + command);
        };
        return new WarframeWorldStateVO(items,
                first(text(cleanData, "notice"), Boolean.TRUE.equals(cleanData.get("stale"))
                        ? "上游刷新暂未成功，当前为缓存数据" : null),
                distinct(unresolved));
    }

    /**
     * 根据赏金补充状态生成中文提示，避免把未合并任务当作官方结果。
     * @param status 状态。
     * @return 补充提示对应的文本。
     */
    private static String supplementNotice(String status) {
        return switch (status == null ? "" : status) {
            case "expiry_matches_official_php" -> "社区任务轮次与官方一致；候选奖励不代表本轮必得奖励";
            case "expiry_mismatch_not_merged" -> "社区任务轮次与官方不符，仅展示官方字段和静态候选池";
            case "unavailable", "invalid_response" -> "社区任务暂不可用，保留官方字段和静态候选池";
            case "disabled" -> "社区任务补充已关闭";
            default -> null;
        };
    }

    /**
     * 将已经组装的单条赏金展示字段转换为任务 VO。
     * @param row 当前数据行。
     * @return 单条赏金任务的展示内容。
     */
    private static JobVO job(Map<?, ?> row) {
        return new JobVO(readable(text(row, "type"), "赏金任务"),
                readable(text(row, "description"), null), level(row), text(row, "minMR"),
                values(row.get("standingStages")),
                readable(text(row, "locationTag"), "未知区域"), text(row, "remaining"),
                map(rows(row.get("rewards")), reward -> new RewardVO(
                        first(text(reward, "displayName"), readable(text(reward, "item"), "未知奖励")),
                        text(reward, "count"), rewardChance(reward),
                        text(reward, "stage"), readable(text(reward, "rarity"), null))),
                readable(text(row, "rewardNotice"), null));
    }

    /**
     * 格式化已解析奖励的概率，缺省时保留明确提示。
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
     * @param row 当前数据行。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> nodes(Map<?, ?> row) {
        List<String> nodes = values(row.get("nodes")).stream().filter(Objects::nonNull)
                .map(Object::toString).map(value -> readable(value, null))
                .filter(Objects::nonNull).distinct().toList();
        String node = readable(text(row, "node"), null);
        return CollUtil.isEmpty(nodes) && node != null ? List.of(node) : nodes;
    }

    /**
     * 按奖励摘要与奖励条目生成统一的展示文本。
     * @param row 当前数据行。
     * @param summary 摘要。
     * @param items 当前物品或成员列表。
     * @return 奖励对应的文本。
     */
    private static String reward(Map<?, ?> row, String summary, String items) {
        // 已解析的奖励摘要保留数量和现金，避免同时重复展示 reward 与 rewardItems。
        String summaryText = readable(text(row, summary), null);
        List<String> itemNames = (row.get(items) instanceof List<?> list ? list : List.of()).stream()
                .filter(Objects::nonNull).map(value -> {
                    if (value instanceof Map<?, ?> entry) {
                        String name = readable(text(entry, "item"), "未知奖励");
                        String count = text(entry, "count");
                        return name + (count == null ? "" : " x" + count);
                    }
                    return readable(value.toString(), "未知奖励");
                })
                .filter(Objects::nonNull).distinct().toList();
        if (summaryText != null && CollUtil.isNotEmpty(itemNames)) {
            summaryText = replaceInternalRewardReferences(summaryText, itemNames);
        }
        String itemText = String.join("、", itemNames);
        return first(summaryText, itemText);
    }

    /**
     * 将任务的最低与最高敌人等级格式化为展示范围。
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
        return StrUtil.isBlank(first) ? second : first;
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
        return value == null || StrUtil.isBlank(value.toString()) ? null : value.toString();
    }

    /**
     * 将已解析数据转换为可读的展示内容。
     *
     * @param value 待校验或规范化的文本。
     * @param fallback 缺省。
     * @return 将已解析数据转换为可读的展示内容对应的文本。
     */
    private static String readable(String value, String fallback) {
        if (StrUtil.isBlank(value)) {
            return fallback;
        }
        String normalized = value.trim();
        return isInternalReference(normalized) ? fallback : normalized;
    }

    /**
     * 清洗不应暴露给展示调用方的内部字段。
     *
     * @param data 当前操作的数据。
     * @param command 命令。
     * @param path 当前文件或业务资源的路径。
     * @param unresolved 未解析集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private static Map<String, Object> sanitize(
            Map<String, Object> data,
            String command,
            String path,
            List<UnresolvedReferenceVO> unresolved) {
        if (CollUtil.isEmpty(data)) {
            return data == null ? Map.of() : data;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        data.forEach((key, value) -> {
            String childPath = StrUtil.isBlank(path) ? key : path + "." + key;
            result.put(key, sanitizeValue(value, key, childPath, command, unresolved));
        });
        return result;
    }

    /**
     * 递归清洗展示值中的内部字段。
     *
     * @param value 待处理的字段值。
     * @param field 已经声明的字段或字段标识。
     * @param path 当前文件或业务资源的路径。
     * @param command 命令。
     * @param unresolved 未解析集合。
     * @return 按当前字段合同转换的值，保留允许的缺省状态。
     */
    private static Object sanitizeValue(
            Object value,
            String field,
            String path,
            String command,
            List<UnresolvedReferenceVO> unresolved) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new LinkedHashMap<>();
            map.forEach((key, entry) -> {
                String name = String.valueOf(key);
                result.put(name, sanitizeValue(entry, name, path + "." + name,
                        command, unresolved));
            });
            return result;
        }
        if (value instanceof List<?> list) {
            List<Object> result = new ArrayList<>(list.size());
            for (int index = 0; index < list.size(); index++) {
                result.add(sanitizeValue(list.get(index), field,
                        path + "[" + index + "]", command, unresolved));
            }
            return List.copyOf(result);
        }
        if (!(value instanceof String text) || !isReferenceField(field)) {
            return value;
        }
        if (field.toLowerCase(Locale.ROOT).contains("reward")) {
            Matcher matcher = INTERNAL_REWARD_REFERENCE.matcher(text);
            StringBuffer output = new StringBuffer();
            boolean changed = false;
            while (matcher.find()) {
                String reference = matcher.group();
                if (!isInternalReference(reference)) {
                    continue;
                }
                unresolved.add(new UnresolvedReferenceVO(
                        command, path, reference, "未解析", List.of()));
                matcher.appendReplacement(output, "未收录奖励");
                changed = true;
            }
            if (changed) {
                matcher.appendTail(output);
                return output.toString();
            }
        }
        if (isInternalReference(text)
                || ("rewardNotice".equals(field)
                && text.toLowerCase(Locale.ROOT).contains("reference"))) {
            unresolved.add(new UnresolvedReferenceVO(command, path, text, "未解析", List.of()));
            return unresolvedPlaceholder(field);
        }
        return text;
    }

    /**
     * 按未解析字段的业务含义选择中文占位提示。
     * @param field 已经声明的字段或字段标识。
     * @return 未解析占位内容对应的文本。
     */
    private static String unresolvedPlaceholder(String field) {
        String key = field == null ? "" : field.toLowerCase(Locale.ROOT);
        if (key.contains("reward") || key.equals("item")) {
            return "未收录奖励";
        }
        if (key.contains("description")) {
            return "未收录说明";
        }
        if (key.contains("location") || key.equals("node") || key.equals("nodes")) {
            return "未收录地点";
        }
        if (key.contains("mission") || key.equals("type") || key.equals("jobtype")) {
            return "未收录任务";
        }
        if (key.contains("faction")) {
            return "未收录阵营";
        }
        if (key.contains("modifier")) {
            return "未收录条件";
        }
        if (key.equals("tier")) {
            return "未收录纪元";
        }
        if (key.contains("syndicate")) {
            return "未收录集团";
        }
        if (key.equals("boss")) {
            return "未收录目标";
        }
        return "未收录数据";
    }

    /**
     * 判断输入是否为游戏内部资源引用。
     *
     * @param value 待校验或规范化的文本。
     * @return 满足输入是否为游戏内部资源引用时为真，否则为假。
     */
    private static boolean isInternalReference(String value) {
        String normalized = value == null ? "" : value.trim();
        return normalized.startsWith("/")
                || normalized.startsWith("MT_")
                || normalized.startsWith("SORTIE_")
                || normalized.startsWith("VoidT")
                || normalized.startsWith("OSTRONS_")
                || normalized.startsWith("SOLARIS_")
                || normalized.startsWith("ENTRATI_")
                || normalized.startsWith("Narmer")
                || normalized.startsWith("PVP")
                || normalized.endsWith("Reference")
                || normalized.matches("[A-Z][A-Z0-9_]+");
    }

    /**
     * 判断字段是否承载需要解析的业务引用。
     *
     * @param field 已经声明的字段或字段标识。
     * @return 满足字段是否承载需要解析的业务引用时为真，否则为假。
     */
    private static boolean isReferenceField(String field) {
        if (field == null) {
            return false;
        }
        return switch (field.toLowerCase(Locale.ROOT)) {
            case "item", "reward", "rewarditems", "attackerreward", "defenderreward",
                    "attackerrewards", "defenderrewards", "node", "nodes", "location",
                    "boss", "type", "mission", "missiontype", "modifier", "syndicate",
                    "faction", "upgradetype", "operationtype", "agenttype", "jobtype",
                    "locationtag", "lastdiscoveredlocation", "mode", "category",
                    "title", "tier", "description", "rewardnotice" -> true;
            default -> false;
        };
    }

    /**
     * 按业务身份去重并保留稳定顺序。
     *
     * @param references 引用集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<UnresolvedReferenceVO> distinct(
            List<UnresolvedReferenceVO> references) {
        return new ArrayList<>(new LinkedHashSet<>(references));
    }

    /**
     * 替换内部奖励引用。
     *
     * @param summary 摘要。
     * @param itemNames 物品名称集合。
     * @return 替换内部奖励引用对应的文本。
     */
    private static String replaceInternalRewardReferences(
            String summary, List<String> itemNames) {
        Matcher matcher = INTERNAL_REWARD_REFERENCE.matcher(summary);
        StringBuffer result = new StringBuffer();
        int itemIndex = 0;
        while (matcher.find()) {
            if (itemIndex >= itemNames.size()) {
                break;
            }
            matcher.appendReplacement(
                    result, Matcher.quoteReplacement(itemNames.get(itemIndex++)));
        }
        matcher.appendTail(result);
        return result.toString();
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
     * @param value 待处理的字段值。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<Map<?, ?>> rows(Object value) {
        return values(value).stream().filter(Map.class::isInstance)
                .<Map<?, ?>>map(item -> (Map<?, ?>) item).toList();
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
