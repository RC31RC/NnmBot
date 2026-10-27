package com.nnm.bot.admin.pojo.vo.bot.notification;

import java.util.List;
import com.nnm.bot.core.plugin.model.DeliveryReport;

public record BotGroupNotificationReportVO(int succeeded, int failed, int skipped, List<DeliveryVO> deliveries) {
    public record DeliveryVO(long groupId, String groupName, String platform, String conversationId,
                           String status, String message, DeliveryReport report) { }
}
