package com.nnm.bot.core.qqofficial.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.message.BotMessageScope;
import com.nnm.bot.core.message.BotPlatform;

import java.util.Optional;

public final class QqOfficialMessageContextFactory {

    private QqOfficialMessageContextFactory() {
    }

    public static Optional<BotMessageContext> from(String appId,
                                                   String eventType,
                                                   JsonNode event) {
        QqOfficialChatType chatType = chatType(eventType);
        if (chatType == null || event == null) {
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
        if (isBlank(appId) || isBlank(senderId) || isBlank(targetId)) {
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
                        eventType, chatType, targetId, messageId, appId, event.toString())
        ));
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

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
