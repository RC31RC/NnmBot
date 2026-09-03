package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.constant.BotPlatform;

/**
 * 提供渠道侧的作用域发现能力。发现结果只用于管理端展示，不负责持久化。
 */
public interface BotScopeDiscoveryProvider {

    BotPlatform platform();

    BotScopeDiscoveryResult discover(BotChannelInstanceRecord instance);
}
