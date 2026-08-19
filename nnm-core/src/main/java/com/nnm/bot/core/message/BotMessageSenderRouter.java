package com.nnm.bot.core.message;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class BotMessageSenderRouter implements BotMessageSender, MarkdownMessageSender {

    @Resource
    private List<PlatformMessageSender> platformSenders;

    private Map<BotPlatform, PlatformMessageSender> senders;

    @PostConstruct
    void initialize() {
        EnumMap<BotPlatform, PlatformMessageSender> registry = new EnumMap<>(BotPlatform.class);
        for (PlatformMessageSender sender : platformSenders) {
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
        PlatformMessageSender sender = senders.get(context.platform());
        if (sender == null) {
            throw new IllegalStateException("No message sender registered for " + context.platform());
        }
        return sender;
    }
}
