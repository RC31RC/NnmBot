package com.nnm.bot.core.task.pojo.dto;

import java.util.UUID;

/** 异步任务提交后立即返回的任务标识。 */
public record AdminTaskSubmissionDTO(UUID taskId, String taskType) {
}
