package com.nnm.bot.warframe.infrastructure.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.domain.model.WorldState;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

/**
 * 将 warframestat.us 和官方 worldState 风格 JSON 转成 NnmBot 领域模型。
 *
 * <p>解析器只处理协议兼容性：字段别名、时间戳格式、空数组和部分缺失字段。
 * 未知字段会被忽略，但不会阻断已知业务数据；模型本身不携带 JsonNode。</p>
 */
@Component
public class WorldStateParser {

    @Resource
    private ObjectMapper objectMapper;

    public WorldState parse(String json) {
        return parse(json, "warframe-official");
    }

    public WorldState parse(String json, String source) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("world state response must not be blank");
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root == null || !root.isObject()) {
                throw new IllegalArgumentException("world state response must be a JSON object");
            }
            Instant providerTimestamp = instant(root, "timestamp", "time", "serverTime");
            Instant fetchedAt = Instant.now();
            WorldState state = new WorldState(
                    alerts(root),
                    fissures(root),
                    invasions(root),
                    firstSortie(root),
                    cetusCycle(root),
                    vallisCycle(root),
                    cambionCycle(root),
                    firstVoidTrader(root),
                    arbitration(root),
                    nightwave(root),
                    providerTimestamp,
                    sorties(root),
                    voidTraders(root),
                    earthCycle(root),
                    duviriCycle(root),
                    zarimanCycle(root),
                    dailyDeals(root),
                    events(root),
                    news(root),
                    syndicateMissions(root),
                    steelPathOfferings(root),
                    activeMissions(root),
                    archonHunts(root),
                    new WorldState.WorldStateMetadata(source, fetchedAt, providerTimestamp, false,
                            "nnm-world-state-v1", sha256(root.toString()))
            );
            validate(state);
            return state;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("failed to parse world state JSON", e);
        }
    }

    private List<WorldState.Alert> alerts(JsonNode root) {
        List<WorldState.Alert> result = new ArrayList<>();
        for (JsonNode item : array(root, "alerts")) {
            JsonNode reward = first(item, "reward", "mission.reward", "missionInfo.missionReward");
            result.add(new WorldState.Alert(
                    text(item, "id", "_id"),
                    text(item, "mission.type", "missionInfo.missionType", "mission", "description"),
                    rewardSummary(reward),
                    instant(item, "expiry", "expiration"),
                    instant(item, "activation"),
                    text(item, "node", "mission.node", "mission.location", "missionInfo.location"),
                    text(item, "faction", "mission.faction", "missionInfo.faction"),
                    integer(item, "minEnemyLevel", "mission.minEnemyLevel", "missionInfo.minEnemyLevel"),
                    integer(item, "maxEnemyLevel", "mission.maxEnemyLevel", "missionInfo.maxEnemyLevel"),
                    strings(reward, "items", "countedItems")
            ));
        }
        return result;
    }

    private List<WorldState.Fissure> fissures(JsonNode root) {
        List<WorldState.Fissure> result = new ArrayList<>();
        for (JsonNode item : array(root, "fissures", "activeMissions")) {
            String tier = text(item, "tier", "modifier");
            result.add(new WorldState.Fissure(
                    text(item, "id", "_id"),
                    text(item, "node", "mission.node"),
                    tier,
                    text(item, "missionType", "mission.type"),
                    instant(item, "expiry", "expiration"),
                    instant(item, "activation"),
                    bool(item, "hard", "isHard"),
                    bool(item, "voidStorm", "voidStorms")
            ));
        }
        return result;
    }

    private List<WorldState.Invasion> invasions(JsonNode root) {
        List<WorldState.Invasion> result = new ArrayList<>();
        for (JsonNode item : array(root, "invasions")) {
            float completion = decimal(item, "completion", "completionRate");
            result.add(new WorldState.Invasion(
                    text(item, "id", "_id"),
                    text(item, "node", "location"),
                    text(item, "attackerReward.asString", "attackerReward"),
                    text(item, "defenderReward.asString", "defenderReward"),
                    completion,
                    instant(item, "activation"),
                    bool(item, "completed"),
                    strings(item, "attackerReward.items", "attackerReward.countedItems"),
                    strings(item, "defenderReward.items", "defenderReward.countedItems")
            ));
        }
        return result;
    }

    private List<WorldState.Sortie> sorties(JsonNode root) {
        return sorties(root, "sorties", "sortie");
    }

    private List<WorldState.Sortie> archonHunts(JsonNode root) {
        return sorties(root, "archonHunts", "archonHunt", "liteSorties");
    }

    private List<WorldState.Sortie> sorties(JsonNode root, String... paths) {
        List<WorldState.Sortie> result = new ArrayList<>();
        for (JsonNode item : array(root, paths)) {
            List<WorldState.SortieVariant> variants = new ArrayList<>();
            for (JsonNode variant : array(item, "variants", "missions")) {
                variants.add(new WorldState.SortieVariant(
                        text(variant, "missionType", "type"),
                        text(variant, "modifier", "modifierType", "modifierDescription"),
                        text(variant, "node", "location")
                ));
            }
            result.add(new WorldState.Sortie(text(item, "id", "_id"), text(item, "boss", "bossName"),
                    variants, instant(item, "expiry", "expiration")));
        }
        return result;
    }

    private List<WorldState.VoidTrader> voidTraders(JsonNode root) {
        List<WorldState.VoidTrader> result = new ArrayList<>();
        for (JsonNode item : array(root, "voidTraders", "voidTrader")) {
            List<WorldState.VoidTraderItem> inventory = new ArrayList<>();
            for (JsonNode product : array(item, "inventory", "manifest")) {
                inventory.add(new WorldState.VoidTraderItem(
                        text(product, "item", "itemType", "uniqueName"),
                        integerOrZero(product, "ducats", "primePrice"),
                        integerOrZero(product, "credits", "regularPrice")
                ));
            }
            result.add(new WorldState.VoidTrader(text(item, "location", "node"), instant(item, "activation"),
                    instant(item, "expiry", "expiration"), bool(item, "active"), inventory));
        }
        return result;
    }

    private List<WorldState.NightwaveMission> nightwave(JsonNode root) {
        List<WorldState.NightwaveMission> result = new ArrayList<>();
        JsonNode season = first(root, "nightwave", "seasonInfo");
        for (JsonNode item : array(season, "missions", "activeChallenges")) {
            result.add(new WorldState.NightwaveMission(text(item, "id", "_id", "challenge"),
                    text(item, "title", "name", "challenge"), text(item, "desc", "description"),
                    integerOrZero(item, "reputation", "standing"),
                    bool(item, "isDaily", "daily", "dailyTasks")));
        }
        return result;
    }

    private List<WorldState.DailyDeal> dailyDeals(JsonNode root) {
        List<WorldState.DailyDeal> result = new ArrayList<>();
        for (JsonNode item : array(root, "dailyDeals")) {
            result.add(new WorldState.DailyDeal(text(item, "id", "_id"),
                    text(item, "item", "itemType", "storeItem"),
                    integerOrZero(item, "stock", "total", "amountTotal"),
                    integerOrZero(item, "sold", "amountSold"),
                    integerOrZero(item, "credits", "regularPrice", "originalPrice"),
                    integerOrZero(item, "platinum", "primePrice", "salePrice"),
                    instant(item, "activation"), instant(item, "expiry", "expiration")));
        }
        return result;
    }

    private List<WorldState.WorldEvent> events(JsonNode root) {
        List<WorldState.WorldEvent> result = new ArrayList<>();
        List<JsonNode> items = root.has("Goals") ? exactArray(root, "Goals")
                : root.has("goals") ? exactArray(root, "goals") : exactArray(root, "events");
        for (JsonNode item : items) {
            result.add(new WorldState.WorldEvent(
                    text(item, "id", "_id"),
                    text(item, "description", "desc", "title", "name"),
                    text(item, "tooltip", "toolTip", "description"),
                    instant(item, "activation", "eventStartDate", "date"),
                    instant(item, "expiry", "expiration", "eventEndDate"),
                    text(item, "category", "tag", "type"),
                    text(item, "node", "location"),
                    number(item, "currentScore", "healthPct", "count"),
                    number(item, "maximumScore", "goal"),
                    strings(item, "rewards", "reward.items", "reward.countedItems")
            ));
        }
        return result;
    }

    private List<WorldState.NewsItem> news(JsonNode root) {
        List<WorldState.NewsItem> result = new ArrayList<>();
        List<JsonNode> items;
        if (root.has("news")) {
            items = exactArray(root, "news");
        } else if (root.has("Events")) {
            items = exactArray(root, "Events");
        } else {
            items = root.has("Goals") || root.has("goals") ? exactArray(root, "events") : List.of();
        }
        for (JsonNode item : items) {
            result.add(new WorldState.NewsItem(
                    text(item, "id", "_id"),
                    eventTitle(item),
                    text(item, "link", "prop"),
                    text(item, "imageLink", "imageUrl", "image", "icon"),
                    instant(item, "date", "activation", "eventStartDate"),
                    instant(item, "expiry", "expiration", "eventEndDate"),
                    bool(item, "priority")
            ));
        }
        return result;
    }

    private List<WorldState.SyndicateMission> syndicateMissions(JsonNode root) {
        List<WorldState.SyndicateMission> result = new ArrayList<>();
        for (JsonNode item : array(root, "syndicateMissions")) {
            result.add(new WorldState.SyndicateMission(text(item, "id", "_id"),
                    text(item, "syndicate", "faction", "tag"), firstString(item, "node", "location", "nodes"),
                    instant(item, "activation"), instant(item, "expiry", "expiration"),
                    strings(item, "jobs", "missions")));
        }
        return result;
    }

    private List<WorldState.SteelPathOffering> steelPathOfferings(JsonNode root) {
        List<WorldState.SteelPathOffering> result = new ArrayList<>();
        for (JsonNode item : array(root, "steelPathOfferings")) {
            result.add(new WorldState.SteelPathOffering(text(item, "id", "_id"),
                    text(item, "node", "location"), text(item, "reward", "reward.asString"),
                    instant(item, "activation"), instant(item, "expiry", "expiration")));
        }
        JsonNode currentReward = first(root, "steelPath.currentReward");
        if (currentReward.isObject()) {
            result.add(new WorldState.SteelPathOffering(text(currentReward, "id", "_id"),
                    text(root, "steelPath.node", "steelPath.location"),
                    text(currentReward, "name", "item", "reward"),
                    instant(root, "steelPath.activation"), instant(root, "steelPath.expiry")));
        }
        return result;
    }

    private List<WorldState.ActiveMission> activeMissions(JsonNode root) {
        List<WorldState.ActiveMission> result = new ArrayList<>();
        for (JsonNode item : array(root, "activeMissions", "fissures")) {
            result.add(new WorldState.ActiveMission(text(item, "id", "_id"), text(item, "node"),
                    text(item, "missionType", "mission.type"), text(item, "tier", "modifier"),
                    instant(item, "activation"), instant(item, "expiry", "expiration"),
                    bool(item, "hard", "isHard"), bool(item, "voidStorm", "voidStorms")));
        }
        for (JsonNode item : array(root, "voidStorms")) {
            result.add(new WorldState.ActiveMission(text(item, "id", "_id"), text(item, "node"),
                    text(item, "missionType"), text(item, "tier", "activeMissionTier"),
                    instant(item, "activation"), instant(item, "expiry", "expiration"),
                    false, true));
        }
        return result;
    }

    private WorldState.CetusCycle cetusCycle(JsonNode root) {
        JsonNode node = first(root, "cetusCycle");
        return node.isMissingNode() || node.isNull() ? null : new WorldState.CetusCycle(
                bool(node, "isDay", "day"), instant(node, "expiry", "expiration"));
    }

    private WorldState.VallisCycle vallisCycle(JsonNode root) {
        JsonNode node = first(root, "vallisCycle");
        return node.isMissingNode() || node.isNull() ? null : new WorldState.VallisCycle(
                bool(node, "isWarm", "warm"), instant(node, "expiry", "expiration"));
    }

    private WorldState.CambionCycle cambionCycle(JsonNode root) {
        JsonNode node = first(root, "cambionCycle");
        return node.isMissingNode() || node.isNull() ? null : new WorldState.CambionCycle(
                text(node, "active", "state"), instant(node, "expiry", "expiration"));
    }

    private WorldState.EarthCycle earthCycle(JsonNode root) {
        JsonNode node = first(root, "earthCycle");
        return node.isMissingNode() || node.isNull() ? null
                : new WorldState.EarthCycle(bool(node, "isDay", "day"), instant(node, "expiry", "expiration"));
    }

    private WorldState.DuviriCycle duviriCycle(JsonNode root) {
        JsonNode node = first(root, "duviriCycle", "duvalierCycle");
        return node.isMissingNode() || node.isNull() ? null
                : new WorldState.DuviriCycle(text(node, "state", "active", "choice"), instant(node, "expiry", "expiration"));
    }

    private WorldState.ZarimanCycle zarimanCycle(JsonNode root) {
        JsonNode node = first(root, "zarimanCycle");
        return node.isMissingNode() || node.isNull() ? null
                : new WorldState.ZarimanCycle(text(node, "state", "active"), instant(node, "expiry", "expiration"));
    }

    private WorldState.Sortie firstSortie(JsonNode root) {
        return sorties(root).stream().findFirst().orElse(null);
    }

    private WorldState.VoidTrader firstVoidTrader(JsonNode root) {
        return voidTraders(root).stream().findFirst().orElse(null);
    }

    private WorldState.Arbitration arbitration(JsonNode root) {
        JsonNode node = first(root, "arbitration");
        return node.isMissingNode() || node.isNull() ? null
                : new WorldState.Arbitration(text(node, "node", "location"), text(node, "type", "missionType"),
                instant(node, "expiry", "expiration"));
    }

    private List<JsonNode> array(JsonNode root, String... paths) {
        for (String path : paths) {
            JsonNode value = first(root, path);
            if (value.isArray()) {
                List<JsonNode> result = new ArrayList<>();
                value.forEach(result::add);
                if (!result.isEmpty()) {
                    return result;
                }
            }
            if (value.isObject()) {
                return List.of(value);
            }
        }
        return List.of();
    }

    private List<JsonNode> exactArray(JsonNode root, String field) {
        if (root == null || !root.isObject()) {
            return List.of();
        }
        JsonNode value = root.get(field);
        if (value == null || value.isNull()) {
            return List.of();
        }
        if (value.isArray()) {
            List<JsonNode> result = new ArrayList<>();
            value.forEach(result::add);
            return result;
        }
        return value.isObject() ? List.of(value) : List.of();
    }

    private JsonNode first(JsonNode root, String... paths) {
        if (root == null) {
            return com.fasterxml.jackson.databind.node.MissingNode.getInstance();
        }
        for (String path : paths) {
            JsonNode current = root;
            for (String part : path.split("\\.")) {
                current = childIgnoreCase(current, part);
            }
            if (!current.isMissingNode() && !current.isNull()) {
                return current;
            }
        }
        return com.fasterxml.jackson.databind.node.MissingNode.getInstance();
    }

    private String text(JsonNode root, String... paths) {
        for (String path : paths) {
            JsonNode value = first(root, path);
            if (value.isValueNode() && !value.isContainerNode()) {
                return value.asText(null);
            }
            JsonNode wrapped = first(value, "$oid", "$numberLong", "value");
            if (wrapped.isValueNode()) {
                return wrapped.asText(null);
            }
        }
        return null;
    }

    private Integer integer(JsonNode root, String... paths) {
        JsonNode value = first(root, paths);
        if (value.isNumber()) {
            return value.asInt();
        }
        return value.isTextual() ? parseInt(value.asText()) : null;
    }

    private int integerOrZero(JsonNode root, String... paths) {
        Integer value = integer(root, paths);
        return value == null ? 0 : value;
    }

    private float decimal(JsonNode root, String... paths) {
        JsonNode value = first(root, paths);
        return value.isNumber() ? value.floatValue() : value.isTextual() ? parseFloat(value.asText()) : 0;
    }

    private Double number(JsonNode root, String... paths) {
        JsonNode value = first(root, paths);
        if (value.isNumber()) {
            return value.doubleValue();
        }
        if (value.isTextual()) {
            try {
                return Double.valueOf(value.asText());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private boolean bool(JsonNode root, String... paths) {
        JsonNode value = first(root, paths);
        return value.isBoolean() ? value.booleanValue() : value.isTextual() && Boolean.parseBoolean(value.asText());
    }

    private List<String> strings(JsonNode root, String... paths) {
        for (String path : paths) {
            JsonNode value = first(root, path);
            if (value.isArray()) {
                List<String> result = new ArrayList<>();
                value.forEach(item -> {
                    String text = item.isValueNode() ? item.asText(null)
                            : text(item, "name", "item", "itemType", "uniqueName", "jobType", "rewards");
                    if (text != null && !text.isBlank()) {
                        result.add(text);
                    }
                });
                if (!result.isEmpty()) {
                    return result;
                }
            } else {
                String text = text(root, path);
                if (text != null && !text.isBlank()) {
                    return List.of(text);
                }
            }
        }
        return List.of();
    }

    private Instant instant(JsonNode root, String... paths) {
        for (String path : paths) {
            Instant parsed = instant(first(root, path));
            if (parsed != null) {
                return parsed;
            }
        }
        return null;
    }

    private Instant instant(JsonNode value) {
        if (value.isObject()) {
            JsonNode nested = first(value, "$date", "$numberLong", "epochMillis", "value");
            return nested == value || nested.isMissingNode() ? null : instant(nested);
        }
        if (value.isNumber()) {
            long raw = value.asLong();
            return Instant.ofEpochMilli(raw < 10_000_000_000L ? raw * 1000 : raw);
        }
        if (!value.isTextual() || value.asText().isBlank()) {
            return null;
        }
        String text = value.asText();
        try {
            return Instant.parse(text);
        } catch (Exception ignored) {
            try {
                long raw = Long.parseLong(text);
                return raw < 10_000_000_000L ? Instant.ofEpochSecond(raw) : Instant.ofEpochMilli(raw);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    private JsonNode childIgnoreCase(JsonNode parent, String name) {
        JsonNode exact = parent.path(name);
        if (!exact.isMissingNode() || !parent.isObject()) {
            return exact;
        }
        for (Map.Entry<String, JsonNode> field : parent.properties()) {
            if (field.getKey().equalsIgnoreCase(name)) {
                return field.getValue();
            }
        }
        return com.fasterxml.jackson.databind.node.MissingNode.getInstance();
    }

    private void validate(WorldState state) {
        boolean hasData = state.timestamp() != null
                || !state.alerts().isEmpty()
                || !state.fissures().isEmpty()
                || !state.invasions().isEmpty()
                || !state.allSorties().isEmpty()
                || !state.archonHunts().isEmpty()
                || !state.allVoidTraders().isEmpty()
                || state.cetusCycle() != null
                || state.vallisCycle() != null
                || state.cambionCycle() != null
                || state.earthCycle() != null
                || state.duviriCycle() != null
                || state.zarimanCycle() != null
                || !state.events().isEmpty()
                || !state.news().isEmpty()
                || !state.dailyDeals().isEmpty()
                || !state.nightwave().isEmpty()
                || !state.syndicateMissions().isEmpty()
                || !state.steelPathOfferings().isEmpty()
                || !state.allActiveMissions().isEmpty()
                || state.arbitration() != null;
        if (!hasData) {
            throw new IllegalArgumentException("world state response contains no recognized data");
        }
    }

    private String eventTitle(JsonNode item) {
        String direct = text(item, "title", "name", "message");
        if (direct != null) {
            return direct;
        }
        String english = null;
        String fallback = null;
        for (JsonNode message : array(item, "messages")) {
            String value = text(message, "message", "text");
            String language = text(message, "languageCode", "language");
            if (fallback == null) {
                fallback = value;
            }
            if (language != null && (language.equalsIgnoreCase("zh") || language.equalsIgnoreCase("zh-hans"))) {
                return value;
            }
            if (language != null && language.equalsIgnoreCase("en")) {
                english = value;
            }
        }
        return english == null ? fallback : english;
    }

    private String rewardSummary(JsonNode reward) {
        String direct = text(reward, "asString", "description", "string");
        if (direct != null) {
            return direct;
        }
        List<String> items = strings(reward, "items", "countedItems");
        int credits = integerOrZero(reward, "credits");
        if (!items.isEmpty() && credits > 0) {
            return String.join(", ", items) + ", " + credits + " credits";
        }
        if (!items.isEmpty()) {
            return String.join(", ", items);
        }
        return credits > 0 ? credits + " credits" : null;
    }

    private String firstString(JsonNode root, String... paths) {
        for (String path : paths) {
            JsonNode value = first(root, path);
            if (value.isArray() && !value.isEmpty()) {
                String item = text(value.get(0), "value", "name", "node");
                return item == null && value.get(0).isValueNode() ? value.get(0).asText(null) : item;
            }
            String item = text(root, path);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    private Integer parseInt(String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String sha256(String content) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
