package com.nnm.bot.warframe.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** 标准化完整奖励池，原始阶段和条目字段保留在快照中。 */
public final class ExportRewardsParser {
    private ExportRewardsParser() {
    }

    public static List<JsonNode> parse(JsonNode root) {
        if (!root.isObject()) {
            throw new IllegalArgumentException("ExportRewards 必须是奖励池对象");
        }
        List<JsonNode> result = new ArrayList<>();
        root.fields().forEachRemaining(entry -> {
            if (!entry.getValue().isArray() || entry.getKey().isBlank()) {
                throw new IllegalArgumentException("奖励池必须包含阶段数组");
            }
            ObjectNode pool = JsonNodeFactory.instance.objectNode();
            pool.put("poolKey", entry.getKey());
            pool.put("recordKey", entry.getKey());
            pool.set("stages", entry.getValue().deepCopy());
            ArrayNode rewards = pool.putArray("rewards");
            for (int stage = 0; stage < entry.getValue().size(); stage++) {
                JsonNode entries = entry.getValue().get(stage);
                if (!entries.isArray()) {
                    throw new IllegalArgumentException("奖励阶段必须为数组");
                }
                for (JsonNode reward : entries) {
                    rewards.add(normalizeReward(reward, stage + 1));
                }
            }
            result.add(pool);
        });
        return List.copyOf(result);
    }

    public static ObjectNode normalizePool(JsonNode source) {
        if (!source.isObject()) {
            throw new IllegalArgumentException("奖励池记录必须是对象");
        }
        String key = text(source, "poolKey", "uniqueName", "recordKey");
        if (key == null || key.length() > 500 || !source.path("rewards").isArray()) {
            throw new IllegalArgumentException("奖励池必须包含完整池键和 rewards 数组");
        }
        ObjectNode pool = source.deepCopy();
        pool.put("poolKey", key);
        pool.put("recordKey", key);
        ArrayNode rewards = pool.putArray("rewards");
        for (JsonNode reward : source.path("rewards")) {
            rewards.add(normalizeReward(reward, null));
        }
        return pool;
    }

    private static ObjectNode normalizeReward(JsonNode reward, Integer stageNumber) {
        if (!reward.isObject()) {
            throw new IllegalArgumentException("奖励条目必须是对象");
        }
        String itemKey = text(reward, "itemKey", "type", "uniqueName", "item");
        if (itemKey == null && reward.path("item").isObject()) {
            itemKey = text(reward.path("item"), "uniqueName");
        }
        JsonNode count = reward.hasNonNull("count") ? reward.get("count") : reward.get("itemCount");
        JsonNode stage = reward.get("stage");
        if (itemKey == null || count == null || !count.isIntegralNumber()
                || !count.canConvertToInt() || count.intValue() < 0
                || (stageNumber == null && stage != null
                && (!stage.isIntegralNumber() || !stage.canConvertToInt() || stage.intValue() < 1))) {
            throw new IllegalArgumentException("奖励条目缺少完整物品键、有效数量或阶段");
        }
        JsonNode probability = reward.get("probability");
        if (probability != null && !probability.isNull()
                && (!probability.isNumber() || probability.decimalValue().compareTo(BigDecimal.ZERO) < 0
                || probability.decimalValue().compareTo(BigDecimal.ONE) > 0)) {
            throw new IllegalArgumentException("奖励概率必须是 0 到 1 的小数");
        }
        JsonNode rarity = reward.get("rarity");
        if (rarity != null && !rarity.isNull()
                && (!rarity.isTextual() || rarity.textValue().isBlank())) {
            throw new IllegalArgumentException("奖励稀有度必须是非空字符串或 null");
        }
        ObjectNode normalized = reward.deepCopy();
        normalized.put("stage", stageNumber != null ? stageNumber : stage == null ? 1 : stage.intValue());
        normalized.put("itemKey", itemKey);
        // 展示名只能由完整物品键解析，不能相信上游按路径末段生成的名称。
        normalized.put("displayName", itemKey);
        normalized.put("count", count.intValue());
        if (probability == null || probability.isNull()) {
            normalized.putNull("probability");
        }
        if (rarity == null || rarity.isNull()) {
            normalized.putNull("rarity");
        } else {
            normalized.put("rarity", rarity.textValue().toUpperCase(Locale.ROOT));
        }
        return normalized;
    }

    private static String text(JsonNode node, String... fields) {
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && value.isTextual() && !value.textValue().isBlank()) {
                return value.textValue();
            }
        }
        return null;
    }
}
