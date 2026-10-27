package com.nnm.bot.core.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class BotMessageConstants {
    private BotMessageConstants() {
    }

    public enum BotIncomingAttachmentType {
        IMAGE,
        FILE
    }

    public enum PartKind {
        TEXT, IMAGE, MARKDOWN, FILE, KEYBOARD
    }
}
