package com.nnm.bot.core.plugin.response;

import java.util.Objects;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 命令结构化数据的通用文字排版器。
 *
 * <p>{@code CommandResult.data} 是所有命令共用的已处理展示数据。该类只负责
 * 将它排版成文字，不参与业务解析，也不为 WorldState 或市场命令提供特殊协议。</p>
 */
@Component
public class CommandDataTextFormatter {

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 将数据按稳定的缩进结构转换成可直接发送的文字。
     *
     * @param data 命令返回的展示数据
     * @param summary 命令摘要，仅为调用协议保留，不参与数据字段筛选
     * @return 排版后的文字；没有数据时返回空字符串
     */
    public String format(Object data, String summary) {
        if (data == null) {
            return "";
        }
        Object serialized = objectMapper.convertValue(data, Object.class);
        if (serialized == null) {
            return "";
        }
        Map<?, ?> fields = serialized instanceof Map<?, ?> map ? map : Map.of("items", serialized);
        Map<String, BeanPropertyDefinition> properties = properties(data);
        StringBuilder result = new StringBuilder();
        if (serialized instanceof Map<?, ?> map && inline(data)) {
            appendInline(result, map, data, 0);
            return result.toString().trim();
        }
        for (Map.Entry<?, ?> entry : fields.entrySet()) {
            String key = String.valueOf(entry.getKey());
            BeanPropertyDefinition property = properties.get(key);
            Object original = serialized instanceof Map<?, ?>
                    ? propertyValue(data, key, property) : data;
            appendField(result, displayLabel(key, property), entry.getValue(), original, 0);
        }
        return result.toString().trim();
    }

    private void appendField(StringBuilder output, String label, Object value, Object original, int depth) {
        indent(output, depth);
        output.append(label).append(": ");
        if (value instanceof Map<?, ?> map) {
            output.append('\n');
            appendMap(output, map, original, depth + 1);
        } else if (value instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                output.append("[]\n");
            } else if (collection.stream().noneMatch(item ->
                    item instanceof Map<?, ?> || item instanceof Collection<?>)) {
                output.append(String.join("、", collection.stream()
                        .map(CommandDataTextFormatter::scalar).toList())).append('\n');
            } else {
                output.append('\n');
                appendCollection(output, collection, original, depth + 1);
            }
        } else {
            output.append(value == null ? "未提供" : value instanceof Boolean flag
                    ? (flag ? "是" : "否") : value).append('\n');
        }
    }

    private void appendMap(StringBuilder output, Map<?, ?> map, Object original, int depth) {
        Map<String, BeanPropertyDefinition> properties = properties(original);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = String.valueOf(entry.getKey());
            BeanPropertyDefinition property = properties.get(key);
            appendField(output, displayLabel(key, property), entry.getValue(),
                    propertyValue(original, key, property), depth);
        }
    }

    private static boolean inline(Object original) {
        return original != null && original.getClass().isAnnotationPresent(CommandTextInline.class);
    }

    private void appendInline(StringBuilder output, Map<?, ?> map, Object original, int depth) {
        Map<String, BeanPropertyDefinition> properties = properties(original);
        List<String> columns = new ArrayList<>();
        map.forEach((key, value) -> {
            if (!(value instanceof Map<?, ?>) && !(value instanceof Collection<?>)) {
                columns.add(displayLabel(String.valueOf(key), properties.get(String.valueOf(key)))
                        + ": " + scalar(value));
            }
        });
        output.append(String.join(" | ", columns)).append('\n');
        // 行内布局只压缩标量；奖励、阶段等嵌套数据仍完整排版，不因模式不同而丢失。
        map.forEach((key, value) -> {
            if (value instanceof Map<?, ?> || value instanceof Collection<?>) {
                String name = String.valueOf(key);
                var property = properties.get(name);
                appendField(output, displayLabel(name, property), value,
                        propertyValue(original, name, property), depth + 1);
            }
        });
    }

    private static String scalar(Object value) {
        return value == null ? "未提供" : value instanceof Boolean flag
                ? (flag ? "是" : "否") : String.valueOf(value);
    }

    private void appendCollection(StringBuilder output, Collection<?> collection, Object original, int depth) {
        List<?> originals = original instanceof Collection<?> values
                ? new ArrayList<>(values) : List.of();
        int index = 0;
        for (Object value : collection) {
            Object source = index < originals.size() ? originals.get(index)
                    : original != null && original.getClass().isArray() && index < Array.getLength(original)
                    ? Array.get(original, index) : null;
            index++;
            indent(output, depth);
            output.append("- ");
            if (value instanceof Map<?, ?> map) {
                if (inline(source)) {
                    appendInline(output, map, source, depth);
                } else {
                    output.append('\n');
                    appendMap(output, map, source, depth + 1);
                }
            } else if (value instanceof Collection<?> nested) {
                output.append('\n');
                appendCollection(output, nested, source, depth + 1);
            } else {
                output.append(value == null ? "未提供" : value instanceof Boolean flag
                        ? (flag ? "是" : "否") : value).append('\n');
            }
        }
    }

    /** 使用 Jackson 的属性定义读取说明，兼容 record、getter、重命名与 JsonIgnore。 */
    private Map<String, BeanPropertyDefinition> properties(Object value) {
        if (value == null || value instanceof Map<?, ?> || value instanceof Collection<?>
                || value.getClass().isArray()) {
            return Map.of();
        }
        Map<String, BeanPropertyDefinition> result = new LinkedHashMap<>();
        objectMapper.getSerializationConfig().introspect(objectMapper.constructType(value.getClass()))
                .findProperties().forEach(property -> result.put(property.getName(), property));
        return result;
    }

    private static Object propertyValue(Object owner, String key, BeanPropertyDefinition property) {
        if (owner instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .filter(entry -> String.valueOf(entry.getKey()).equals(key))
                    .map(Map.Entry::getValue).filter(Objects::nonNull)
                    .findFirst().orElse(null);
        }
        if (owner == null || property == null || property.getAccessor() == null) {
            return null;
        }
        property.getAccessor().fixAccess(true);
        return property.getAccessor().getValue(owner);
    }

    private static String displayLabel(String key, BeanPropertyDefinition property) {
        String description = property == null ? null : property.getMetadata().getDescription();
        return StrUtil.isBlank(description) ? ("items".equals(key) ? "项目" : key) : description;
    }

    private static void indent(StringBuilder output, int depth) {
        output.append("  ".repeat(Math.max(0, depth)));
    }

}
