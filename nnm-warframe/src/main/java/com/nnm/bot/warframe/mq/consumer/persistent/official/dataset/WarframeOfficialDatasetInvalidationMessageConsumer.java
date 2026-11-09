package com.nnm.bot.warframe.mq.consumer.persistent.official.dataset;

import com.nnm.bot.warframe.config.cache.WarframeDatasetCache;
import com.nnm.bot.warframe.mq.consumerdescriptor.persistent.official.dataset.WarframeOfficialDatasetInvalidationConsumerDescriptor;
import com.nnm.bot.warframe.mq.message.persistent.official.dataset.WarframeOfficialDatasetInvalidationMessage;
import com.nnm.bot.warframe.mq.messagedescriptor.persistent.official.dataset.WarframeOfficialDatasetInvalidationMessageDescriptor;
import io.github.rdt.queue.RdtMessageConsumer;
import io.github.rdt.spring.annotation.RdtQueueListener;
import jakarta.annotation.Resource;

/** 接收官方数据集版本后只标记本地代际，避免再次广播。 */
@RdtQueueListener(topic = WarframeOfficialDatasetInvalidationMessageDescriptor.TOPIC,
    group = WarframeOfficialDatasetInvalidationConsumerDescriptor.GROUP)
public class WarframeOfficialDatasetInvalidationMessageConsumer
    implements RdtMessageConsumer<WarframeOfficialDatasetInvalidationMessage> {

    @Resource
    private WarframeDatasetCache cache;

    /**
     * 处理当前消费者收到的跨实例消息。
     *
     * @param message 待发送或处理的消息。
     */
    @Override
    public void consume(WarframeOfficialDatasetInvalidationMessage message) {
        cache.observe(message.payload());
    }
}
