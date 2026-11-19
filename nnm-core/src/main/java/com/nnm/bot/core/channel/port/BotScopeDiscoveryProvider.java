package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.constant.BotPlatform;

/**
 * 提供渠道侧的作用域发现能力。发现结果只用于管理端展示，不负责持久化。
 */
public interface BotScopeDiscoveryProvider {

    /** 返回此发现实现所属的平台。 */
    BotPlatform platform();

    /** 查询当前账号可发现的作用域；实现不应写入业务作用域表。
     *
     * @param instance 执行发现的渠道实例
     * @return 供管理端确认的发现结果
     */
    BotScopeDiscoveryResult discover(BotChannelInstanceRecord instance);
}
