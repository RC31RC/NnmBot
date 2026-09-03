package com.nnm.bot.core.channel.model;

import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotChannelStatus;

import java.time.Instant;
import java.util.Set;

/**
 * 供管理端读取的渠道实例运行快照，不回写配置文件。
 *
 * @param instanceId 系统内部实例编码
 * @param key 运行实例的渠道账号键
 * @param capabilities 当前支持的能力集合
 * @param status 当前运行状态
 * @param statusChangedAt 状态最后变更时间
 * @param lastError 最近一次错误信息
 */
public record BotChannelSnapshot(
        String instanceId,
        BotChannelKey key,
        Set<BotChannelCapability> capabilities,
        BotChannelStatus status,
        Instant statusChangedAt,
        String lastError) {
}
