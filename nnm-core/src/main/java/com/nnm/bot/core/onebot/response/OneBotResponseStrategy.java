package com.nnm.bot.core.onebot.response;

import cn.hutool.core.util.StrUtil;

import com.nnm.bot.core.constant.ChannelOutputMode;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.sender.BotMessageSenderRouter;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.plugin.api.BotChannelResponseStrategy;
import com.nnm.bot.core.message.exception.BotChannelCapabilityException;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * OneBot 输出策略。OneBot 不支持 Markdown 时统一降级为文字。
 */
@Component
public class OneBotResponseStrategy implements BotChannelResponseStrategy {

    @Resource
    private BotMessageSenderRouter messageSenderRouter;

    @Override
    public BotPlatform platform() {
        return BotPlatform.ONEBOT;
    }

    @Override
    public void send(BotMessageContext context, ResponsePlan plan) {
        if (plan.outputMode() == ChannelOutputMode.NONE) {
            if (plan.hasText()) {
                sendText(context, plan);
            }
            return;
        }
        if (plan.outputMode() == ChannelOutputMode.IMAGE
                && plan.hasImage()) {
            sendText(context, plan);
            this.sendImageWithFallback(context, plan);
            return;
        }
        if (plan.outputMode() == ChannelOutputMode.FILE) {
            sendText(context, plan);
            try {
                for (int index = 0; index < plan.attachments().size(); index++) {
                    sendFile(context, plan, plan.attachments().get(index),
                            index == 0 ? plan.mentionUserId() : null);
                }
            } catch (BotChannelCapabilityException exception) {
                // 摘要已经发送过，能力降级时只记录并结束，不能再次发送摘要。
                return;
            }
            return;
        }
        if (plan.outputMode() == ChannelOutputMode.MARKDOWN) {
            if (plan.hasText()) sendText(context, plan);
            if (plan.markdown() != null) {
                messageSenderRouter.sendText(context, plan.markdown().content());
            }
            return;
        }
        if (plan.hasText()) {
            sendText(context, plan);
        }
    }

    private void sendImageWithFallback(BotMessageContext context, ResponsePlan plan) {
        try {
            sendImage(context, plan);
        } catch (BotChannelCapabilityException exception) {
            // 摘要已经发送过，避免能力异常导致同一摘要重复发送。
        }
    }

    private void sendText(BotMessageContext context, ResponsePlan plan) {
        if (StrUtil.isBlank(plan.mentionUserId())) {
            messageSenderRouter.sendText(context, plan.text());
        } else {
            messageSenderRouter.sendText(context, plan.text(), plan.mentionUserId());
        }
    }

    private void sendImage(BotMessageContext context, ResponsePlan plan) {
        if (StrUtil.isBlank(plan.mentionUserId())) {
            messageSenderRouter.sendImage(context, plan.image());
        } else {
            messageSenderRouter.sendImage(context, plan.image(), plan.mentionUserId());
        }
    }

    private void sendFile(BotMessageContext context,
                          ResponsePlan plan,
                          CommandAttachment attachment,
                          String mentionUserId) {
        if (StrUtil.isBlank(mentionUserId)) {
            messageSenderRouter.sendFile(context, attachment.toBotFile());
        } else {
            messageSenderRouter.sendFile(
                    context, attachment.toBotFile(), mentionUserId);
        }
    }
}
