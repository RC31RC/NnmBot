package com.nnm.bot.admin.pojo.vo.task;

import java.util.UUID;

/** 管理端异步任务的提交结果。 */
public record AdminTaskSubmissionVO(UUID taskId, String taskType) {
}
