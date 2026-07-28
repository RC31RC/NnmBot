package com.nnm.bot.core.message;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import org.springframework.util.StringUtils;

import java.util.Objects;

public final class BotMessageContext {

    public enum Scope {
        GROUP,
        PRIVATE
    }

    private final Bot bot;
    private final AnyMessageEvent sourceEvent;
    private final Scope scope;
    private final long botId;
    private final long userId;
    private final Long groupId;
    private final Integer messageId;
    private final String rawMessage;
    private final String message;

    private BotMessageContext(Bot bot,
                              AnyMessageEvent sourceEvent,
                              Scope scope,
                              long botId,
                              long userId,
                              Long groupId,
                              Integer messageId,
                              String rawMessage,
                              String message) {
        this.bot = bot;
        this.sourceEvent = sourceEvent;
        this.scope = scope;
        this.botId = botId;
        this.userId = userId;
        this.groupId = groupId;
        this.messageId = messageId;
        this.rawMessage = rawMessage;
        this.message = message;
    }

    public static BotMessageContext from(Bot bot, AnyMessageEvent event) {
        Objects.requireNonNull(bot, "bot must not be null");
        Objects.requireNonNull(event, "event must not be null");

        Scope scope = "group".equals(event.getMessageType()) ? Scope.GROUP : Scope.PRIVATE;
        String rawMessage = Objects.requireNonNullElse(event.getRawMessage(), "");
        String plainMessage = StringUtils.hasText(event.getPlainText())
                ? event.getPlainText()
                : rawMessage;

        return new BotMessageContext(
                bot,
                event,
                scope,
                bot.getSelfId(),
                Objects.requireNonNullElse(event.getUserId(), 0L),
                event.getGroupId(),
                event.getMessageId(),
                rawMessage,
                plainMessage.trim()
        );
    }

    Bot bot() {
        return bot;
    }

    AnyMessageEvent sourceEvent() {
        return sourceEvent;
    }

    public Scope scope() {
        return scope;
    }

    public long botId() {
        return botId;
    }

    public long userId() {
        return userId;
    }

    public Long groupId() {
        return groupId;
    }

    public Integer messageId() {
        return messageId;
    }

    public String rawMessage() {
        return rawMessage;
    }

    public String message() {
        return message;
    }

    public boolean isGroupMessage() {
        return scope == Scope.GROUP;
    }
}
