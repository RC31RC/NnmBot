package com.nnm.bot.warframe.plugin;

import java.math.BigDecimal;

import java.util.LinkedHashMap;
import java.util.Locale;
import com.nnm.bot.warframe.pojo.WarframeWorldStateDisplay;
import com.nnm.bot.warframe.pojo.WarframeWorldStateDisplay.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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

    private WarframeWorldStateDisplayMapper() {}

    public static WarframeWorldStateDisplay from(String command, Map<String, Object> data) {
        List<UnresolvedReference> unresolved = new ArrayList<>();
        Map<String, Object> cleanData = sanitize(data, command, "", unresolved);
        return from(command, cleanData, unresolved);
    }

    public static WarframeWorldStateDisplay from(
            String command,
            Map<String, Object> data,
            List<UnresolvedReference> unresolvedReferences) {
        List<UnresolvedReference> unresolved = new ArrayList<>(
                unresolvedReferences == null ? List.of() : unresolvedReferences);
        Map<String, Object> cleanData = sanitize(data, command, "", unresolved);
        List<Map<?, ?>> rows = rows(cleanData.get("items"));
        List<?> items = switch (command) {
            case "alerts" -> map(rows, row -> new Alert(readable(text(row, "node"), "未知节点"),
                    readable(text(row, "mission"), "未知任务"),
                    readable(text(row, "faction"), "未知阵营"), level(row), reward(row, "reward", "rewardItems"),
                    text(row, "expiry"), text(row, "remaining")));
            case "sorties", "archon-hunts" -> map(rows, row -> new Sortie(
                    readable(text(row, "boss"), "未知目标"),
                    text(row, "expiry"), text(row, "remaining"),
                    map(rows(row.get("variants")), variant -> new Variant(
                            readable(text(variant, "node"), "未知节点"),
                            readable(text(variant, "missionType"), "未知任务"),
                            readable(text(variant, "modifier"), "未知条件")))));
            case "arbitration", "arbitration-table", "today-arbitration", "efficient-arbitration" ->
                    map(rows, row -> new ArbitrationMission(readable(text(row, "node"), "未知节点"),
                            readable(first(text(row, "missionType"), text(row, "type")), "未知任务"),
                            readable(text(row, "faction"), null), text(row, "essence"),
                            text(row, "activation"), text(row, "expiry")));
            case "fissures", "steel-fissures", "void-storms" ->
                    map(rows, row -> new Mission(readable(text(row, "node"), "未知节点"),
                            readable(first(text(row, "missionType"), text(row, "type")), "未知任务"),
                            readable(text(row, "tier"), "未知裂缝"),
                            readable(text(row, "essence"), null),
                            text(row, "expiry"), text(row, "remaining")));
            case "cycles", "duviri-cycle" -> map(rows.stream()
                    .filter(row -> !command.equals("duviri-cycle")
                            || "双衍王境".equals(text(row, "name"))).toList(), row -> {
                        Map<?, ?> cycle = row.get("value") instanceof Map<?, ?> value ? value : Map.of();
                return new Cycle(readable(text(row, "name"), "未知区域"),
                        readable(first(text(cycle, "value"), text(row, "value")), "未知状态"),
                                text(cycle, "expiry"), text(cycle, "remaining"));
                    });
            case "void-trader" -> map(rows, row -> new Trader(readable(text(row, "location"), "未知地点"),
                    Boolean.TRUE.equals(row.get("active")) ? "已到达" : "未到达",
                    text(row, "activation"), text(row, "expiry"),
                    map(rows(row.get("inventory")), product -> new Product(
                            readable(text(product, "item"), "未知物品"),
                            text(product, "ducats"), text(product, "credits")))));
            case "daily-deals" -> map(rows, row -> new Deal(readable(text(row, "item"), "未知物品"),
                    text(row, "credits"),
                    text(row, "platinum"), text(row, "stock"), text(row, "sold"), text(row, "remaining")));
            case "invasions" -> map(rows.stream().filter(row ->
                    !Boolean.TRUE.equals(row.get("completed"))).toList(), row -> new Invasion(
                    readable(text(row, "node"), "未知节点"), text(row, "completion"),
                    reward(row, "attackerReward", "attackerRewards"),
                    reward(row, "defenderReward", "defenderRewards")));
            case "steel-path" -> map(rows, row -> new Offering(readable(text(row, "node"), "未知节点"),
                    readable(text(row, "reward"), "未知奖励"),
                    text(row, "expiry"), text(row, "remaining")));
            case "ostrons", "solaris", "entrati", "cavia", "holdfasts", "hex-bounties",
                    "syndicate-missions" -> map(rows, row -> new Bounty(
                    readable(text(row, "syndicate"), "未知集团"),
                    nodes(row), text(row, "expiry"), text(row, "remaining"),
                    map(rows(row.get("jobs")), WarframeWorldStateDisplayMapper::job)));
            case "nightwave" -> map(rows, row -> new Challenge(readable(text(row, "title"), "未命名任务"),
                    readable(text(row, "description"), "暂无任务说明"),
                    text(row, "reputation"), Boolean.TRUE.equals(row.get("daily")) ? "每日" : "每周"));
            case "conclave" -> map(rows, row -> new Conclave(
                    readable(text(row, "title"), "未命名挑战"),
                    readable(text(row, "description"), "暂无挑战说明"),
                    readable(text(row, "mode"), "未知模式"),
                    readable(text(row, "category"), Boolean.TRUE.equals(row.get("daily")) ? "每日" : "每周"),
                    text(row, "amount"), text(row, "standing"),
                    text(row, "expiry"), text(row, "remaining")));
            case "news" -> map(rows, row -> new News(text(row, "title"),
                    text(row, "link"), text(row, "publishedAt")));
            case "construction-progress" -> map(rows, row ->
                    new Construction(text(row, "name"), text(row, "progress")));
            case "simaris" -> map(rows, row -> new SimarisTarget(
                    readable(text(row, "title"), "未提供目标"),
                    Boolean.TRUE.equals(row.get("active")) ? "研究进行中" : "上次完成的公共研究"));
            case "sentient-outposts" -> map(rows, row -> new Outpost(
                    readable(text(row, "node"), "未知节点"), text(row, "activation"),
                    text(row, "expiry"), text(row, "remaining")));
            case "clan-initiative" -> map(rows, row -> new ClanInitiative(
                    text(row, "week"), readable(text(row, "location"), "未知区域"),
                    map(rows(row.get("rewards")), reward -> new ClanReward(
                            readable(text(reward, "item"), "未知奖励"), text(reward, "count"),
                            text(reward, "points"), text(reward, "claimed")))));
            case "circuit-normal", "circuit-hard" -> map(rows, row ->
                    new CircuitReward(readable(text(row, "item"), "未知选项")));
            case "calendar" -> map(rows, row -> new CalendarDay(text(row, "date"),
                    text(row, "iteration"), map(rows(row.get("events")), event -> new CalendarEvent(
                            readable(text(event, "title"), null), readable(text(event, "description"), null),
                            readable(text(event, "reward"), null),
                            readable(text(event, "dialogueName"), null)))));
            case "vault-trader" -> map(rows, row -> new VaultTrader(
                    readable(text(row, "location"), "未知地点"), text(row, "activation"), text(row, "expiry"),
                    map(rows(row.get("inventory")), product -> new VaultProduct(
                            readable(text(product, "item"), "未知物品"), text(product, "primePrice"),
                            text(product, "regularPrice")))));
            case "timestamp" -> map(rows, row -> new WorldTimestamp(text(row, "timestamp")));
            case "flash-sales" -> map(rows, row -> new FlashSale(
                    readable(text(row, "item"), "未知物品"), text(row, "discount"),
                    text(row, "regularOverride"), text(row, "premiumOverride"),
                    text(row, "expiry"), text(row, "remaining")));
            case "persistent-enemies" -> map(rows, row -> new PersistentEnemy(
                    readable(text(row, "agentType"), "未知敌人"), text(row, "rank"),
                    text(row, "healthPercent"), readable(first(text(row, "lastDiscoveredLocation"),
                            text(row, "locationTag")), "未知地点"), text(row, "lastDiscoveredTime"),
                    row.get("discovered") instanceof Boolean value ? value : null));
            case "archimedeas", "deep-archimedeas", "hex-archimedeas" ->
                    map(rows, row -> new Archimedea(
                            readable(first(text(row, "type"), text(row, "typeKey")), "未知科研"),
                            text(row, "expiry"), text(row, "remaining"),
                            values(row.get("personalModifiers")).stream()
                                    .map(value -> readable(String.valueOf(value), "未知词缀")).toList(),
                            map(rows(row.get("missions")), mission -> new ArchimedeaMission(
                                    readable(text(mission, "faction"), "未知阵营"),
                                    readable(first(text(mission, "missionType"),
                                            text(mission, "missionTypeKey")), "未知任务"),
                                    readable(text(mission, "deviation"), "未知偏差"),
                                            values(mission.get("risks")).stream()
                                    .map(value -> readable(String.valueOf(value), "未知风险")).toList()))));
            case "descents" -> map(rows, row -> new WorldStateEntry(
                    "深层下降",
                    "种子：" + readable(text(row, "seed"), "未知"),
                    null, null, null,
                    map(rows(row.get("challenges")), challenge -> new Reward(
                            readable(text(challenge, "challenge"), "未知挑战"),
                            text(challenge, "index"), null, null, null)),
                    null, null, null));
            case "events" -> map(rows, row -> new WorldStateEntry(
                    readable(text(row, "title"), "未命名活动"),
                    readable(text(row, "description"), null),
                    readable(text(row, "node"), null),
                    text(row, "progress"),
                    text(row, "goal"),
                    map(rows(row.get("rewards")), reward -> new Reward(
                            readable(text(reward, "item"), "未知奖励"),
                            text(reward, "count"), null, null, null)),
                    null, text(row, "expiry"), text(row, "remaining")));
            case "global-upgrades" -> map(rows, row -> new WorldStateEntry(
                    readable(text(row, "upgradeType"), "未知增益"),
                    readable(text(row, "operationType"), null),
                    null, null, null, List.of(),
                    text(row, "value"), text(row, "expiry"), text(row, "remaining")));
            default -> throw new IllegalArgumentException("Missing world state display: " + command);
        };
        return new WarframeWorldStateDisplay(items,
                first(text(cleanData, "notice"), Boolean.TRUE.equals(cleanData.get("stale"))
                        ? "上游刷新暂未成功，当前为缓存数据" : null),
                distinct(unresolved));
    }

    private static Job job(Map<?, ?> row) {
        return new Job(readable(text(row, "type"), "赏金任务"),
                readable(text(row, "description"), null), level(row), text(row, "minMR"),
                values(row.get("standingStages")),
                readable(text(row, "locationTag"), "未知区域"), text(row, "remaining"),
                map(rows(row.get("rewards")), reward -> new Reward(
                        first(text(reward, "displayName"), readable(text(reward, "item"), "未知奖励")),
                        text(reward, "count"), rewardChance(reward),
                        text(reward, "stage"), readable(text(reward, "rarity"), null))),
                readable(text(row, "rewardNotice"), null));
    }

    private static String rewardChance(Map<?, ?> reward) {
        Object probability = reward.get("probability");
        return probability instanceof Number number
                ? new BigDecimal(number.toString()).movePointRight(2).stripTrailingZeros().toPlainString()
                : text(reward, "chance");
    }

    private static List<String> nodes(Map<?, ?> row) {
        List<String> nodes = values(row.get("nodes")).stream().filter(Objects::nonNull)
                .map(Object::toString).map(value -> readable(value, null))
                .filter(Objects::nonNull).distinct().toList();
        String node = readable(text(row, "node"), null);
        return nodes.isEmpty() && node != null ? List.of(node) : nodes;
    }

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
        if (summaryText != null && !itemNames.isEmpty()) {
            summaryText = replaceInternalRewardReferences(summaryText, itemNames);
        }
        String itemText = String.join("、", itemNames);
        return first(summaryText, itemText);
    }

    private static String level(Map<?, ?> row) {
        String min = text(row, "minEnemyLevel");
        String max = text(row, "maxEnemyLevel");
        return min != null && max != null ? min + "-" + max : first(min, max);
    }

    private static String first(String first, String second) {
        return first == null || first.isBlank() ? second : first;
    }

    private static String text(Map<?, ?> row, String key) {
        Object value = row.get(key);
        return value == null || value.toString().isBlank() ? null : value.toString();
    }

    private static String readable(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String normalized = value.trim();
        return isInternalReference(normalized) ? fallback : normalized;
    }

    private static Map<String, Object> sanitize(
            Map<String, Object> data,
            String command,
            String path,
            List<UnresolvedReference> unresolved) {
        if (data == null || data.isEmpty()) {
            return data == null ? Map.of() : data;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        data.forEach((key, value) -> {
            String childPath = path.isBlank() ? key : path + "." + key;
            result.put(key, sanitizeValue(value, key, childPath, command, unresolved));
        });
        return result;
    }

    private static Object sanitizeValue(
            Object value,
            String field,
            String path,
            String command,
            List<UnresolvedReference> unresolved) {
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
                unresolved.add(new UnresolvedReference(
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
            unresolved.add(new UnresolvedReference(command, path, text, "未解析", List.of()));
            return unresolvedPlaceholder(field);
        }
        return text;
    }

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

    private static List<UnresolvedReference> distinct(
            List<UnresolvedReference> references) {
        return new ArrayList<>(new LinkedHashSet<>(references));
    }

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

    private static List<?> values(Object value) {
        return value instanceof List<?> list ? list : List.of();
    }

    private static List<Map<?, ?>> rows(Object value) {
        return values(value).stream().filter(Map.class::isInstance)
                .<Map<?, ?>>map(item -> (Map<?, ?>) item).toList();
    }

    private static <T> List<T> map(List<Map<?, ?>> rows, Function<Map<?, ?>, T> mapper) {
        return rows.stream().map(mapper).toList();
    }
}
