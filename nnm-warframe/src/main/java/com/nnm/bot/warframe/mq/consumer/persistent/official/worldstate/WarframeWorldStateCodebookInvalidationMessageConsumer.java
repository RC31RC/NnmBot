package com.nnm.bot.warframe.mq.consumer.persistent.official.worldstate;

import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeWorldStateCodebookInvalidator;
import com.nnm.bot.warframe.mq.message.persistent.official.worldstate.WarframeWorldStateCodebookInvalidationMessage;
import com.nnm.bot.warframe.mq.messagedescriptor.persistent.official.worldstate.WarframeWorldStateCodebookInvalidationMessageDescriptor;
import com.nnm.bot.warframe.mq.consumerdescriptor.persistent.official.worldstate.WarframeWorldStateCodebookInvalidationConsumerDescriptor;
import io.github.rdt.queue.RdtMessageConsumer;
import io.github.rdt.spring.annotation.RdtQueueListener;
import jakarta.annotation.Resource;

/** 接收跨实例失效消息后只刷新本实例代码簿，避免再次发布同类消息。 */
@RdtQueueListener(topic = WarframeWorldStateCodebookInvalidationMessageDescriptor.TOPIC, group = WarframeWorldStateCodebookInvalidationConsumerDescriptor.GROUP)
public class WarframeWorldStateCodebookInvalidationMessageConsumer
        implements RdtMessageConsumer<WarframeWorldStateCodebookInvalidationMessage> {

    @Resource
    private WarframeWorldStateCodebookInvalidator invalidator;

    /**
     * 处理当前消费者收到的跨实例消息。
     *
     * @param message 待发送或处理的消息。
     */
    @Override
    public void consume(WarframeWorldStateCodebookInvalidationMessage message) {
        invalidator.reloadLocal();
    }
}
