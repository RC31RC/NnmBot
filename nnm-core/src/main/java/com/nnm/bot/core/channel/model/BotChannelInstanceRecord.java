package com.nnm.bot.core.channel.model;

import com.nnm.bot.core.constant.BotPlatform;

import java.time.Duration;

/**
 * 数据库存储的渠道实例连接信息。核心模块只依赖这个稳定的数据结构。
 *
 * @param platform 接入平台
 * @param instanceId 系统内部实例编码
 * @param botAccountId 平台侧机器人账号
 * @param enabled 是否允许启动
 * @param connectionUrl OneBot WebSocket 地址
 * @param accessToken OneBot 鉴权令牌
 * @param appId QQ 官方机器人应用 ID
 * @param clientSecret QQ 官方机器人应用密钥
 * @param tokenUrl QQ 官方机器人令牌地址
 * @param apiBaseUrl QQ 官方机器人 API 基础地址
 * @param gatewayUrl QQ 官方机器人网关地址
 * @param reconnectInterval OneBot 重连间隔
 * @param intents QQ 官方机器人事件意图
 * @param gatewayConnectTimeout QQ 官方机器人连接超时
 * @param reconnectDelay QQ 官方机器人重连延迟
 */
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
