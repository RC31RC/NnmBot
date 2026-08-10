package com.nnm.bot.core.message;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class BotMessageSenderRouter implements BotMessageSender {

    private final Map<BotPlatform, PlatformMessageSender> senders;

    public BotMessageSenderRouter(List<PlatformMessageSender> senders) {
        EnumMap<BotPlatform, PlatformMessageSender> registry = new EnumMap<>(BotPlatform.class);
        for (PlatformMessageSender sender : senders) {
            PlatformMessageSender previous = registry.put(sender.platform(), sender);
            if (previous != null) {
                throw new IllegalStateException("Duplicate message sender for " + sender.platform());
            }
        }
        this.senders = Map.copyOf(registry);
    }

    @Override
    public void sendText(BotMessageContext context, String text) {
        sender(context).sendText(context, text);
    }

    @Override
    public void sendImage(BotMessageContext context, byte[] image) {
        sender(context).sendImage(context, image);
    }

    private PlatformMessageSender sender(BotMessageContext context) {
        Assert.notNull(context, "context must not be null");
        PlatformMessageSender sender = senders.get(context.platform());
        if (sender == null) {
            throw new IllegalStateException("No message sender registered for " + context.platform());
        }
        return sender;
    }
}
