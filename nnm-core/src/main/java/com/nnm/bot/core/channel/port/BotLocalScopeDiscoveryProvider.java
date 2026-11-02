package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;

/** 在拥有渠道实例的节点上执行作用域发现。 */
public interface BotLocalScopeDiscoveryProvider extends BotScopeDiscoveryProvider {

    BotScopeDiscoveryResult discoverLocal(String botAccountId);
}
