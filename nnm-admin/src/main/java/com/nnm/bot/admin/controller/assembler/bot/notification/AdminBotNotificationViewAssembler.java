package com.nnm.bot.admin.controller.assembler.bot.notification;

import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationReportDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationJobDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationPreviewDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationResourceDTO;
import com.nnm.bot.admin.pojo.vo.bot.notification.BotGroupNotificationReportVO;
import com.nnm.bot.admin.pojo.vo.bot.notification.BotNotificationJobVO;
import com.nnm.bot.admin.pojo.vo.bot.notification.BotNotificationPreviewVO;
import com.nnm.bot.admin.pojo.vo.bot.notification.BotNotificationResourceVO;
import java.util.List;

/** 在 HTTP 展示边界转换数据，保持服务传输合同独立。 */
public final class AdminBotNotificationViewAssembler {
    private AdminBotNotificationViewAssembler() {
    }

    public static BotGroupNotificationReportVO toBotGroupNotificationReportVO(BotGroupNotificationReportDTO value) {
        if (value == null) return null;
        return new BotGroupNotificationReportVO(
                value.succeeded(),
                value.failed(),
                value.skipped(),
                value.deliveries() == null ? null : value.deliveries().stream().map(AdminBotNotificationViewAssembler::toBotGroupNotificationReportDTODeliveryVO).toList());
    }

    public static BotGroupNotificationReportVO.DeliveryVO toBotGroupNotificationReportDTODeliveryVO(BotGroupNotificationReportDTO.DeliveryDTO value) {
        if (value == null) return null;
        return new BotGroupNotificationReportVO.DeliveryVO(
                value.groupId(),
                value.groupName(),
                value.platform(),
                value.conversationId(),
                value.status(),
                value.message(),
                value.report());
    }

    public static BotNotificationJobVO toBotNotificationJobVO(BotNotificationJobDTO value) {
        if (value == null) return null;
        return new BotNotificationJobVO(
                value.id(),
                value.status(),
                value.scheduledAt(),
                value.taskId(),
                value.groupIds(),
                AdminBotNotificationViewAssembler.toBotGroupNotificationReportVO(value.report()),
                value.error());
    }

    public static List<BotNotificationJobVO> toBotNotificationJobVO(List<BotNotificationJobDTO> values) {
        return values.stream().map(AdminBotNotificationViewAssembler::toBotNotificationJobVO).toList();
    }

    public static BotNotificationPreviewVO toBotNotificationPreviewVO(BotNotificationPreviewDTO value) {
        if (value == null) return null;
        return new BotNotificationPreviewVO(
                value.groupId(),
                value.groupName(),
                value.platform(),
                value.botAccountId(),
                value.conversationId(),
                value.feasible(),
                value.reason(),
                value.messages() == null ? null : value.messages().stream().map(AdminBotNotificationViewAssembler::toBotNotificationPreviewDTOMessageVO).toList());
    }

    public static List<BotNotificationPreviewVO> toBotNotificationPreviewVO(List<BotNotificationPreviewDTO> values) {
        return values.stream().map(AdminBotNotificationViewAssembler::toBotNotificationPreviewVO).toList();
    }

    public static BotNotificationPreviewVO.MessageVO toBotNotificationPreviewDTOMessageVO(BotNotificationPreviewDTO.MessageDTO value) {
        if (value == null) return null;
        return new BotNotificationPreviewVO.MessageVO(
                value.logicalMessage(),
                value.part(),
                value.blocks(),
                value.text(),
                value.actions() == null ? null : value.actions().stream().map(AdminBotNotificationViewAssembler::toBotNotificationPreviewDTOActionVO).toList());
    }

    public static BotNotificationPreviewVO.ActionVO toBotNotificationPreviewDTOActionVO(BotNotificationPreviewDTO.ActionDTO value) {
        if (value == null) return null;
        return new BotNotificationPreviewVO.ActionVO(
                value.label(),
                value.value(),
                value.presentation());
    }

    public static BotNotificationResourceVO toBotNotificationResourceVO(BotNotificationResourceDTO value) {
        if (value == null) return null;
        return new BotNotificationResourceVO(
                value.id(),
                value.contentType(),
                value.size(),
                value.width(),
                value.height(),
                value.expiresAt());
    }

}
