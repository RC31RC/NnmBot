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
            if (content.isBlank()) throw new IllegalArgumentException("Empty text block");
        }
    }

    /** 保留 DTO 类型；集合递归快照，DTO 自身由生产者保证不可变。 */
    record DataView<T>(String viewKey, T data, String summary) implements ReplyBlock {
        @SuppressWarnings("unchecked")
        public DataView {
            if (viewKey == null || !viewKey.matches("[a-z][a-z0-9_.-]{0,79}")) {
                throw new IllegalArgumentException("Invalid viewKey");
            }
            data = (T) snapshot(Objects.requireNonNull(data, "data"));
            if (summary != null) {
                summary = summary.strip();
                if (summary.isEmpty() || summary.length() > 4000
                        || summary.chars().anyMatch(Character::isISOControl)) {
                    throw new IllegalArgumentException("Invalid view summary");
                }
            }
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
                if (!(key instanceof String name)) throw new IllegalArgumentException("Data keys must be strings");
                copy.put(name, snapshot(item));
            });
            return Collections.unmodifiableMap(copy);
        }
        if (value instanceof Collection<?> collection) return collection.stream().map(ReplyBlock::snapshot).toList();
        if (value != null && value.getClass().isArray()) {
            throw new IllegalArgumentException("Use immutable lists instead of arrays in view data");
        }
        return value;
    }
}
