package com.nnm.bot.core.channel.messaging.producer;

import com.nnm.bot.core.channel.messaging.model.OneBotScopeDiscoveryRequest;
import io.github.rdt.queue.MessageQueueTemplate;
import jakarta.annotation.Resource;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 发布 OneBot 群组发现请求。
 *
 * <p>生产者只负责消息投递，业务协调和本地平台调用分别由上层服务与消费者负责。</p>
 */
@Service
@ConditionalOnProperty(prefix = "nnm.distributed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OneBotScopeDiscoveryMessageProducer {
    private static final String DESTINATION = "onebot-scope-discovery-v1";

    @Resource
    private MessageQueueTemplate messages;

    public void publish(OneBotScopeDiscoveryRequest request) {
        messages.reliableBroadcast(DESTINATION, Objects.requireNonNull(request, "request"));
    }
}
