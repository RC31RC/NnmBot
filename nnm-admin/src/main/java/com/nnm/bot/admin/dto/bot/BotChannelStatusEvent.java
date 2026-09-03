package com.nnm.bot.admin.dto.bot;

import com.nnm.bot.core.constant.BotChannelStatus;
import com.nnm.bot.core.constant.BotPlatform;

import java.time.Instant;

/** 管理端订阅的机器人实例运行状态事件。 */
public record BotChannelStatusEvent(
        String instanceId,
        BotPlatform platform,
        String botAccountId,
        BotChannelStatus status,
        Instant statusChangedAt,
        String lastError) {
}
