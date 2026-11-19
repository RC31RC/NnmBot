package com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot;

import java.util.UUID;

/** 官方静态资料后台任务的提交结果。 */
public record WarframeOfficialTaskSubmissionVO(UUID taskId, String taskType) {
}
