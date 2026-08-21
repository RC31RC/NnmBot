package com.nnm.bot.core.channel;

import com.nnm.bot.core.message.BotPlatform;

import java.time.Duration;

/** 数据库存储的渠道实例连接信息。核心模块只依赖这个稳定的数据结构。 */
public record BotChannelInstanceRecord(
        BotPlatform platform,
        String instanceId,
        String botAccountId,
        boolean enabled,
        String connectionUrl,
        String accessToken,
        String appId,
        String clientSecret,
        String tokenUrl,
        String apiBaseUrl,
        String gatewayUrl,
        Duration reconnectInterval,
        long intents,
        Duration gatewayConnectTimeout,
        Duration reconnectDelay) {
}
