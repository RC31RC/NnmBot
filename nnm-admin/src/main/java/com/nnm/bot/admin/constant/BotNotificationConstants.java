package com.nnm.bot.admin.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class BotNotificationConstants {
    private BotNotificationConstants() {
    }

    public enum JobStatus {
        SCHEDULED, SUBMITTING, RUNNING, CANCEL_REQUESTED, COMPLETED, CANCELLED, FAILED, UNKNOWN;

        public boolean terminal() {
            return this == COMPLETED || this == CANCELLED || this == FAILED || this == UNKNOWN;
        }
    }
}
