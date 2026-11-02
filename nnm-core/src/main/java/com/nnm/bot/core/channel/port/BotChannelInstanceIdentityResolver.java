package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.constant.BotPlatform;

import java.util.OptionalLong;

/**
 * 将运行时渠道身份解析为持久化实例标识。 将运行时通道标识解析为其持久实例标识符。
 */
public interface BotChannelInstanceIdentityResolver {

    OptionalLong resolveId(BotPlatform platform, String botAccountId);
}
