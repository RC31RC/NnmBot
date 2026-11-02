package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.constant.BotPlatform;

/** 连接器只管理隔离的协议会话，不负责集群分配或命令执行。 */
public interface BotChannelConnector {
    BotPlatform platform();

    void startInstance(BotChannelInstanceRecord instance);

    void stopInstance(String instanceId);
}
