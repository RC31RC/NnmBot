package com.nnm.bot.core.message.sender;

import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotProactiveGroupMessage;
import com.nnm.bot.core.message.model.BotProactiveTarget;
import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.plugin.response.ReplyTarget;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** 按平台路由主动消息发送器。 */
@Component
public class BotProactiveMessageSenderRouter {

    private final Map<BotPlatform, BotProactiveMessageSender> senders;

    public BotProactiveMessageSenderRouter(List<BotProactiveMessageSender> senders) {
        EnumMap<BotPlatform, BotProactiveMessageSender> indexed =
                new EnumMap<>(BotPlatform.class);
        for (BotProactiveMessageSender sender : senders) {
            BotProactiveMessageSender previous = indexed.put(sender.platform(), sender);
            if (previous != null) {
                throw new IllegalStateException(
                        "Duplicate proactive message sender: " + sender.platform());
            }
        }
        this.senders = Map.copyOf(indexed);
    }

    public void sendGroupText(BotPlatform platform, BotProactiveGroupMessage message) {
        BotProactiveTarget target = new BotProactiveTarget(platform, message.botAccountId(), message.conversationId(),
                message.mentionUserIds());
        try (PreparedBotMessage prepared = prepare(target, List.of(new BotMessagePart.Text(message.text())))) {
            prepared.send();
        }
    }

    public ReplyTarget target(BotProactiveTarget target) {
        BotProactiveMessageSender sender = sender(target.platform());
        return new ReplyTarget() {
            @Override
            public boolean supports(BotChannelCapability capability) {
                return sender.supports(target, capability);
            }

            @Override
            public boolean supportsComposition(List<PartKind> kinds) {
                return sender.supportsComposition(target, kinds);
            }

            @Override
            public boolean canReserve(int messages) { return messages > 0; }

            @Override
            public String mentionUserId() { return null; }
        };
    }

    public PreparedBotMessage prepare(BotProactiveTarget target, List<BotMessagePart> parts) {
        return sender(target.platform()).prepare(target, parts);
    }

    private BotProactiveMessageSender sender(BotPlatform platform) {
        BotProactiveMessageSender sender = senders.get(platform);
        if (sender == null) {
            throw new BusinessException(BotChannelErrorCode.CHANNEL_NOT_REGISTERED,
                    "未注册主动消息发送器: " + platform);
        }
        return sender;
    }
}
