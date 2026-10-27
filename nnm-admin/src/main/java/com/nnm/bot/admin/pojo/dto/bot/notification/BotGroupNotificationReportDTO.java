package com.nnm.bot.admin.pojo.dto.bot.notification;

import java.util.List;
import com.nnm.bot.core.plugin.model.DeliveryReport;

public record BotGroupNotificationReportDTO(int succeeded, int failed, int skipped, List<DeliveryDTO> deliveries) {
    public record DeliveryDTO(long groupId, String groupName, String platform, String conversationId,
                           String status, String message, DeliveryReport report) { }
}
