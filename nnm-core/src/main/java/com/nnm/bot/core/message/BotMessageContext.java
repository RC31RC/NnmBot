package com.nnm.bot.core.message;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Platform-neutral message context used by plugins.
 *
 * @param nativeSource adapter-owned source event, never interpreted by plugins
 */
public final class BotMessageContext {

    private final BotPlatform platform;
    private final BotMessageScope scope;
    private final String botAccountId;
    private final String senderId;
    private final String conversationId;
    private final String messageId;
    private final String rawMessage;
    private final String message;
    private final Object nativeSource;

    private BotMessageContext(BotPlatform platform,
                              BotMessageScope scope,
                              String botAccountId,
                              String senderId,
                              String conversationId,
                              String messageId,
                              String rawMessage,
                              String message,
                              Object nativeSource) {
        this.platform = platform;
        this.scope = scope;
        this.botAccountId = botAccountId;
        this.senderId = senderId;
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.rawMessage = rawMessage;
        this.message = message;
        this.nativeSource = nativeSource;
    }

    public static BotMessageContext of(BotPlatform platform,
                                       BotMessageScope scope,
                                       String botAccountId,
                                       String senderId,
                                       String conversationId,
                                       String messageId,
                                       String rawMessage,
                                       String message,
                                       Object nativeSource) {
        Assert.notNull(platform, "platform must not be null");
        Assert.notNull(scope, "scope must not be null");
        Assert.hasText(botAccountId, "botAccountId must not be blank");
        Assert.hasText(senderId, "senderId must not be blank");
        Assert.hasText(conversationId, "conversationId must not be blank");
        return new BotMessageContext(
                platform,
                scope,
                botAccountId,
                senderId,
                conversationId,
                messageId,
                Objects.requireNonNullElse(rawMessage, ""),
                normalizeMessage(message, rawMessage),
                nativeSource
        );
    }

    private static String normalizeMessage(String message, String rawMessage) {
        String value = StringUtils.hasText(message)
                ? message
                : Objects.requireNonNullElse(rawMessage, "");
        return value.trim();
    }

    public BotPlatform platform() {
        return platform;
    }

    public BotMessageScope scope() {
        return scope;
    }

    public String botAccountId() {
        return botAccountId;
    }

    public String senderId() {
        return senderId;
    }

    public String conversationId() {
        return conversationId;
    }

    public String messageId() {
        return messageId;
    }

    public String rawMessage() {
        return rawMessage;
    }

    public String message() {
        return message;
    }

    public boolean isGroupMessage() {
        return scope == BotMessageScope.GROUP;
    }

    public boolean isPrivateMessage() {
        return scope == BotMessageScope.PRIVATE;
    }

    public boolean isChannelMessage() {
        return scope == BotMessageScope.CHANNEL;
    }

    public <T> T nativeSource(Class<T> type) {
        Assert.notNull(type, "type must not be null");
        if (!type.isInstance(nativeSource)) {
            throw new IllegalStateException(
                    "Message source is not " + type.getSimpleName() + ": " + nativeSource);
        }
        return type.cast(nativeSource);
    }
}
