package com.nnm.bot.core.onebot;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.message.BotMessageScope;
import com.nnm.bot.core.message.BotPlatform;
import org.springframework.util.StringUtils;

import java.util.Objects;

public final class OneBotMessageContextFactory {

    private OneBotMessageContextFactory() {
    }

    public static BotMessageContext from(Bot bot, AnyMessageEvent event) {
        Objects.requireNonNull(bot, "bot must not be null");
        Objects.requireNonNull(event, "event must not be null");

        BotMessageScope scope = switch (event.getMessageType()) {
            case "group" -> BotMessageScope.GROUP;
            case "guild" -> BotMessageScope.CHANNEL;
            default -> BotMessageScope.PRIVATE;
        };
        String accountId = String.valueOf(bot.getSelfId());
        String senderId = String.valueOf(Objects.requireNonNullElse(event.getUserId(), 0L));
        String conversationId = scope == BotMessageScope.GROUP
                ? String.valueOf(Objects.requireNonNullElse(event.getGroupId(), 0L))
                : senderId;
        String rawMessage = Objects.requireNonNullElse(event.getRawMessage(), "");
        String message = StringUtils.hasText(event.getPlainText()) ? event.getPlainText() : rawMessage;

        return BotMessageContext.of(
                BotPlatform.ONEBOT,
                scope,
                accountId,
                senderId,
                conversationId,
                event.getMessageId() == null ? null : String.valueOf(event.getMessageId()),
                rawMessage,
                message,
                new OneBotMessageSource(bot, event)
        );
    }
}
