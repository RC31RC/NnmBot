package com.nnm.bot.warframe.mq.consumer.persistent.curated.reference;

import com.nnm.bot.warframe.config.cache.WarframeDatasetCache;
import com.nnm.bot.warframe.mq.consumerdescriptor.persistent.curated.reference.WarframeCommunityDatasetInvalidationConsumerDescriptor;
import com.nnm.bot.warframe.mq.message.persistent.curated.reference.WarframeCommunityDatasetInvalidationMessage;
import com.nnm.bot.warframe.mq.messagedescriptor.persistent.curated.reference.WarframeCommunityDatasetInvalidationMessageDescriptor;
import io.github.rdt.queue.RdtMessageConsumer;
import io.github.rdt.spring.annotation.RdtQueueListener;
import jakarta.annotation.Resource;

/**
 * 消费社区数据集版本广播，只更新本实例的所属数据集版本。
 */
@RdtQueueListener(topic = WarframeCommunityDatasetInvalidationMessageDescriptor.TOPIC,
    group = WarframeCommunityDatasetInvalidationConsumerDescriptor.GROUP)
public class WarframeCommunityDatasetInvalidationMessageConsumer
    implements RdtMessageConsumer<WarframeCommunityDatasetInvalidationMessage> {
    @Resource
    private WarframeDatasetCache cache;

    /**
     * 记录当前实例收到的社区数据集版本。
     *
     * @param message 待发送或处理的消息。
     */
    @Override
    public void consume(WarframeCommunityDatasetInvalidationMessage message) {
        cache.observe(message.payload());
    }
}
