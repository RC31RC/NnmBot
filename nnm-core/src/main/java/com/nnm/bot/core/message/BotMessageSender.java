package com.nnm.bot.core.message;

public interface BotMessageSender {

    void sendText(BotMessageContext context, String text);

    void sendImage(BotMessageContext context, byte[] image);
}
