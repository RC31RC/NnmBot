package com.nnm.bot.admin.pojo.dto.bot.notification;

import com.nnm.bot.admin.constant.BotNotificationConstants.JobStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BotNotificationJobDTO(UUID id, JobStatus status, Instant scheduledAt, UUID taskId,
                                      List<Long> groupIds, BotGroupNotificationReportDTO report, String error) { }
