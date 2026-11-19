package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;

/** 在拥有渠道实例的节点上执行作用域发现。 */
public interface BotLocalScopeDiscoveryProvider extends BotScopeDiscoveryProvider {

    /** 在当前节点持有的指定账号会话上执行发现。
     *
     * @param botAccountId 平台机器人账号标识
     * @return 当前会话可见的作用域
     */
    BotScopeDiscoveryResult discoverLocal(String botAccountId);
}
