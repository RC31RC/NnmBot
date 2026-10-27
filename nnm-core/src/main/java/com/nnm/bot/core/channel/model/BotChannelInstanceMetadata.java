package com.nnm.bot.core.channel.model;

import com.nnm.bot.core.constant.BotPlatform;

/** 身份和路由只需这些元数据，不接触连接凭据。 */
public record BotChannelInstanceMetadata(BotPlatform platform, String instanceId, String botAccountId, boolean enabled) {
    public static BotChannelInstanceMetadata of(BotChannelInstanceRecord record) {
        return new BotChannelInstanceMetadata(record.platform(), record.instanceId(), record.botAccountId(), record.enabled());
    }
}
