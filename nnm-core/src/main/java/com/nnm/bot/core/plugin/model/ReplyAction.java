package com.nnm.bot.core.plugin.model;

import java.net.URI;
import java.util.Objects;

/** 动作只描述导航或填入文本，不携带平台 JSON，也不直接执行业务。 */
public sealed interface ReplyAction {
    String label();
    String value();
    boolean required();

    record Link(String label, String value, boolean required) implements ReplyAction {
        public Link {
            label = checked(label, 80);
            value = checked(value, 2048);
            URI uri = URI.create(value);
            if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null || uri.getRawUserInfo() != null) {
                throw new IllegalArgumentException("Action URL must be absolute HTTP(S) without credentials");
            }
        }
    }

    record Command(String label, String value, boolean required) implements ReplyAction {
        public Command {
            label = checked(label, 80);
            value = checked(value, 512);
        }
    }

    private static String checked(String value, int maxLength) {
        String text = Objects.requireNonNull(value, "action text").strip();
        if (text.isEmpty() || text.length() > maxLength || text.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Invalid action text");
        }
        return text;
    }
}
