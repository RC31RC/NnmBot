package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.constant.BotPlatform;

/** 连接器只管理隔离的协议会话，不负责集群分配或命令执行。 */
public interface BotChannelConnector {

    /** 返回此连接器实现的平台类型。 */
    BotPlatform platform();

    /** 启动指定平台账号的协议会话。
     *
     * @param instance 当前持久化渠道配置快照
     */
    void startInstance(BotChannelInstanceRecord instance);

    /** 停止指定实例并释放其会话资源。
     *
     * @param instanceId 持久化渠道实例标识
     */
    void stopInstance(String instanceId);
}
