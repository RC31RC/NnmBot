package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.constant.BotPlatform;

import java.util.OptionalLong;

/** 将运行时平台账号映射为持久化渠道实例 ID。 */
public interface BotChannelInstanceIdentityResolver {

    /**
     * 按平台和机器人账号解析实例主键。
     *
     * @param platform 平台类型
     * @param botAccountId 平台机器人账号标识
     * @return 实例主键；没有匹配配置时为空
     */
    OptionalLong resolveId(BotPlatform platform, String botAccountId);
}
