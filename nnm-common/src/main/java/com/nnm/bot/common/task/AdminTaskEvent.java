package com.nnm.bot.common.task;

import java.time.Instant;
import java.util.UUID;

public record AdminTaskEvent(
        UUID taskId,
        String taskType,
        AdminTaskStatus status,
        String stage,
        String message,
        long current,
        long total,
        int percent,
        Object result,
        String error,
        Integer errorCode,
        String errorType,
        long sequence,
        Instant updatedAt) {
}
