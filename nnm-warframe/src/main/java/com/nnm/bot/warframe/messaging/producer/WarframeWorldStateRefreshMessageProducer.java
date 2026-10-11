package com.nnm.bot.warframe.messaging.producer;

import io.github.rdt.queue.MessageQueueTemplate;
import io.github.rdt.queue.QueueMode;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 发布 Warframe 世界状态刷新请求。
 *
 * <p>生产者只负责投递消息，不直接执行刷新业务。刷新业务由独立消费者完成，
 * 这样定时任务、管理端手动刷新和其他触发方都可以复用同一条消息入口。</p>
 */
@Service
@ConditionalOnProperty(
        prefix = "nnm.distributed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class WarframeWorldStateRefreshMessageProducer {
    private static final String DESTINATION = "worldstate-refresh-v1";

    @Resource
    private MessageQueueTemplate messages;

    /**
     * 发布“刷新最新世界状态”命令。
     */
    public void publishRefreshRequest() {
        messages.send(DESTINATION, "latest", QueueMode.STREAM);
    }
}
