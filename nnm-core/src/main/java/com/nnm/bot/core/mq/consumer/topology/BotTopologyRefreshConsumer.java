package com.nnm.bot.core.mq.consumer.topology;

import com.nnm.bot.core.mq.message.topology.BotTopologyRefreshMessage;

import com.nnm.bot.core.channel.topology.BotTopologyStore;
import com.nnm.bot.core.mq.messagedescriptor.topology.BotTopologyRefreshMessageDescriptor;
import com.nnm.bot.core.mq.consumerdescriptor.topology.BotTopologyRefreshConsumerDescriptor;
import io.github.rdt.queue.RdtMessageConsumer;
import io.github.rdt.spring.annotation.RdtQueueListener;
import jakarta.annotation.Resource;

@RdtQueueListener(topic = BotTopologyRefreshMessageDescriptor.TOPIC, group = BotTopologyRefreshConsumerDescriptor.GROUP)
public class BotTopologyRefreshConsumer implements RdtMessageConsumer<BotTopologyRefreshMessage> {
    @Resource
    private BotTopologyStore topology;

    @Override
    public void consume(BotTopologyRefreshMessage message) {
        String version = message.payload();
        topology.refreshIfChanged(version);
    }
}
