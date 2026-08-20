package com.nnm.bot.core.message;

import com.nnm.bot.core.channel.BotChannelKey;

import java.util.Set;

public interface PlatformMessageSender {

    BotPlatform platform();

    /** 当前发送器负责的具体机器人账号。 */
    Set<BotChannelKey> channels();

    void sendText(BotMessageContext context, String text);

    void sendImage(BotMessageContext context, byte[] image);
}
