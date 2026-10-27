package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.config.ReplyProperties;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/** 只读取显式投影字段，不执行表达式、Bean 调用或仓储访问。 */
@Component
public class MarkdownViewRenderer {
    private static final Pattern FIELD = Pattern.compile("\\{\\{([a-zA-Z_][a-zA-Z0-9_.]*)}}");
    private static final Pattern LOOP = Pattern.compile("\\{\\{#([a-zA-Z_][a-zA-Z0-9_.]*)}}([\\s\\S]*?)\\{\\{/\\1}}");
    @Resource
    private ReplyProperties limits;

    public String render(String template, Map<String, Object> data, boolean markdown) {
        if (template == null || template.isBlank()) throw new IllegalArgumentException("Missing view template");
        checkSize(template.length());
        Matcher loops = LOOP.matcher(template);
        StringBuilder expanded = new StringBuilder();
        int end = 0;
        while (loops.find()) {
            expanded.append(fields(template.substring(end, loops.start()), data, markdown));
            Object value = field(data, loops.group(1));
            if (!(value instanceof Iterable<?> items)) throw new IllegalArgumentException("Template loop requires a list");
            int count = 0;
            for (Object item : items) {
                if (++count > limits.getMaxTextCharacters()) throw new IllegalArgumentException("Template loop limit exceeded");
                expanded.append(fields(loops.group(2), item instanceof Map<?, ?> map ? map : Map.of("item", item), markdown));
                checkSize(expanded.length());
            }
            end = loops.end();
        }
        expanded.append(fields(template.substring(end), data, markdown));
        String result = expanded.toString();
        checkSize(result.length());
        if (result.isBlank()) throw new IllegalArgumentException("View template produced empty content");
        return result;
    }

    private String fields(String template, Map<?, ?> data, boolean markdown) {
        if (FIELD.matcher(template).replaceAll("").contains("{{")) {
            throw new IllegalArgumentException("Unsupported template expression");
        }
        Matcher matcher = FIELD.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            Object value = field(data, matcher.group(1));
            if (!(value instanceof CharSequence || value instanceof Number || value instanceof Boolean)) {
                throw new IllegalArgumentException("Template field must be a scalar: " + matcher.group(1));
            }
            String text = value.toString();
            if (markdown) text = text.replaceAll("([\\\\`*_{}\\[\\]()<>#!|])", "\\\\$1");
            matcher.appendReplacement(result, Matcher.quoteReplacement(text));
            checkSize(result.length());
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private Object field(Map<?, ?> data, String path) {
        Object value = data;
        for (String key : path.split("\\.")) value = value instanceof Map<?, ?> map ? map.get(key) : null;
        if (value == null) throw new IllegalArgumentException("Missing template field: " + path);
        return value;
    }

    private void checkSize(int size) {
        if (size > limits.getMaxTextCharacters()) throw new IllegalArgumentException("Rendered view text limit exceeded");
    }
}
