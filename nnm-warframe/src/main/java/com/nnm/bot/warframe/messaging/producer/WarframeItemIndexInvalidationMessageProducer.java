package com.nnm.bot.warframe.messaging.producer;

import io.github.rdt.queue.MessageQueueTemplate;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 发布 Warframe 物品解析索引失效广播。
 */
@Service
@ConditionalOnProperty(
        prefix = "nnm.distributed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class WarframeItemIndexInvalidationMessageProducer {
    private static final String DESTINATION = "warframe-item-index-invalidated-v1";

    @Resource
    private MessageQueueTemplate messages;

    /**
     * 广播本地物品解析索引失效。
     */
    public void publish() {
        messages.reliableBroadcast(DESTINATION, "invalidate");
    }
}
