package com.nnm.bot.core.message;

public interface PlatformMessageSender {

    BotPlatform platform();

    void sendText(BotMessageContext context, String text);

    void sendImage(BotMessageContext context, byte[] image);
}
