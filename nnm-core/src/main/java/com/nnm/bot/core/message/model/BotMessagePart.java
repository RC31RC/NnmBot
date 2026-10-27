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
            if (buttons.isEmpty() || buttons.size() > 10) throw new IllegalArgumentException("Keyboard requires 1–10 buttons");
        }
    }

    record Button(String label, String value, boolean link) {
        public Button {
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(value, "value");
            if (label.isBlank() || label.length() > 80 || value.isBlank() || value.length() > 2048
                    || label.chars().anyMatch(Character::isISOControl) || value.chars().anyMatch(Character::isISOControl)) {
                throw new IllegalArgumentException("Invalid keyboard button");
            }
        }
    }

    record Markdown(BotMarkdownMessage message) implements BotMessagePart {
        public Markdown {
            Objects.requireNonNull(message, "message");
        }
    }
}
