package com.nnm.bot.core.qqofficial.message;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.channel.port.BotMessageReadProvider;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.BotMessageConstants.ReferenceKind;
import com.nnm.bot.core.constant.QqOfficialChatType;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.qqofficial.gateway.QqOfficialGatewayClient;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

/** QQ 引用优先由事件及缓存解析；只有频道提供按消息 ID 读取的协议接口。 */
@Component
public class QqOfficialMessageReadProvider implements BotMessageReadProvider {

    @Resource
    private QqOfficialGatewayClient gateway;

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public boolean canReadReference(BotMessageContext context) {
        return context.reference() != null && context.reference().kind() == ReferenceKind.MESSAGE_ID
            && context.nativeSource(QqOfficialMessageSource.class).chatType() == QqOfficialChatType.CHANNEL;
    }

    @Override
    public List<BotIncomingAttachment> readReference(BotMessageContext context) {
        if (!canReadReference(context))
            throw unavailable();
        return QqOfficialMessageContextFactory.attachments(gateway.apiClient(context.botAccountId())
            .readChannelMessage(context.conversationId(), context.reference().id()));
    }

    @Override
    public BotIncomingAttachment resolveAttachment(BotMessageContext context, BotIncomingAttachment attachment) {
        throw unavailable();
    }

    /** 不虚构群聊/C2C 的历史或文件 ID 下载接口。 */
    private BusinessException unavailable() {
        return new BusinessException(BotChannelErrorCode.REFERENCED_IMAGE_UNAVAILABLE);
    }
}
