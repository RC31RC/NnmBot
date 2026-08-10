package com.nnm.bot.core.message;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.MsgId;
import com.nnm.bot.core.onebot.OneBotMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class OneBotMessageSender implements PlatformMessageSender {

    @Override
    public BotPlatform platform() {
        return BotPlatform.ONEBOT;
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
