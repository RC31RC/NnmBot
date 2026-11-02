package com.nnm.bot.core.channel.model;

import com.nnm.bot.core.group.domain.BotGroupBot;

/** 逻辑群组绑定及其本地运行实例。 */
public record BotChannelTarget(BotGroupBot binding, BotChannelInstanceMetadata instance) {

    public BotChannelKey key() {
        return new BotChannelKey(instance.platform(), instance.botAccountId());
    }
}
