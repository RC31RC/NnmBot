package com.nnm.bot.core.message.sender;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotProactiveGroupMessage;
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
        BotProactiveMessageSender sender = senders.get(platform);
        if (sender == null) {
            throw new BusinessException(
                    BotChannelErrorCode.CHANNEL_NOT_REGISTERED,
                    "未注册主动消息发送器: " + platform);
        }
        sender.sendGroupText(message);
    }
}
