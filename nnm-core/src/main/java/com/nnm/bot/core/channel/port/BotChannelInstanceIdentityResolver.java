package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.constant.BotPlatform;

import java.util.OptionalLong;

/**
 * Resolves a runtime channel identity to its persistent instance identifier.
 * 将运行时通道标识解析为其持久实例标识符。
 */
public interface BotChannelInstanceIdentityResolver {

    OptionalLong resolveId(BotPlatform platform, String botAccountId);
}
