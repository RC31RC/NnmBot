package com.nnm.bot.core.channel.model;

import com.nnm.bot.core.group.domain.BotGroupBot;

/** A logical-group binding and its local runtime instance. */
public record BotChannelTarget(BotGroupBot binding, BotChannelInstanceMetadata instance) {

    public BotChannelKey key() {
        return new BotChannelKey(instance.platform(), instance.botAccountId());
    }
}
