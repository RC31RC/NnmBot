package com.nnm.bot.core.message;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.MsgId;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class OneBotMessageSender implements BotMessageSender {

    @Override
    public void sendText(BotMessageContext context, String text) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(text, "text must not be blank");
        ensureSuccess(context.bot().sendMsg(context.sourceEvent(), text, true));
    }

    @Override
    public void sendImage(BotMessageContext context, byte[] image) {
        Assert.notNull(context, "context must not be null");
        Assert.isTrue(image != null && image.length > 0, "image must not be empty");
        String message = MsgUtils.builder().img(image).build();
        ensureSuccess(context.bot().sendMsg(context.sourceEvent(), message, false));
    }

    private static void ensureSuccess(ActionData<MsgId> result) {
        if (result == null || !"ok".equalsIgnoreCase(result.getStatus())
                || (result.getRetCode() != null && result.getRetCode() != 0)) {
            throw new IllegalStateException("OneBot message send failed: " + result);
        }
    }
}
