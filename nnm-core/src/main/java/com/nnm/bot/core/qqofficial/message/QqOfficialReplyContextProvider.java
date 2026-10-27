package com.nnm.bot.core.qqofficial.message;

import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.channel.port.BotReplyContextProvider;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.QqOfficialChatType;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.message.model.BotMessageContext;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 为 QQ 官方机器人创建主动群回复上下文。
 *
 * <p>QQ 官方群消息发送不要求引用原消息 ID，因此跨渠道回复可以使用同一
 * {@code group_openid} 创建新的发送上下文；不会借用 OneBot 的消息 ID 或用户标识。</p>
 */
@Component
public class QqOfficialReplyContextProvider implements BotReplyContextProvider {

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public Optional<BotMessageContext> create(BotChannelInstanceMetadata instance,
                                               BotGroupBot binding,
                                               BotMessageContext origin,
                                               String senderId) {
        if (!"GROUP".equals(binding.getScopeType())) {
            return Optional.empty();
        }
        QqOfficialMessageSource source = new QqOfficialMessageSource(
                "PROACTIVE_GROUP_MESSAGE",
                QqOfficialChatType.GROUP,
                binding.getScopeKey(),
                null,
                instance.botAccountId(),
                null,
                null);
        BotMessageContext context = BotMessageContext.of(
                BotPlatform.QQ_OFFICIAL,
                BotMessageScope.GROUP,
                instance.botAccountId(),
                senderId == null || senderId.isBlank() ? "system" : senderId,
                binding.getScopeKey(),
                null,
                origin.rawMessage(),
                origin.message(),
                source,
                origin.attachments());
        return Optional.of(origin.dispatchId() == null
                ? context
                : context.withDispatchId(origin.dispatchId()));
    }
}
