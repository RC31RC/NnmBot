package com.nnm.bot.core.message.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

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
 *
 * @param content 直接发送的 Markdown 内容
 * @param customTemplateId QQ 官方 Markdown 模板 ID
 * @param params 模板参数
 */
public record BotMarkdownMessage(String content,
                                 String customTemplateId,
                                 Map<String, List<String>> params) {

    public BotMarkdownMessage {
        content = StrUtil.isBlank(content) ? null : content;
        customTemplateId = StrUtil.isBlank(customTemplateId)
                ? null
                : customTemplateId.trim();
        params = immutableParams(params);
        if (ObjectUtil.isNull(content) && ObjectUtil.isNull(customTemplateId)) {
            throw new IllegalArgumentException(
                    "content or customTemplateId must not be blank");
        }
        if (ObjectUtil.isNotNull(content) && ObjectUtil.isNotNull(customTemplateId)) {
            throw new IllegalArgumentException(
                    "content and customTemplateId cannot both be configured");
        }
        if (ObjectUtil.isNull(customTemplateId) && !params.isEmpty()) {
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
        if (MapUtil.isEmpty(source)) {
            return Map.of();
        }
        Map<String, List<String>> copy = new LinkedHashMap<>();
        source.forEach((key, values) -> {
            String normalizedKey = requireText(key, "parameter key");
            if (CollUtil.isEmpty(values)) {
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
