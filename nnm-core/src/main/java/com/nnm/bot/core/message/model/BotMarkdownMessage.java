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
import java.net.URI;

/**
 * Markdown 消息及可选的消息内按钮；渠道可将按钮转换为原生交互控件。
 *
 * <p>
 * QQ 官方机器人既支持直接 Markdown 内容，也支持自定义模板 ID 和参数，因此模型同时保留这两种表达方式。
 * </p>
 *
 * @param content 直接发送的 Markdown 内容
 * @param customTemplateId QQ 官方 Markdown 模板 ID
 * @param params 模板参数
 * @param buttons 随 Markdown 消息展示的按钮，row 从 0 开始
 */
public record BotMarkdownMessage(String content, String customTemplateId, Map<String, List<String>> params,
    List<Button> buttons) {

    public BotMarkdownMessage {
        content = StrUtil.isBlank(content) ? null : content;
        customTemplateId = StrUtil.isBlank(customTemplateId) ? null : customTemplateId.trim();
        params = immutableParams(params);
        buttons = buttons == null ? List.of() : List.copyOf(buttons);
        if (ObjectUtil.isNull(content) && ObjectUtil.isNull(customTemplateId)) {
            throw new IllegalArgumentException("content or customTemplateId must not be blank");
        }
        if (ObjectUtil.isNotNull(content) && ObjectUtil.isNotNull(customTemplateId)) {
            throw new IllegalArgumentException("content and customTemplateId cannot both be configured");
        }
        if (ObjectUtil.isNull(customTemplateId) && !params.isEmpty()) {
            throw new IllegalArgumentException("params require customTemplateId");
        }
        validateButtons(buttons);
    }

    public static BotMarkdownMessage content(String content) {
        return new BotMarkdownMessage(content, null, Map.of(), List.of());
    }

    public static BotMarkdownMessage template(String customTemplateId) {
        return new BotMarkdownMessage(null, customTemplateId, Map.of(), List.of());
    }

    public static BotMarkdownMessage template(String customTemplateId, Map<String, List<String>> params) {
        return new BotMarkdownMessage(null, customTemplateId, params, List.of());
    }

    public BotMarkdownMessage withButtons(List<Button> buttons) {
        return new BotMarkdownMessage(content, customTemplateId, params, buttons);
    }

    public BotMarkdownMessage withoutButtons() {
        return buttons.isEmpty() ? this : withButtons(List.of());
    }

    public String plainTextContent() {
        if (content == null)
            return "";
        return content.replaceAll("(?m)^[ \t]{0,3}`{3}[\\w+.-]*[ \t]*\\R", "")
            .replaceAll("(?m)^[ \t]{0,3}`{3}[ \t]*$", "").replaceAll("(?m)^\\s{0,3}#{1,6}\\s*", "")
            .replaceAll("(?m)^\\s*>\\s?", "").replaceAll("(?m)^\\s*[-+*]\\s+", "")
            .replaceAll("(?m)^\\s*\\d+[.)]\\s+", "").replaceAll("!?\\[([^]]*)]\\(([^)]*)\\)", "$1：$2")
            .replaceAll("!?\\[([^]]*)]\\[([^]]*)]", "$1 [$2]").replaceAll("`{1,3}", "")
            .replaceAll("(?<!\\w)(\\*\\*|__|~~|\\*|_)(?=\\S)", "").replaceAll("(?<=\\S)(\\*\\*|__|~~|\\*|_)(?!\\w)", "")
            .replaceAll("(?i)<(https?://[^>\\s]+)>", "$1").replaceAll("<[^>]+>", "").strip();
    }

    public record Button(String label, String value, boolean link, int row) {
        public Button {
            label = requireText(label, "button label");
            value = requireText(value, "button value");
            int maxLength = link ? 2048 : 512;
            if (label.length() > 80 || value.length() > maxLength || row < 0 || row > 4
                || label.chars().anyMatch(Character::isISOControl) || value.chars().anyMatch(Character::isISOControl)) {
                throw new IllegalArgumentException("Invalid Markdown button");
            }
            if (link) {
                URI uri = URI.create(value);
                if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null || uri.getRawUserInfo() != null) {
                    throw new IllegalArgumentException("Button URL must be absolute HTTP(S) without credentials");
                }
            }
        }
    }

    private static void validateButtons(List<Button> buttons) {
        if (buttons.size() > 25)
            throw new IllegalArgumentException("Markdown message cannot contain more than 25 buttons");
        int previousRow = -1;
        int rowSize = 0;
        for (Button button : buttons) {
            if (button.row() < previousRow || button.row() > previousRow + 1)
                throw new IllegalArgumentException("Markdown button rows must be contiguous and ordered");
            if (button.row() != previousRow) {
                previousRow = button.row();
                rowSize = 0;
            }
            if (++rowSize > 5)
                throw new IllegalArgumentException("Markdown button rows cannot contain more than 5 buttons");
        }
    }

    private static Map<String, List<String>> immutableParams(Map<String, List<String>> source) {
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
                .map(value -> Objects.requireNonNull(value, "parameter value must not be null")).toList();
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
