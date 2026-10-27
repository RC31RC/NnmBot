package com.nnm.bot.core.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class NotificationConstants {
    private NotificationConstants() {
    }

    public enum BlockType {
        TEXT, MARKDOWN, IMAGE
    }

    public enum ActionType {
        COMMAND, LINK
    }

    public enum GroupResultStatus {
        IN_PROGRESS,
        CANCELLED,
        SUCCEEDED,
        PARTIAL,
        FAILED,
        UNKNOWN
    }
}
