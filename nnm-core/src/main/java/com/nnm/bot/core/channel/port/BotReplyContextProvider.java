package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.message.model.BotMessageContext;
import java.util.Optional;

/** 只有支持主动发送的适配器，才能创建不带原生回执的回复上下文。 */
public interface BotReplyContextProvider {
    BotPlatform platform();

    Optional<BotMessageContext> create(BotChannelInstanceMetadata instance, BotGroupBot binding,
        BotMessageContext origin, String senderId);
}
