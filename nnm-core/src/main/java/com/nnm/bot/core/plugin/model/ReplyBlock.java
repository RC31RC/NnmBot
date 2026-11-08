package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.message.model.BotMarkdownMessage;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public sealed interface ReplyBlock {
    record Text(String content) implements ReplyBlock {
        public Text {
            Objects.requireNonNull(content, "content");
            if (content.isBlank())
                throw new IllegalArgumentException("Empty text block");
        }
    }

    /** 待用当前命令选定的 HTML 模板渲染的数据；DTO 自身由生产者保证不可变。 */
    record HtmlData<T>(T data) implements ReplyBlock {
        @SuppressWarnings("unchecked")
        public HtmlData {
            data = (T)snapshot(Objects.requireNonNull(data, "data"));
        }
    }

    record Image(CommandAttachment resource) implements ReplyBlock {
        public Image {
            Objects.requireNonNull(resource, "resource");
            if (!resource.contentType().startsWith("image/")) {
                throw new IllegalArgumentException("Image resource must have an image MIME type");
            }
        }
    }

    record Markdown(BotMarkdownMessage message, String plainText) implements ReplyBlock {
        public Markdown {
            Objects.requireNonNull(message, "message");
            if (plainText != null && plainText.isBlank()) {
                throw new IllegalArgumentException("Declared Markdown fallback must be nonempty");
            }
            if (message.customTemplateId() != null && plainText == null) {
                throw new IllegalArgumentException("Custom Markdown templates require a plain-text fallback");
            }
        }

        public String plainTextContent() {
            if (plainText != null)
                return plainText;
            return message.plainTextContent();
        }
    }

    record File(CommandAttachment resource) implements ReplyBlock {
        public File {
            Objects.requireNonNull(resource, "resource");
        }
    }

    private static Object snapshot(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            map.forEach((key, item) -> {
                if (!(key instanceof String name))
                    throw new IllegalArgumentException("Data keys must be strings");
                copy.put(name, snapshot(item));
            });
            return Collections.unmodifiableMap(copy);
        }
        if (value instanceof Collection<?> collection)
            return collection.stream().map(ReplyBlock::snapshot).toList();
        if (value != null && value.getClass().isArray()) {
            throw new IllegalArgumentException("Use immutable lists instead of arrays in HTML data");
        }
        return value;
    }
}
