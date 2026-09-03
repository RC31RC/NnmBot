package com.nnm.bot.core.qqofficial.message;

import cn.hutool.core.util.ArrayUtil;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotFile;
import com.nnm.bot.core.message.model.BotMarkdownMessage;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.exception.BotChannelCapabilityException;
import com.nnm.bot.core.message.sender.BotMessageSender;
import com.nnm.bot.core.qqofficial.gateway.QqOfficialGatewayClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class QqOfficialMessageSender implements BotMessageSender {

    @Resource
    private QqOfficialGatewayClient gatewayClient;

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public boolean supports(BotMessageContext context, BotChannelCapability capability) {
        if (capability == BotChannelCapability.TEXT_MESSAGE
                || capability == BotChannelCapability.MARKDOWN_MESSAGE) {
            return true;
        }
        if (capability == BotChannelCapability.IMAGE_MESSAGE
                || capability == BotChannelCapability.FILE_MESSAGE) {
            return context.nativeSource(QqOfficialMessageSource.class).supportsMedia();
        }
        return false;
    }

    @Override
    public void sendText(BotMessageContext context, String text) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(text, "text must not be blank");
        // QQ 官方账号由运行期渠道实例管理，不依赖启动时的静态配置账号列表。
        this.gatewayClient.apiClient(context.botAccountId())
                .sendText(context.nativeSource(QqOfficialMessageSource.class), text);
    }

    @Override
    public void sendImage(BotMessageContext context, byte[] image) {
        Assert.notNull(context, "context must not be null");
        Assert.isTrue(ArrayUtil.isNotEmpty(image), "image must not be empty");
        this.gatewayClient.apiClient(context.botAccountId())
                .sendImage(context.nativeSource(QqOfficialMessageSource.class), image);
    }

    @Override
    public void sendImageWithText(BotMessageContext context, String text, byte[] image) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(text, "text must not be blank");
        Assert.isTrue(ArrayUtil.isNotEmpty(image), "image must not be empty");
        QqOfficialMessageSource source = context.nativeSource(QqOfficialMessageSource.class);
        if (!source.supportsMedia()) {
            throw new BotChannelCapabilityException(
                    this.platform(), BotChannelCapability.IMAGE_MESSAGE,
                    "会话类型 " + source.chatType());
        }
        this.gatewayClient.apiClient(context.botAccountId())
                .sendImageWithText(source, text, image);
    }

    @Override
    public void sendFile(BotMessageContext context, BotFile file) {
        Assert.notNull(context, "context must not be null");
        Assert.notNull(file, "file must not be null");
        QqOfficialMessageSource source = context.nativeSource(QqOfficialMessageSource.class);
        if (!source.supportsMedia()) {
            throw new BotChannelCapabilityException(
                    this.platform(), BotChannelCapability.FILE_MESSAGE,
                    "会话类型 " + source.chatType());
        }
        this.gatewayClient.apiClient(context.botAccountId()).sendFile(source, file);
    }

    @Override
    public void sendFileWithText(BotMessageContext context, String text, BotFile file) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(text, "text must not be blank");
        Assert.notNull(file, "file must not be null");
        QqOfficialMessageSource source = context.nativeSource(QqOfficialMessageSource.class);
        if (!source.supportsMedia()) {
            throw new BotChannelCapabilityException(
                    this.platform(), BotChannelCapability.FILE_MESSAGE,
                    "会话类型 " + source.chatType());
        }
        this.gatewayClient.apiClient(context.botAccountId()).sendFileWithText(source, text, file);
    }

    @Override
    public void sendMarkdown(BotMessageContext context, BotMarkdownMessage message) {
        Assert.notNull(context, "context must not be null");
        Assert.notNull(message, "message must not be null");
        this.gatewayClient.apiClient(context.botAccountId())
                .sendMarkdown(context.nativeSource(QqOfficialMessageSource.class), message);
    }
}
