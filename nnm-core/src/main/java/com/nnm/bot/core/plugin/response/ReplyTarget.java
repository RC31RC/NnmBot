package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import java.util.List;

/** 规划仅使用目标能力与预算；主动目标无需构造入站消息或借用回复回执。 */
public interface ReplyTarget {
    boolean supports(BotChannelCapability capability);
    boolean supportsComposition(List<PartKind> kinds);
    boolean canReserve(int messages);
    String mentionUserId();
}
