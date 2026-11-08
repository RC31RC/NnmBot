package com.nnm.bot.core.qqofficial.message;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.constant.BotMessageConstants.BotIncomingAttachmentType;
import com.nnm.bot.core.constant.BotMessageConstants.ReferenceKind;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.QqOfficialChatType;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotMessageReference;
import com.nnm.bot.core.qqofficial.message.dto.QqOfficialGatewayEventDTO;
import com.nnm.bot.core.qqofficial.message.dto.QqOfficialGatewayEventDTO.AttachmentDTO;
import com.nnm.bot.core.qqofficial.message.dto.QqOfficialGatewayEventDTO.ReferencedMessageDTO;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 将类型化 QQ Gateway 事件转换为平台无关上下文；原始 JSON 用于源协议追踪。 */
public final class QqOfficialMessageContextFactory {
    private static final int REFERENCE_MESSAGE_TYPE = 103;

    private QqOfficialMessageContextFactory() {}

    public static Optional<BotMessageContext> from(String appId, String eventType, QqOfficialGatewayEventDTO event,
        String rawJson) {
        QqOfficialChatType chatType = chatType(eventType);
        if (chatType == null || event == null)
            return Optional.empty();
        var author = event.author();
        String senderId = switch (chatType) {
            case C2C -> author == null ? null : text(author.userOpenid());
            case GROUP -> author == null ? null : firstText(author.memberOpenid(), author.userOpenid());
            case CHANNEL, DIRECT -> author == null ? null : text(author.id());
        };
        String targetId = switch (chatType) {
            case C2C -> senderId;
            case GROUP -> text(event.groupOpenid());
            case CHANNEL -> text(event.channelId());
            case DIRECT -> text(event.guildId());
        };
        String messageId = text(event.id());
        if (StrUtil.isBlank(appId) || senderId == null || targetId == null || messageId == null)
            return Optional.empty();
        String content = normalizeContent(event.content() == null ? "" : event.content(), appId);
        BotMessageScope scope = switch (chatType) {
            case GROUP -> BotMessageScope.GROUP;
            case CHANNEL -> BotMessageScope.CHANNEL;
            case C2C, DIRECT -> BotMessageScope.PRIVATE;
        };
        String timestamp = text(event.timestamp());
        return Optional.of(BotMessageContext
            .of(BotPlatform.QQ_OFFICIAL, scope, appId, senderId, targetId, messageId, rawJson, content,
                new QqOfficialMessageSource(eventType, chatType, targetId, messageId, appId, rawJson,
                    timestamp == null ? null : OffsetDateTime.parse(timestamp).toInstant()),
                attachments(event))
            .withReference(reference(event), index(event, "msg_idx")));
    }

    static BotMessageReference reference(QqOfficialGatewayEventDTO event) {
        String id = index(event, "ref_msg_idx");
        ReferencedMessageDTO element =
            event.msgElements() == null || event.msgElements().isEmpty() ? null : event.msgElements().getFirst();
        if (id == null && Integer.valueOf(REFERENCE_MESSAGE_TYPE).equals(event.messageType()) && element != null)
            id = text(element.msgIdx());
        if (id != null) {
            List<BotIncomingAttachment> referenced =
                element != null && (text(element.msgIdx()) == null || id.equals(text(element.msgIdx())))
                    ? attachments(element.attachments()) : List.of();
            return new BotMessageReference(ReferenceKind.REFERENCE_INDEX, id, referenced);
        }
        String messageId = event.messageReference() == null ? null : text(event.messageReference().messageId());
        return messageId == null ? null : new BotMessageReference(ReferenceKind.MESSAGE_ID, messageId, List.of());
    }

    private static String index(QqOfficialGatewayEventDTO event, String key) {
        List<String> ext = event.messageScene() == null ? null : event.messageScene().ext();
        if (ext == null)
            return null;
        for (String entry : ext) {
            if (entry == null)
                continue;
            int separator = entry.indexOf('=');
            if (separator > 0 && key.equals(entry.substring(0, separator).trim())) {
                String id = entry.substring(separator + 1).trim();
                if (!id.isEmpty())
                    return id;
            }
        }
        return null;
    }

    static QqOfficialChatType chatType(String eventType) {
        return switch (eventType) {
            case "C2C_MESSAGE_CREATE" -> QqOfficialChatType.C2C;
            case "GROUP_AT_MESSAGE_CREATE", "GROUP_MESSAGE_CREATE" -> QqOfficialChatType.GROUP;
            case "AT_MESSAGE_CREATE" -> QqOfficialChatType.CHANNEL;
            case "DIRECT_MESSAGE_CREATE" -> QqOfficialChatType.DIRECT;
            default -> null;
        };
    }

    private static String text(String value) {
        return StrUtil.isBlank(value) ? null : value;
    }

    private static String firstText(String... values) {
        for (String value : values) {
            if (text(value) != null)
                return value;
        }
        return null;
    }

    private static String normalizeContent(String content, String appId) {
        if (StrUtil.isBlank(content) || StrUtil.isBlank(appId))
            return content;
        return content.replace("<@!" + appId + ">", "").replace("<@" + appId + ">", "");
    }

    public static List<BotIncomingAttachment> attachments(QqOfficialGatewayEventDTO event) {
        return event == null ? List.of() : attachments(event.attachments());
    }

    private static List<BotIncomingAttachment> attachments(List<AttachmentDTO> nodes) {
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
                // 上游附件大小并非可靠数字；其它字段仍应正常处理。
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
}
