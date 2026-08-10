package com.nnm.bot.core.qqofficial.message;

import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.message.BotPlatform;
import com.nnm.bot.core.message.PlatformMessageSender;
import com.nnm.bot.core.qqofficial.api.QqOfficialApiClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class QqOfficialMessageSender implements PlatformMessageSender {

    private final QqOfficialApiClient apiClient;

    public QqOfficialMessageSender(QqOfficialApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public void sendText(BotMessageContext context, String text) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(text, "text must not be blank");
        apiClient.sendText(context.nativeSource(QqOfficialMessageSource.class), text);
    }

    @Override
    public void sendImage(BotMessageContext context, byte[] image) {
        Assert.notNull(context, "context must not be null");
        Assert.isTrue(image != null && image.length > 0, "image must not be empty");
        apiClient.sendImage(context.nativeSource(QqOfficialMessageSource.class), image);
    }
}
