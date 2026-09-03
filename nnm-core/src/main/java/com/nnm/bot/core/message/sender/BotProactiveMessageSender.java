package com.nnm.bot.core.message.sender;

import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotProactiveGroupMessage;

/** 渠道主动消息发送端口，不依赖伪造的入站消息上下文。 */
public interface BotProactiveMessageSender {

    BotPlatform platform();

    void sendGroupText(BotProactiveGroupMessage message);
}
