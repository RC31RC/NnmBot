package com.nnm.bot.warframe.messaging.producer;

import io.github.rdt.queue.MessageQueueTemplate;
import jakarta.annotation.Resource;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 发布 Warframe 世界状态快照已更新的广播。
 *
 * <p>消息只携带内容哈希，完整快照始终从 Redis 共享存储读取，避免广播大 JSON。</p>
 */
@Service
@ConditionalOnProperty(
        prefix = "nnm.distributed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class WarframeWorldStateUpdatedMessageProducer {
    private static final String DESTINATION = "warframe-worldstate-updated-v1";

    @Resource
    private MessageQueueTemplate messages;

    /**
     * 广播共享快照更新通知。
     *
     * @param contentHash 快照内容哈希
     */
    public void publish(String contentHash) {
        messages.reliableBroadcast(DESTINATION, Objects.requireNonNull(contentHash, "contentHash"));
    }
}
