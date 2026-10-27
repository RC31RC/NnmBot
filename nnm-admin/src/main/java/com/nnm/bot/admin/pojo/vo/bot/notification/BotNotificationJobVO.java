package com.nnm.bot.admin.pojo.vo.bot.notification;

import com.nnm.bot.admin.constant.BotNotificationConstants.JobStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BotNotificationJobVO(UUID id, JobStatus status, Instant scheduledAt, UUID taskId,
                                      List<Long> groupIds, BotGroupNotificationReportVO report, String error) { }
