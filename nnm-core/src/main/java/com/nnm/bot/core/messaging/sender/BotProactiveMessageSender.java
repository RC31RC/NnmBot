package com.nnm.bot.core.messaging.sender;

import com.nnm.bot.core.messaging.constant.BotMessageConstants.PartKind;
import com.nnm.bot.core.channel.constant.BotChannelCapability;
import com.nnm.bot.core.channel.constant.BotPlatform;
import com.nnm.bot.core.messaging.model.BotMessagePart;
import com.nnm.bot.core.messaging.model.BotProactiveTarget;
import com.nnm.bot.core.messaging.model.PreparedBotMessage;
import java.util.List;

/** 主动消息使用独立目标，不借用入站回执或被动回复额度。 */
public interface BotProactiveMessageSender {
    BotPlatform platform();

    boolean supports(BotProactiveTarget target, BotChannelCapability capability);

    boolean supportsComposition(BotProactiveTarget target, List<PartKind> kinds);

    PreparedBotMessage prepare(BotProactiveTarget target, List<BotMessagePart> parts);
}
