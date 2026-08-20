package com.nnm.bot.core.message;

import jakarta.annotation.PostConstruct;
import com.nnm.bot.core.channel.BotChannelKey;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotMessageSenderRouter implements BotMessageSender, MarkdownMessageSender {

    private final List<PlatformMessageSender> platformSenders;

    private Map<BotChannelKey, PlatformMessageSender> senders;

    public BotMessageSenderRouter(List<PlatformMessageSender> platformSenders) {
        this.platformSenders = List.copyOf(platformSenders);
    }

    @PostConstruct
    void initialize() {
        Map<BotChannelKey, PlatformMessageSender> registry = new HashMap<>();
        for (PlatformMessageSender sender : platformSenders) {
            for (BotChannelKey channel : sender.channels()) {
                if (channel.platform() != sender.platform()) {
                    throw new IllegalStateException(
                            "Sender platform does not match channel " + channel);
                }
                PlatformMessageSender previous = registry.put(channel, sender);
                if (previous != null) {
                    throw new IllegalStateException(
                            "Duplicate message sender for " + channel);
                }
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

    @Override
    public void sendMarkdown(BotMessageContext context, BotMarkdownMessage message) {
        PlatformMessageSender platformSender = sender(context);
        if (!(platformSender instanceof MarkdownMessageSender markdownSender)) {
            throw new UnsupportedOperationException(
                    "Markdown messages are not supported by " + context.platform());
        }
        markdownSender.sendMarkdown(context, message);
    }

    private PlatformMessageSender sender(BotMessageContext context) {
        Assert.notNull(context, "context must not be null");
        BotChannelKey key = new BotChannelKey(
                context.platform(), context.botAccountId());
        PlatformMessageSender sender = senders.get(key);
        if (sender == null) {
            throw new IllegalStateException("No message sender registered for " + key);
        }
        return sender;
    }
}
