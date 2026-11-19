package com.nnm.bot.core.task.pojo.dto;

import com.nnm.bot.core.task.constant.TaskConstants.AdminTaskStatus;

import java.time.Instant;
import java.util.UUID;

/** 管理端异步任务可订阅的进度快照。 */
public record AdminTaskDTO(UUID taskId, String taskType, AdminTaskStatus status, String stage, String message,
    long current, long total, int percent, Object result, String error, Integer errorCode, String errorType,
    long sequence, Instant updatedAt) {
}
