package com.nnm.bot.core.qqofficial.message;

import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.message.BotMarkdownMessage;
import com.nnm.bot.core.message.BotPlatform;
import com.nnm.bot.core.channel.BotChannelKey;
import com.nnm.bot.core.message.MarkdownMessageSender;
import com.nnm.bot.core.message.PlatformMessageSender;
import com.nnm.bot.core.qqofficial.config.QqOfficialProperties;
import com.nnm.bot.core.qqofficial.gateway.QqOfficialGatewayClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QqOfficialMessageSender implements PlatformMessageSender, MarkdownMessageSender {

    private final QqOfficialGatewayClient gatewayClient;
    private final Set<BotChannelKey> channels;

    public QqOfficialMessageSender(QqOfficialGatewayClient gatewayClient,
                                   QqOfficialProperties properties) {
        this.gatewayClient = gatewayClient;
        this.channels = properties.isEnabled()
                ? properties.getInstances().stream()
                .filter(QqOfficialProperties.Instance::isEnabled)
                .map(instance -> new BotChannelKey(
                        BotPlatform.QQ_OFFICIAL, instance.botAccountId()))
                .collect(Collectors.toUnmodifiableSet())
                : Set.of();
    }

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public Set<BotChannelKey> channels() {
        return channels;
    }

    @Override
    public void sendText(BotMessageContext context, String text) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(text, "text must not be blank");
        gatewayClient.apiClient(context.botAccountId())
                .sendText(context.nativeSource(QqOfficialMessageSource.class), text);
    }

    @Override
    public void sendImage(BotMessageContext context, byte[] image) {
        Assert.notNull(context, "context must not be null");
        Assert.isTrue(image != null && image.length > 0, "image must not be empty");
        gatewayClient.apiClient(context.botAccountId())
                .sendImage(context.nativeSource(QqOfficialMessageSource.class), image);
    }

    @Override
    public void sendMarkdown(BotMessageContext context, BotMarkdownMessage message) {
        Assert.notNull(context, "context must not be null");
        Assert.notNull(message, "message must not be null");
        gatewayClient.apiClient(context.botAccountId())
                .sendMarkdown(context.nativeSource(QqOfficialMessageSource.class), message);
    }
}
