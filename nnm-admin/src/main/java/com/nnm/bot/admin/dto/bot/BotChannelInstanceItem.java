package com.nnm.bot.admin.dto.bot;

import com.nnm.bot.core.message.BotPlatform;

import java.time.Instant;
import java.time.LocalDateTime;

public record BotChannelInstanceItem(
        long id,
        BotPlatform platform,
        String instanceId,
        String displayName,
        String botAccountId,
        boolean enabled,
        String connectionUrl,
        boolean accessTokenConfigured,
        Long reconnectIntervalSeconds,
        String appId,
        boolean clientSecretConfigured,
        String tokenUrl,
        String apiBaseUrl,
        String gatewayUrl,
        Long intents,
        Long gatewayConnectTimeoutSeconds,
        Long reconnectDelaySeconds,
        String runtimeStatus,
        Instant statusChangedAt,
        String lastError,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
