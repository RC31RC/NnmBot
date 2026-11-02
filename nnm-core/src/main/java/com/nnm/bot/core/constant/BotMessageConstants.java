package com.nnm.bot.core.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class BotMessageConstants {
    private BotMessageConstants() {}

    public enum BotIncomingAttachmentType {
        IMAGE, FILE
    }

    /** 平台普通消息 ID 与 QQ 引用索引不能互换使用。 */
    public enum ReferenceKind {
        MESSAGE_ID, REFERENCE_INDEX
    }

    public enum PartKind {
        TEXT, IMAGE, MARKDOWN, FILE, KEYBOARD
    }
}
