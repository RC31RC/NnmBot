package com.nnm.bot.common.task;

@FunctionalInterface
public interface TaskProgressReporter {

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
