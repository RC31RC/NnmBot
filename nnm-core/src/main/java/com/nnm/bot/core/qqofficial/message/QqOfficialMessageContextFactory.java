package com.nnm.bot.core.qqofficial.message;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.nnm.bot.core.constant.QqOfficialChatType;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.constant.BotMessageConstants.BotIncomingAttachmentType;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.OffsetDateTime;
import java.time.Instant;

/**
 * 将 QQ 官方 Gateway 消息事件转换为平台无关上下文。
 *
 * <p>非消息事件或缺少发送者、会话标识的事件返回空结果，由 Gateway 继续维护连接，
 * 不进入命令链。</p>
 */
public final class QqOfficialMessageContextFactory {

    private QqOfficialMessageContextFactory() {
    }

    /**
     * 解析支持的消息事件，并移除机器人自身的 at 标记。
     */
    public static Optional<BotMessageContext> from(String appId,
                                                   String eventType,
                                                   JsonNode event) {
        QqOfficialChatType chatType = chatType(eventType);
        if (ObjectUtil.isNull(chatType) || ObjectUtil.isNull(event)) {
            return Optional.empty();
        }

        String senderId = switch (chatType) {
            case C2C -> text(event.path("author"), "user_openid");
            case GROUP -> firstText(event.path("author"), "member_openid", "user_openid");
            case CHANNEL, DIRECT -> text(event.path("author"), "id");
        };
        String targetId = switch (chatType) {
            case C2C -> senderId;
            case GROUP -> text(event, "group_openid");
            case CHANNEL -> text(event, "channel_id");
            case DIRECT -> text(event, "guild_id");
        };
        String messageId = text(event, "id");
        String content = normalizeContent(event.path("content").asText(""), appId);
        if (isBlank(appId) || isBlank(senderId) || isBlank(targetId) || isBlank(messageId)) {
            return Optional.empty();
        }

        BotMessageScope scope = switch (chatType) {
            case GROUP -> BotMessageScope.GROUP;
            case CHANNEL -> BotMessageScope.CHANNEL;
            case C2C, DIRECT -> BotMessageScope.PRIVATE;
        };
        return Optional.of(BotMessageContext.of(
                BotPlatform.QQ_OFFICIAL,
                scope,
                appId,
                senderId,
                targetId,
                messageId,
                event.toString(),
                content,
                new QqOfficialMessageSource(
                        eventType, chatType, targetId, messageId, appId, event.toString(), timestamp(event)),
                attachments(event)
        ));
    }

    private static Instant timestamp(JsonNode event) {
        String timestamp = text(event, "timestamp");
        return timestamp == null ? null : OffsetDateTime.parse(timestamp).toInstant();
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

    private static String firstText(JsonNode node, String... fields) {
        for (String field : fields) {
            String value = text(node, field);
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private static String text(JsonNode node, String field) {
        String value = node.path(field).asText("");
        return isBlank(value) ? null : value;
    }

    private static String normalizeContent(String content, String appId) {
        if (isBlank(content) || isBlank(appId)) {
            return content;
        }
        return content
                .replace("<@!" + appId + ">", "")
                .replace("<@" + appId + ">", "");
    }

    private static List<BotIncomingAttachment> attachments(JsonNode event) {
        JsonNode attachmentNodes = event.path("attachments");
        if (!attachmentNodes.isArray()) {
            return List.of();
        }
        List<BotIncomingAttachment> attachments = new ArrayList<>();
        for (JsonNode node : attachmentNodes) {
            String contentType = firstText(node, "content_type", "mime_type", "mime");
            String fileName = firstText(node, "filename", "file_name", "name");
            String url = firstText(node, "url", "download_url");
            String fileId = firstText(node, "id", "file_id");
            String type = firstText(node, "type", "content_type");
            BotIncomingAttachmentType attachmentType =
                    isImage(type, contentType, fileName)
                            ? BotIncomingAttachmentType.IMAGE
                            : BotIncomingAttachmentType.FILE;
            attachments.add(new BotIncomingAttachment(
                    attachmentType,
                    fileName,
                    contentType,
                    url,
                    fileId,
                    positiveLong(node, "size", "file_size"),
                    positiveInt(node, "width"),
                    positiveInt(node, "height"),
                    Map.of("platformType", type == null ? "unknown" : type)));
        }
        return List.copyOf(attachments);
    }

    private static boolean isImage(String type, String contentType, String fileName) {
        return (type != null && type.toLowerCase().contains("image"))
                || (contentType != null && contentType.toLowerCase().startsWith("image/"))
                || (fileName != null && contentTypeFromName(fileName) != null);
    }

    private static Long positiveLong(JsonNode node, String... fields) {
        for (String field : fields) {
            long value = node.path(field).asLong(0);
            if (value > 0) {
                return value;
            }
        }
        return null;
    }

    private static Integer positiveInt(JsonNode node, String field) {
        long value = node.path(field).asLong(0);
        return value > 0 && value <= Integer.MAX_VALUE ? (int) value : null;
    }

    private static String contentTypeFromName(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif")) return "image/gif";
        return null;
    }

    private static boolean isBlank(String value) {
        return StrUtil.isBlank(value);
    }
}
