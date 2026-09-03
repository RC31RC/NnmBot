package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.ResponsePlan;

/**
 * 各消息渠道的输出策略。核心只约定结果形式，具体协议由渠道适配器实现。
 */
public interface BotChannelResponseStrategy {

    BotPlatform platform();

    void send(BotMessageContext context, ResponsePlan plan);
}
