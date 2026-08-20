package com.nnm.bot.core.message;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.MsgId;
import com.nnm.bot.core.channel.BotChannelKey;
import com.nnm.bot.core.onebot.OneBotMessageSource;
import com.nnm.bot.core.onebot.config.OneBotProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OneBotMessageSender implements PlatformMessageSender {

    private final Set<BotChannelKey> channels;

    public OneBotMessageSender(OneBotProperties properties) {
        this.channels = properties.isEnabled()
                ? properties.getInstances().stream()
                .filter(OneBotProperties.Instance::isEnabled)
                .map(instance -> new BotChannelKey(
                        BotPlatform.ONEBOT, instance.getBotAccountId()))
                .collect(Collectors.toUnmodifiableSet())
                : Set.of();
    }

    @Override
    public BotPlatform platform() {
        return BotPlatform.ONEBOT;
    }

    @Override
    public Set<BotChannelKey> channels() {
        return channels;
    }

    @Override
    public void sendText(BotMessageContext context, String text) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(text, "text must not be blank");
        OneBotMessageSource source = context.nativeSource(OneBotMessageSource.class);
        ensureSuccess(source.bot().sendMsg(source.event(), text, true));
    }

    @Override
    public void sendImage(BotMessageContext context, byte[] image) {
        Assert.notNull(context, "context must not be null");
        Assert.isTrue(image != null && image.length > 0, "image must not be empty");
        String message = MsgUtils.builder().img(image).build();
        OneBotMessageSource source = context.nativeSource(OneBotMessageSource.class);
        ensureSuccess(source.bot().sendMsg(source.event(), message, false));
    }

    private static void ensureSuccess(ActionData<MsgId> result) {
        if (result == null || !"ok".equalsIgnoreCase(result.getStatus())
                || (result.getRetCode() != null && result.getRetCode() != 0)) {
            throw new IllegalStateException("OneBot message send failed: " + result);
        }
    }
}
