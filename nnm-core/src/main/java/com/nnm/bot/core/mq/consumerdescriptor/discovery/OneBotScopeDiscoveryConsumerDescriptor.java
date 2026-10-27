package com.nnm.bot.core.mq.consumerdescriptor.discovery;

import io.github.rdt.consistency.InboxMode;
import io.github.rdt.queue.ConsumerDescriptor;
import io.github.rdt.queue.QueueOptions;
import java.time.Duration;

/** 本地消费并发与重试策略，不接受消息载荷覆盖。 */
public final class OneBotScopeDiscoveryConsumerDescriptor extends ConsumerDescriptor {
    public static final String GROUP = "onebot-scope-discovery-owners-v1";

    @Override
    public String displayName() {
        return "OneBot 群组发现请求";
    }

    @Override
    public int concurrency() {
        return 20;
    }

    @Override
    public InboxMode inbox() {
        return InboxMode.DISABLED;
    }

    @Override
    public QueueOptions options() {
        return QueueOptions.builder()
                .maxAttempts(2)
                .retryDelay(Duration.ofSeconds(2))
                .maxRetryDelay(Duration.ofSeconds(5))
                .visibilityTimeout(Duration.ofSeconds(15))
                .build();
    }
}
