package com.nnm.bot.core.qqofficial.message;

import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.QqOfficialChatType;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.message.model.BotProactiveTarget;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.message.sender.BotProactiveMessageSender;
import com.nnm.bot.core.qqofficial.config.QqReplyProperties;
import com.nnm.bot.core.qqofficial.gateway.QqOfficialGatewayClient;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * QQ 官方主动群消息必须按已确认的账号和群资格显式启用。
 */
@Component
public class QqOfficialProactiveMessageSender implements BotProactiveMessageSender {
    @Resource
    private QqOfficialGatewayClient gatewayClient;
    @Resource
    private QqReplyProperties replyProperties;

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public boolean supports(BotProactiveTarget target, BotChannelCapability capability) {
        if (!qualified(target)) return false;
        if (capability == BotChannelCapability.NATIVE_KEYBOARD) {
            return replyProperties.keyboardAllowed(target.botAccountId(), BotMessageScope.GROUP);
        }
        return capability == BotChannelCapability.TEXT_MESSAGE || capability == BotChannelCapability.IMAGE_MESSAGE
                || capability == BotChannelCapability.MARKDOWN_MESSAGE || capability == BotChannelCapability.FILE_MESSAGE;
    }

    @Override
    public boolean supportsComposition(BotProactiveTarget target, List<PartKind> kinds) {
        if (!qualified(target)) return false;
        if (kinds.contains(PartKind.KEYBOARD) || kinds.contains(PartKind.MARKDOWN)) {
            return kinds.stream().allMatch(kind -> kind == PartKind.TEXT
                    || kind == PartKind.MARKDOWN || kind == PartKind.KEYBOARD)
                    && (!kinds.contains(PartKind.KEYBOARD)
                    || replyProperties.keyboardAllowed(target.botAccountId(), BotMessageScope.GROUP));
        }
        return kinds.equals(List.of(PartKind.TEXT, PartKind.IMAGE))
                || kinds.equals(List.of(PartKind.TEXT, PartKind.FILE));
    }

    @Override
    public PreparedBotMessage prepare(BotProactiveTarget target, List<BotMessagePart> parts) {
        if (!qualified(target)) {
            throw new BotMessageNotSentException("QQ 主动群消息未通过账号和群组资格登记");
        }
        if (parts.stream().anyMatch(BotMessagePart.Keyboard.class::isInstance)
                && !replyProperties.keyboardAllowed(target.botAccountId(), BotMessageScope.GROUP)) {
            throw new BotMessageNotSentException("QQ 原生键盘资格已变化，尚未发送");
        }
        QqOfficialMessageSource source = new QqOfficialMessageSource("PROACTIVE_GROUP_MESSAGE",
                QqOfficialChatType.GROUP, target.conversationId(), null, target.botAccountId(), null, null);
        return gatewayClient.apiClient(target.botAccountId()).prepare(source, parts,
                BotMessageSendPermit.unrestricted());
    }

    private boolean qualified(BotProactiveTarget target) {
        return replyProperties.proactiveGroupAllowed(target.botAccountId(), target.conversationId());
    }
}
