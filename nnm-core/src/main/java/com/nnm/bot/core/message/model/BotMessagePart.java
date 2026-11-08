package com.nnm.bot.core.message.model;

import java.util.Objects;
import java.util.List;

/** 已编译的消息段；传输层不依赖插件业务视图，也不决定展示降级。 */
public sealed interface BotMessagePart {

    record Text(String content) implements BotMessagePart {
        public Text {
            Objects.requireNonNull(content, "content");
        }
    }

    record Image(BotFile resource) implements BotMessagePart {
        public Image {
            Objects.requireNonNull(resource, "resource");
        }
    }

    record File(BotFile resource) implements BotMessagePart {
        public File {
            Objects.requireNonNull(resource, "resource");
        }
    }

    record Keyboard(List<Button> buttons) implements BotMessagePart {
        public Keyboard {
            buttons = List.copyOf(buttons);
            if (buttons.isEmpty() || buttons.size() > 25)
                throw new IllegalArgumentException("Keyboard requires 1–25 buttons");
        }
    }

    record Button(String label, String value, boolean link, int row) {
        public Button {
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(value, "value");
            if (label.isBlank() || label.length() > 80 || value.isBlank() || value.length() > 2048 || row < 0 || row > 4
                || label.chars().anyMatch(Character::isISOControl) || value.chars().anyMatch(Character::isISOControl)) {
                throw new IllegalArgumentException("Invalid keyboard button");
            }
        }
    }

    record Markdown(BotMarkdownMessage message, String plainText) implements BotMessagePart {
        public Markdown {
            Objects.requireNonNull(message, "message");
            if (plainText == null)
                plainText = message.plainTextContent();
        }
    }
}
