package com.nnm.bot.admin.pojo.vo.task;

import com.nnm.bot.core.task.constant.TaskConstants.AdminTaskStatus;
import java.time.Instant;
import java.util.UUID;

/** 管理端任务状态及最近一次进度快照。 */
public record AdminTaskVO(UUID taskId, String taskType, AdminTaskStatus status, String stage, String message,
    long current, long total, int percent, Object result, String error, Integer errorCode, String errorType,
    long sequence, Instant updatedAt) {
}
