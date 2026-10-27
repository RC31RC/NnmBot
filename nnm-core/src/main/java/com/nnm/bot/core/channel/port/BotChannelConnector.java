package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.constant.BotPlatform;

/** A connector owns isolated protocol sessions, never cluster placement or command execution. */
public interface BotChannelConnector {
    BotPlatform platform();

    void startInstance(BotChannelInstanceRecord instance);

    void stopInstance(String instanceId);
}
