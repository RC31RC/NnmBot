package com.nnm.bot.core.mq.messagedescriptor.discovery;

import com.nnm.bot.core.mq.consumerdescriptor.discovery.OneBotScopeDiscoveryConsumerDescriptor;
import com.nnm.bot.core.mq.message.discovery.OneBotScopeDiscoveryRequest;
import io.github.rdt.queue.ConsumerDescriptor;
import io.github.rdt.queue.MessageDescriptor;
import io.github.rdt.queue.QueueMode;

/** 主题与投递模式不依赖消息载荷或远端元数据。 */
public abstract class OneBotScopeDiscoveryMessageDescriptor extends MessageDescriptor<OneBotScopeDiscoveryRequest> {
    public static final String TOPIC = "onebot-scope-discovery-v1";

    protected OneBotScopeDiscoveryMessageDescriptor() {
    }

    protected OneBotScopeDiscoveryMessageDescriptor(OneBotScopeDiscoveryRequest payload) {
        super(payload);
    }

    @Override
    public String topic() {
        return TOPIC;
    }

    @Override
    public QueueMode mode() {
        return QueueMode.RELIABLE_BROADCAST;
    }

    @Override
    public ConsumerDescriptor consumer(String group) {
        return new OneBotScopeDiscoveryConsumerDescriptor();
    }
}
