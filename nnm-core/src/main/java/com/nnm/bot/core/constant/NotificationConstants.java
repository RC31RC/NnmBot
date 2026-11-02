package com.nnm.bot.core.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class NotificationConstants {
    private NotificationConstants() {}

    public enum BlockType {
        TEXT, MARKDOWN, IMAGE
    }

    public enum ActionType {
        COMMAND, LINK
    }

    public enum GroupResultStatus {
        IN_PROGRESS, CANCELLED, SUCCEEDED, PARTIAL, FAILED, UNKNOWN
    }

    /** 通知请求的操作互斥用途，键名与既有消费者保持一致。 */
    public enum LockPurpose {
        REQUEST("request");

        private final String key;

        LockPurpose(String key) {
            this.key = key;
        }

        /** 返回请求互斥的稳定操作名。 */
        public String key() {
            return key;
        }
    }
}
