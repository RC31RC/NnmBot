package com.nnm.bot.core.message;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Markdown 模板消息。
 *
 * <p>QQ 官方机器人既支持直接 Markdown 内容，也支持自定义模板 ID 和参数，
 * 因此模型同时保留这两种表达方式。不同平台不支持该能力时，由路由器抛出明确的异常。</p>
 */
public record BotMarkdownMessage(String content,
                                 String customTemplateId,
                                 Map<String, List<String>> params) {

    public BotMarkdownMessage {
        content = content == null || content.isBlank() ? null : content;
        customTemplateId = customTemplateId == null || customTemplateId.isBlank()
                ? null
                : customTemplateId.trim();
        params = immutableParams(params);
        if (content == null && customTemplateId == null) {
            throw new IllegalArgumentException(
                    "content or customTemplateId must not be blank");
        }
        if (content != null && customTemplateId != null) {
            throw new IllegalArgumentException(
                    "content and customTemplateId cannot both be configured");
        }
        if (customTemplateId == null && !params.isEmpty()) {
            throw new IllegalArgumentException(
                    "params require customTemplateId");
        }
    }

    public static BotMarkdownMessage content(String content) {
        return new BotMarkdownMessage(content, null, Map.of());
    }

    public static BotMarkdownMessage template(String customTemplateId) {
        return new BotMarkdownMessage(null, customTemplateId, Map.of());
    }

    public static BotMarkdownMessage template(String customTemplateId,
                                              Map<String, List<String>> params) {
        return new BotMarkdownMessage(null, customTemplateId, params);
    }

    private static Map<String, List<String>> immutableParams(
            Map<String, List<String>> source) {
        if (source == null || source.isEmpty()) {
            return Map.of();
        }
        Map<String, List<String>> copy = new LinkedHashMap<>();
        source.forEach((key, values) -> {
            String normalizedKey = requireText(key, "parameter key");
            if (values == null || values.isEmpty()) {
                throw new IllegalArgumentException("parameter values must not be empty: " + normalizedKey);
            }
            List<String> normalizedValues = values.stream()
                    .map(value -> Objects.requireNonNull(value, "parameter value must not be null"))
                    .toList();
            copy.put(normalizedKey, normalizedValues);
        });
        return Collections.unmodifiableMap(copy);
    }

    private static String requireText(String value, String field) {
        String normalized = Objects.requireNonNull(value, field + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }

}
