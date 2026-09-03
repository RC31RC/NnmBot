package com.nnm.bot.core.qqofficial.response;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.sender.BotMessageSenderRouter;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.plugin.api.BotChannelResponseStrategy;
import com.nnm.bot.core.constant.ChannelOutputMode;
import com.nnm.bot.core.message.exception.BotChannelCapabilityException;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * QQ 官方输出策略，额外支持 Markdown。
 *
 * <p>当前 QQ 官方机器人协议没有接入触发者提及能力，因此故意使用不带
 * {@code mentionUserId} 的发送方法，忽略命令级提及配置。</p>
 */
@Component
public class QqOfficialResponseStrategy implements BotChannelResponseStrategy {

    @Resource
    private BotMessageSenderRouter messageSenderRouter;

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public void send(BotMessageContext context, ResponsePlan plan) {
        if (plan.outputMode() == ChannelOutputMode.NONE) {
            return;
        }
        if (plan.outputMode() == ChannelOutputMode.MARKDOWN && plan.hasText()) {
            this.sendOrFallback(
                    context,
                    plan,
                    "当前渠道不支持 Markdown 消息",
                    () -> messageSenderRouter.sendMarkdown(context, plan.markdown()));
            return;
        }
        if (plan.outputMode() == ChannelOutputMode.IMAGE && plan.hasImage()) {
            sendImage(context, plan);
            return;
        }
        if (plan.outputMode() == ChannelOutputMode.TEXT_IMAGE && plan.hasImage()) {
            if (!plan.hasText()) {
                sendImage(context, plan);
                return;
            }
            this.sendOrFallback(
                    context,
                    plan,
                    "当前渠道不支持图文消息",
                    () -> messageSenderRouter.sendImageWithText(
                            context, plan.text(), plan.image()));
            return;
        }
        if (plan.outputMode() == ChannelOutputMode.FILE
                || plan.outputMode() == ChannelOutputMode.TEXT_FILE) {
            if (plan.attachments().size() == 1 && plan.hasText()) {
                if (!this.trySend(() -> messageSenderRouter.sendFileWithText(
                        context,
                        plan.text(),
                        plan.attachments().getFirst().toBotFile()))) {
                    this.sendFileFallback(context, plan);
                }
            } else {
                this.sendFileFallback(context, plan);
            }
            return;
        }
        if (plan.hasText()) {
            messageSenderRouter.sendText(context, plan.text());
        }
    }

    private void sendImage(BotMessageContext context, ResponsePlan plan) {
        this.sendOrFallback(
                context,
                plan,
                "当前渠道不支持图片消息",
                () -> messageSenderRouter.sendImage(context, plan.image()));
    }

    private void sendFileFallback(BotMessageContext context, ResponsePlan plan) {
        boolean sent = this.sendOrFallback(
                context,
                plan,
                "当前渠道不支持文件消息",
                () -> plan.attachments().forEach(attachment ->
                        messageSenderRouter.sendFile(context, attachment.toBotFile())));
        if (!sent) {
            return;
        }
        if (plan.hasText()) {
            messageSenderRouter.sendText(context, plan.text());
        }
    }

    /**
     * QQ 会话类型会影响媒体能力，能力不足时使用命令文字作为可控降级。
     */
    private boolean sendOrFallback(
            BotMessageContext context,
            ResponsePlan plan,
            String defaultText,
            Runnable sendAction) {
        if (this.trySend(sendAction)) {
            return true;
        }
        this.sendTextFallback(context, plan, defaultText);
        return false;
    }

    private boolean trySend(Runnable sendAction) {
        try {
            sendAction.run();
            return true;
        } catch (BotChannelCapabilityException exception) {
            return false;
        }
    }

    private void sendTextFallback(BotMessageContext context, ResponsePlan plan,
                                  String defaultText) {
        messageSenderRouter.sendText(context, plan.hasText() ? plan.text() : defaultText);
    }
}
