package com.nnm.bot.warframe.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.pojo.WarframeMarketItem;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 统一解析 Warframe Market 物品目录和订单内嵌物品字段。 */
@Component
public class WarframeMarketItemParser {

    @Resource
    private ObjectMapper objectMapper;

    public WarframeMarketItem parse(JsonNode item) {
        if (item == null || item.isMissingNode() || item.isNull()) {
            return new WarframeMarketItem("", null);
        }
        Map<String, WarframeMarketItem.WarframeMarketItemI18n> i18n = new LinkedHashMap<>();
        JsonNode localized = item.path("i18n");
        if (localized.isObject()) {
            localized.fields().forEachRemaining(entry ->
                    i18n.put(entry.getKey(), parseI18n(entry.getValue())));
        }
        String legacyEnglish = text(item, "en", "name");
        if (!legacyEnglish.isBlank() && !i18n.containsKey("en")) {
            i18n.put("en", new WarframeMarketItem.WarframeMarketItemI18n(
                    legacyEnglish, "", "", "", "", ""));
        }
        return new WarframeMarketItem(
                text(item, "id", "_id"),
                text(item, "slug", "url_name", "urlName"),
                text(item, "gameRef", "game_ref", "uniqueName"),
                strings(item, "tags"),
                nullableBoolean(item, "setRoot", "set_root"),
                strings(item, "setParts", "set_parts"),
                nullableInteger(item, "quantityInSet", "quantity_in_set"),
                text(item, "rarity"),
                nullableBoolean(item, "bulkTradable", "bulk_tradable"),
                strings(item, "subtypes"),
                nullableInteger(item, "maxRank", "max_rank"),
                nullableInteger(item, "maxCharges", "max_charges"),
                nullableInteger(item, "maxAmberStars", "max_amber_stars"),
                nullableInteger(item, "maxCyanStars", "max_cyan_stars"),
                nullableInteger(item, "baseEndo", "base_endo"),
                decimal(item, "endoMultiplier", "endo_multiplier"),
                nullableInteger(item, "ducats"),
                nullableInteger(item, "vosfor"),
                nullableInteger(item, "reqMasteryRank", "req_mastery_rank"),
                nullableBoolean(item, "vaulted"),
                nullableInteger(item, "tradingTax", "trading_tax"),
                nullableBoolean(item, "tradable"),
                i18n,
                rawFields(item));
    }

    private WarframeMarketItem.WarframeMarketItemI18n parseI18n(JsonNode value) {
        return new WarframeMarketItem.WarframeMarketItemI18n(
                text(value, "name"),
                text(value, "description"),
                text(value, "wikiLink", "wiki_link"),
                text(value, "icon"),
                text(value, "thumb"),
                text(value, "subIcon", "sub_icon"));
    }

    private Map<String, Object> rawFields(JsonNode node) {
        if (node == null || !node.isObject()) {
            return Map.of();
        }
        return objectMapper.convertValue(node, new TypeReference<>() {
        });
    }

    private static String text(JsonNode node, String... fields) {
        if (node == null) {
            return "";
        }
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && !value.isNull()) {
                return value.asText("");
            }
        }
        return "";
    }

    private static Integer nullableInteger(JsonNode node, String... fields) {
        if (node == null) {
            return null;
        }
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && value.isIntegralNumber()) {
                return value.asInt();
            }
        }
        return null;
    }

    private static Double decimal(JsonNode node, String... fields) {
        if (node == null) {
            return null;
        }
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && value.isNumber()) {
                return value.asDouble();
            }
        }
        return null;
    }

    private static Boolean nullableBoolean(JsonNode node, String... fields) {
        if (node == null) {
            return null;
        }
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && !value.isNull()) {
                return value.isBoolean() ? value.asBoolean() : Boolean.valueOf(value.asText());
            }
        }
        return null;
    }

    private static List<String> strings(JsonNode node, String... fields) {
        if (node == null) {
            return List.of();
        }
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && value.isArray()) {
                List<String> result = new ArrayList<>();
                value.forEach(entry -> {
                    if (entry.isValueNode()) {
                        result.add(entry.asText());
                    }
                });
                return List.copyOf(result);
            }
        }
        return List.of();
    }
}
