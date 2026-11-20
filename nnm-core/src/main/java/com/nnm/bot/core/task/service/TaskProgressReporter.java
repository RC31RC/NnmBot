package com.nnm.bot.core.task.service;

/** 向管理端任务中心报告当前阶段及进度。 */
@FunctionalInterface
public interface TaskProgressReporter {

    /**
     * 发布进度快照。
     *
     * @param stage 当前阶段标识
     * @param message 面向管理端的阶段说明
     * @param current 当前完成量
     * @param total 总量；未知时可传 0
     */
    void report(String stage, String message, long current, long total);

    default void report(String stage, String message) {
        report(stage, message, 0, 0);
    }

    default void complete(String message) {
        report("COMPLETED", message, 1, 1);
    }

    static TaskProgressReporter noop() {
        return (stage, message, current, total) -> {
        };
    }
}
