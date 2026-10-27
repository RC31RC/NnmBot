package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;

/** Executes scope discovery on the node that owns a channel instance. */
public interface BotLocalScopeDiscoveryProvider extends BotScopeDiscoveryProvider {

    BotScopeDiscoveryResult discoverLocal(String botAccountId);
}
