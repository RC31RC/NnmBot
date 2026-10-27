package com.nnm.bot.warframe.mq.consumer.persistent.curated.reference;

import com.nnm.bot.warframe.persistent.curated.cache.reference.WarframeItemIndexInvalidator;
import com.nnm.bot.warframe.mq.message.persistent.curated.reference.WarframeItemIndexInvalidationMessage;
import com.nnm.bot.warframe.mq.messagedescriptor.persistent.curated.reference.WarframeItemIndexInvalidationMessageDescriptor;
import com.nnm.bot.warframe.mq.consumerdescriptor.persistent.curated.reference.WarframeItemIndexInvalidationConsumerDescriptor;
import io.github.rdt.queue.RdtMessageConsumer;
import io.github.rdt.spring.annotation.RdtQueueListener;
import jakarta.annotation.Resource;

/**
 * 消费物品解析索引失效广播，并清理当前实例的本地索引。
 */
@RdtQueueListener(topic = WarframeItemIndexInvalidationMessageDescriptor.TOPIC, group = WarframeItemIndexInvalidationConsumerDescriptor.GROUP)
public class WarframeItemIndexInvalidationMessageConsumer implements RdtMessageConsumer<WarframeItemIndexInvalidationMessage> {
    @Resource
    private WarframeItemIndexInvalidator invalidator;

    /**
     * 清理当前实例的本地物品解析索引。
     *
     * @param 当前固定为 invalidate，参数保留用于协议扩展
     * @param message 待发送或处理的消息。
     */
    @Override
    public void consume(WarframeItemIndexInvalidationMessage message) {
        String invalidationCommand = message.payload();
        invalidator.clearLocal();
    }
}
