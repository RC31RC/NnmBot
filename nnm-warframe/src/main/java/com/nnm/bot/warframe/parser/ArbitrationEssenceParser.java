package com.nnm.bot.warframe.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** 只读取完整 ESSENCE_TABLE 字面量，绝不执行下载的 JavaScript。 */
public final class ArbitrationEssenceParser {
    private static final Pattern DECLARATION =
            Pattern.compile("(?m)^\\s*const\\s+ESSENCE_TABLE\\s*=\\s*(\\{)");
    private static final JsonMapper MAPPER = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
            .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
            .build();

    private ArbitrationEssenceParser() {
    }

    public static List<JsonNode> parse(String script) {
        var matcher = DECLARATION.matcher(script);
        if (!matcher.find()) {
            throw new IllegalArgumentException("缺少完整 ESSENCE_TABLE 声明");
        }
        int start = matcher.start(1);
        if (matcher.find()) {
            throw new IllegalArgumentException("重复 ESSENCE_TABLE 声明");
        }
        try (JsonParser parser = MAPPER.createParser(script.substring(start))) {
            JsonNode table = MAPPER.readTree(parser);
            int end = start + (int) parser.currentLocation().getCharOffset();
            if (!script.substring(end).stripLeading().startsWith(";")
                    || table == null || !table.isObject() || table.isEmpty()) {
                throw new IllegalArgumentException("精华规则必须是完整且非空的对象字面量");
            }
            List<JsonNode> records = new ArrayList<>();
            table.fields().forEachRemaining(entry -> {
                if (entry.getKey().isBlank() || entry.getKey().length() > 200
                        || !entry.getValue().isIntegralNumber()
                        || !entry.getValue().canConvertToInt() || entry.getValue().asInt() < 0) {
                    throw new IllegalArgumentException("非法仲裁精华规则：" + entry.getKey());
                }
                ObjectNode record = MAPPER.createObjectNode();
                record.put("node", entry.getKey());
                record.put("essence", entry.getValue().asInt());
                record.put("unit", "essence/hour");
                records.add(record);
            });
            return List.copyOf(records);
        } catch (IOException exception) {
            throw new IllegalArgumentException("无法完整解析仲裁精华规则", exception);
        }
    }
}
