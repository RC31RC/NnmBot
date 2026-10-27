package com.nnm.bot.core.message.sender;

import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.message.model.BotProactiveTarget;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import java.util.List;

/** 主动消息使用独立目标，不借用入站回执或被动回复额度。 */
public interface BotProactiveMessageSender {
    BotPlatform platform();
    boolean supports(BotProactiveTarget target, BotChannelCapability capability);
    boolean supportsComposition(BotProactiveTarget target, List<PartKind> kinds);
    PreparedBotMessage prepare(BotProactiveTarget target, List<BotMessagePart> parts);
}
