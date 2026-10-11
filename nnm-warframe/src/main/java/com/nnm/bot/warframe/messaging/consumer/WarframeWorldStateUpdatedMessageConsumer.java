package com.nnm.bot.warframe.messaging.consumer;

import com.nnm.bot.warframe.messaging.model.WarframeWorldStateAvailableEvent;
import io.github.rdt.queue.QueueMode;
import io.github.rdt.queue.RdtMessageConsumer;
import io.github.rdt.spring.annotation.RdtQueueListener;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 消费共享世界状态更新广播，并唤醒当前实例的本地读模型。
 */
@RdtQueueListener(
        value = "warframe-worldstate-updated-v1",
        displayName = "Warframe 世界状态更新广播",
        group = "worldstate-local-v1",
        mode = QueueMode.RELIABLE_BROADCAST
)
public class WarframeWorldStateUpdatedMessageConsumer
        implements RdtMessageConsumer<String> {
    @Resource
    private ApplicationEventPublisher events;

    /**
     * 发布本地 Spring 事件，由世界状态服务读取 Redis 快照并更新本地状态。
     *
     * @param contentHash 共享快照内容哈希
     */
    @Override
    public void consume(String contentHash) {
        events.publishEvent(new WarframeWorldStateAvailableEvent(contentHash));
    }
}
