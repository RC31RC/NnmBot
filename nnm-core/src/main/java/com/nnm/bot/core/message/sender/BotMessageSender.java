package com.nnm.bot.core.message.sender;

import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import java.util.List;
import java.util.stream.IntStream;

public interface BotMessageSender {

    BotPlatform platform();

    boolean supports(BotMessageContext context, BotChannelCapability capability);

    boolean supportsComposition(BotMessageContext context, List<PartKind> parts);

    default boolean canReserve(BotMessageContext context, int count) {
        return count > 0;
    }

    /** 整组预留必须原子完成；无协议额度的平台只提供生命周期句柄。 */
    default List<BotMessageSendPermit> reserve(BotMessageContext context, int count) {
        if (count < 1) throw new IllegalArgumentException("count must be positive");
        return IntStream.range(0, count).mapToObj(index -> BotMessageSendPermit.unrestricted()).toList();
    }

    /** 可上传或暂存资源，但不得创建平台消息；调用方负责关闭返回对象。 */
    PreparedBotMessage prepare(BotMessageContext context, List<BotMessagePart> blocks, String mentionUserId,
                               BotMessageSendPermit permit);
}
