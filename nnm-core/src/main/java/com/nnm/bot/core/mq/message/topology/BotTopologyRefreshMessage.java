package com.nnm.bot.core.mq.message.topology;

import com.nnm.bot.core.mq.messagedescriptor.topology.BotTopologyRefreshMessageDescriptor;

/** 跨实例传输消息；发布和消费策略由独立描述符定义。 */
public final class BotTopologyRefreshMessage extends BotTopologyRefreshMessageDescriptor {
    public BotTopologyRefreshMessage() {
    }

    public BotTopologyRefreshMessage(String payload) {
        super(payload);
    }
}
