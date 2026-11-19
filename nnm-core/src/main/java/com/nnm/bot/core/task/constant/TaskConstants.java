package com.nnm.bot.core.task.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class TaskConstants {
    private TaskConstants() {}

    /** 管理端异步任务的持久化与 API 状态。 */
    public enum AdminTaskStatus {
        QUEUED, RUNNING, COMPLETED, FAILED, CANCELLED
    }
}
