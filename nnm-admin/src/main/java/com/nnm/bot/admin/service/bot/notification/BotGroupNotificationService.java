package com.nnm.bot.admin.service.bot.notification;

import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationReportDTO;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.core.mq.message.notification.NotificationResourceManifest;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationPreviewDTO;
import java.util.List;

public interface BotGroupNotificationService {
    BotGroupNotificationReportDTO send(BotGroupNotificationDTO request,
            NotificationResourceManifest manifest, TaskProgressReporter reporter) throws InterruptedException;
    List<BotNotificationPreviewDTO> preview(long ownerId, BotGroupNotificationDTO request);
}
