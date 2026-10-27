package com.nnm.bot.admin.pojo.dto.bot.channel;

import com.nnm.bot.core.constant.BotPlatform;

import java.time.Instant;
import java.time.LocalDateTime;

/** 管理端展示的机器人实例信息。 */
public record BotChannelInstanceDTO(
        /** 实例数据库主键。 */
        long id,
        /** 接入平台。 */
        BotPlatform platform,
        /** 系统内部实例编码。 */
        String instanceId,
        /** 实例展示名称。 */
        String displayName,
        /** 平台侧机器人账号。 */
        String botAccountId,
        /** 是否启用。 */
        boolean enabled,
        /** OneBot WebSocket 地址。 */
        String connectionUrl,
        /** 是否已配置 OneBot 令牌。 */
        boolean accessTokenConfigured,
        /** OneBot 重连间隔秒数。 */
        Long reconnectIntervalSeconds,
        /** QQ 官方应用 ID。 */
        String appId,
        /** 是否已配置 QQ 官方应用密钥。 */
        boolean clientSecretConfigured,
        /** QQ 官方令牌地址。 */
        String tokenUrl,
        /** QQ 官方 API 基础地址。 */
        String apiBaseUrl,
        /** QQ 官方网关地址。 */
        String gatewayUrl,
        /** QQ 官方事件意图。 */
        Long intents,
        /** QQ 官方网关连接超时秒数。 */
        Long gatewayConnectTimeoutSeconds,
        /** QQ 官方重连延迟秒数。 */
        Long reconnectDelaySeconds,
        /** 当前运行状态。 */
        String runtimeStatus,
        /** 运行状态变更时间。 */
        Instant statusChangedAt,
        /** 最近一次运行错误。 */
        String lastError,
        /** 创建时间。 */
        LocalDateTime createdAt,
        /** 最后更新时间。 */
        LocalDateTime updatedAt) {
}
