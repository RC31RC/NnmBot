package com.nnm.bot.core.mq.messagedescriptor.topology;

import com.nnm.bot.core.mq.consumerdescriptor.topology.BotTopologyRefreshConsumerDescriptor;
import io.github.rdt.queue.ConsumerDescriptor;
import io.github.rdt.queue.MessageDescriptor;
import io.github.rdt.queue.QueueMode;

/** 主题与投递模式不依赖消息载荷或远端元数据。 */
public abstract class BotTopologyRefreshMessageDescriptor extends MessageDescriptor<String> {
    public static final String TOPIC = "bot-topology-refresh-v1";

    protected BotTopologyRefreshMessageDescriptor() {
    }

    protected BotTopologyRefreshMessageDescriptor(String payload) {
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
        return new BotTopologyRefreshConsumerDescriptor();
    }
}
