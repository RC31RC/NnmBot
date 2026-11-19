package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.messaging.model.BotMessageContext;
import com.nnm.bot.core.messaging.model.PreparedBotMessage;
import com.nnm.bot.core.messaging.model.BotMessageSendPermit;
import com.nnm.bot.core.messaging.sender.BotMessageSenderRouter;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/** 一个已编译计划准备一条平台消息；这里不降级、不拆分、不重试。 */
@Component
public class ReplyDeliveryExecutor {
    @Resource
    private BotMessageSenderRouter senders;

    public PreparedBotMessage prepare(BotMessageContext context, ResponsePlan plan, BotMessageSendPermit permit) {
        return senders.prepare(context, plan.parts(), plan.mentionUserId(), permit);
    }
}
