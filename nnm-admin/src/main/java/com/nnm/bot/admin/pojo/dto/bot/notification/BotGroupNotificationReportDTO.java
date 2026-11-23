package com.nnm.bot.admin.pojo.dto.bot.notification;

import com.nnm.bot.admin.constant.bot.notification.BotNotificationConstants.GroupDeliveryStatus;
import com.nnm.bot.core.plugin.response.DeliveryReport;
import java.util.List;

/** 群通知的聚合计数和逐目标结果。 */
public record BotGroupNotificationReportDTO(int succeeded, int failed, int skipped, List<DeliveryDTO> deliveries) {
    /** 单个目标的投递结果与可核对的发送明细。 */
    public record DeliveryDTO(long groupId, String groupName, String platform, String conversationId,
        GroupDeliveryStatus status, String message, DeliveryReport report) {
    }
}
