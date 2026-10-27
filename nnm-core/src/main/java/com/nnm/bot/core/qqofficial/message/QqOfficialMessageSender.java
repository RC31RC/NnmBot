package com.nnm.bot.core.qqofficial.message;

import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.sender.BotMessageSender;
import com.nnm.bot.core.qqofficial.gateway.QqOfficialGatewayClient;
import jakarta.annotation.Resource;
import com.nnm.bot.core.qqofficial.config.QqReplyProperties;
import org.springframework.stereotype.Component;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.model.BotMessagePart;
import java.util.List;

@Component
public class QqOfficialMessageSender implements BotMessageSender {

    @Resource
    private QqOfficialGatewayClient gatewayClient;
    @Resource
    private QqReplyProperties replyProperties;
    @Resource
    private QqPassiveReplyBudget replyBudget;

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public boolean canReserve(BotMessageContext context, int count) {
        return replyBudget.canReserve(context, count);
    }

    @Override
    public List<BotMessageSendPermit> reserve(BotMessageContext context, int count) {
        return replyBudget.reserve(context, count);
    }

    @Override
    public boolean supports(BotMessageContext context, BotChannelCapability capability) {
        if (capability == BotChannelCapability.NATIVE_KEYBOARD) return replyProperties.keyboardAllowed(context.botAccountId(), context.scope());
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
    public boolean supportsComposition(BotMessageContext context, List<PartKind> parts) {
        if (parts.contains(PartKind.KEYBOARD) || parts.contains(PartKind.MARKDOWN)) {
            return parts.stream().allMatch(kind -> kind == PartKind.TEXT
                    || kind == PartKind.MARKDOWN || kind == PartKind.KEYBOARD)
                    && (!parts.contains(PartKind.KEYBOARD)
                        || replyProperties.keyboardAllowed(context.botAccountId(), context.scope()));
        }
        return context.nativeSource(QqOfficialMessageSource.class).supportsMedia()
                && (parts.equals(List.of(PartKind.IMAGE))
                    || parts.equals(List.of(PartKind.TEXT, PartKind.IMAGE))
                    || parts.equals(List.of(PartKind.TEXT, PartKind.FILE)));
    }

    @Override
    public PreparedBotMessage prepare(BotMessageContext context, List<BotMessagePart> blocks, String mentionUserId,
                                     BotMessageSendPermit permit) {
        if (blocks.stream().anyMatch(BotMessagePart.Keyboard.class::isInstance)
                && !replyProperties.keyboardAllowed(context.botAccountId(), context.scope())) {
            throw new BotMessageNotSentException("QQ 原生键盘资格已变化，尚未发送");
        }
        return this.gatewayClient.apiClient(context.botAccountId()).prepare(
                context.nativeSource(QqOfficialMessageSource.class), blocks, permit);
    }
}
