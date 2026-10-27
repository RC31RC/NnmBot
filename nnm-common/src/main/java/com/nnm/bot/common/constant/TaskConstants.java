package com.nnm.bot.common.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class TaskConstants {
    private TaskConstants() {
    }

    public enum AdminTaskStatus {
        QUEUED,
        RUNNING,
        COMPLETED,
        FAILED
    }
}
