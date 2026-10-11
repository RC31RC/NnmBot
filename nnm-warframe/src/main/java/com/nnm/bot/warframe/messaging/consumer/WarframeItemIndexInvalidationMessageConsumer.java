package com.nnm.bot.warframe.messaging.consumer;

import com.nnm.bot.warframe.cache.WarframeItemIndexInvalidator;
import io.github.rdt.queue.QueueMode;
import io.github.rdt.queue.RdtMessageConsumer;
import io.github.rdt.spring.annotation.RdtQueueListener;
import jakarta.annotation.Resource;

/**
 * 消费物品解析索引失效广播，并清理当前实例的本地索引。
 */
@RdtQueueListener(
        value = "warframe-item-index-invalidated-v1",
        displayName = "Warframe 物品索引失效广播",
        group = "warframe-item-index-v1",
        mode = QueueMode.RELIABLE_BROADCAST
)
public class WarframeItemIndexInvalidationMessageConsumer
        implements RdtMessageConsumer<String> {
    @Resource
    private WarframeItemIndexInvalidator invalidator;

    /**
     * 清理当前实例的本地物品解析索引。
     *
     * @param invalidationCommand 当前固定为 invalidate，参数保留用于协议扩展
     */
    @Override
    public void consume(String invalidationCommand) {
        invalidator.clearLocal();
    }
}
