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
            return;
        }
        if (plan.outputMode() == ChannelOutputMode.IMAGE
                && plan.hasImage()) {
            this.sendImageWithFallback(context, plan);
            return;
        }
        if (plan.outputMode() == ChannelOutputMode.TEXT_IMAGE
                && plan.hasImage()) {
            if (plan.hasText()) {
                this.sendOrFallback(
                        context,
                        plan,
                        "当前渠道不支持图文消息",
                        () -> sendImageWithText(context, plan));
            } else {
                this.sendImageWithFallback(context, plan);
            }
            return;
        }
        if (plan.outputMode() == ChannelOutputMode.FILE
                || plan.outputMode() == ChannelOutputMode.TEXT_FILE) {
            boolean sent = this.sendOrFallback(
                    context,
                    plan,
                    "当前渠道不支持文件消息",
                    () -> {
                        for (int index = 0; index < plan.attachments().size(); index++) {
                            sendFile(context, plan, plan.attachments().get(index),
                                    index == 0 ? plan.mentionUserId() : null);
                        }
                    });
            if (!sent) {
                return;
            }
            if (plan.hasText()) {
                sendText(context, plan);
            }
            return;
        }
        if (plan.hasText()) {
            sendText(context, plan);
        }
    }

    private void sendImageWithFallback(BotMessageContext context, ResponsePlan plan) {
        this.sendOrFallback(
                context,
                plan,
                "当前渠道不支持图片消息",
                () -> sendImage(context, plan));
    }

    /**
     * 只吞掉明确的能力不足异常；网络、协议和业务失败继续交给命令异常边界。
     */
    private boolean sendOrFallback(
            BotMessageContext context,
            ResponsePlan plan,
            String defaultText,
            Runnable sendAction) {
        try {
            sendAction.run();
            return true;
        } catch (BotChannelCapabilityException exception) {
            this.sendTextFallback(context, plan, defaultText);
            return false;
        }
    }

    private void sendTextFallback(BotMessageContext context, ResponsePlan plan,
                                  String defaultText) {
        if (StrUtil.isBlank(plan.mentionUserId())) {
            messageSenderRouter.sendText(context, plan.hasText() ? plan.text() : defaultText);
        } else {
            messageSenderRouter.sendText(
                    context, plan.hasText() ? plan.text() : defaultText, plan.mentionUserId());
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

    private void sendImageWithText(BotMessageContext context, ResponsePlan plan) {
        if (StrUtil.isBlank(plan.mentionUserId())) {
            messageSenderRouter.sendImageWithText(context, plan.text(), plan.image());
        } else {
            messageSenderRouter.sendImageWithText(
                    context, plan.text(), plan.image(), plan.mentionUserId());
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
