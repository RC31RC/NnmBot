package com.nnm.bot.core.channel;

import java.time.Instant;
import java.util.Set;

/** 供管理端读取的渠道实例运行快照，不回写配置文件。 */
public record BotChannelSnapshot(
        String instanceId,
        BotChannelKey key,
        Set<BotChannelCapability> capabilities,
        BotChannelStatus status,
        Instant statusChangedAt,
        String lastError) {
}
