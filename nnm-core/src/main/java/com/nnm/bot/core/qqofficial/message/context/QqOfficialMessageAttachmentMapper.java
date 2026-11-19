package com.nnm.bot.core.qqofficial.message.context;

import com.nnm.bot.core.messaging.constant.BotMessageConstants.BotIncomingAttachmentType;
import com.nnm.bot.core.messaging.model.BotIncomingAttachment;
import com.nnm.bot.core.qqofficial.gateway.dto.QqOfficialGatewayEventDTO;
import com.nnm.bot.core.qqofficial.gateway.dto.QqOfficialGatewayEventDTO.AttachmentDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 将 QQ 官方消息附件字段映射为平台无关附件。 */
public final class QqOfficialMessageAttachmentMapper {

    private QqOfficialMessageAttachmentMapper() {}

    public static List<BotIncomingAttachment> from(QqOfficialGatewayEventDTO event) {
        return event == null ? List.of() : from(event.attachments());
    }

    public static List<BotIncomingAttachment> from(List<AttachmentDTO> nodes) {
        if (nodes == null)
            return List.of();
        List<BotIncomingAttachment> attachments = new ArrayList<>();
        for (AttachmentDTO node : nodes) {
            if (node == null)
                continue;
            String contentType = firstText(node.contentType(), node.mimeType(), node.mime());
            String fileName = firstText(node.filename(), node.fileName(), node.name());
            String url = firstText(node.url(), node.downloadUrl());
            String fileId = firstText(node.id(), node.fileId());
            String type = firstText(node.type(), node.contentType());
            BotIncomingAttachmentType attachmentType =
                isImage(type, contentType, fileName) ? BotIncomingAttachmentType.IMAGE : BotIncomingAttachmentType.FILE;
            attachments.add(new BotIncomingAttachment(attachmentType, fileName, contentType, url, fileId,
                positiveLong(node.size(), node.fileSize()), positiveInt(node.width()), positiveInt(node.height()),
                Map.of("platformType", type == null ? "unknown" : type)));
        }
        return List.copyOf(attachments);
    }

    private static boolean isImage(String type, String contentType, String fileName) {
        return (type != null && type.toLowerCase().contains("image"))
            || (contentType != null && contentType.toLowerCase().startsWith("image/"))
            || (fileName != null && contentTypeFromName(fileName) != null);
    }

    private static Long positiveLong(String... fields) {
        for (String field : fields) {
            if (field == null)
                continue;
            try {
                long value = Long.parseLong(field);
                if (value > 0)
                    return value;
            } catch (NumberFormatException ignored) {
                // 附件大小无效时继续映射其它可用字段。
            }
        }
        return null;
    }

    private static Integer positiveInt(String field) {
        Long value = positiveLong(field);
        return value != null && value <= Integer.MAX_VALUE ? value.intValue() : null;
    }

    private static String contentTypeFromName(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png"))
            return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg"))
            return "image/jpeg";
        if (lower.endsWith(".webp"))
            return "image/webp";
        if (lower.endsWith(".gif"))
            return "image/gif";
        return null;
    }

    private static String firstText(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank())
                return value;
        }
        return null;
    }
}
