package com.nnm.bot.core.qqofficial.message;

import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.QqOfficialChatType;
import com.nnm.bot.core.message.model.BotProactiveGroupMessage;
import com.nnm.bot.core.message.sender.BotProactiveMessageSender;
import com.nnm.bot.core.qqofficial.gateway.QqOfficialGatewayClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * QQ 官方机器人主动群消息适配器。
 *
 * <p>当前渠道不拼接 OneBot CQ 码，成员提醒降级为普通群文本。</p>
 */
@Component
public class QqOfficialProactiveMessageSender implements BotProactiveMessageSender {

    @Resource
    private QqOfficialGatewayClient gatewayClient;

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public void sendGroupText(BotProactiveGroupMessage message) {
        Assert.notNull(message, "message must not be null");
        Assert.hasText(message.text(), "message.text must not be blank");
        QqOfficialMessageSource source = new QqOfficialMessageSource(
                "PROACTIVE_GROUP_MESSAGE",
                QqOfficialChatType.GROUP,
                message.conversationId(),
                null,
                message.botAccountId(),
                null);
        gatewayClient.apiClient(message.botAccountId()).sendText(source, message.text());
    }
}
