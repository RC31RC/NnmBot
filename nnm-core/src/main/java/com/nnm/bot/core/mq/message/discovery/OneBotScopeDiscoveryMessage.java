package com.nnm.bot.core.mq.message.discovery;

import com.nnm.bot.core.mq.messagedescriptor.discovery.OneBotScopeDiscoveryMessageDescriptor;

/** 跨实例传输消息；发布和消费策略由独立描述符定义。 */
public final class OneBotScopeDiscoveryMessage extends OneBotScopeDiscoveryMessageDescriptor {
    public OneBotScopeDiscoveryMessage() {
    }

    public OneBotScopeDiscoveryMessage(OneBotScopeDiscoveryRequest payload) {
        super(payload);
    }
}
