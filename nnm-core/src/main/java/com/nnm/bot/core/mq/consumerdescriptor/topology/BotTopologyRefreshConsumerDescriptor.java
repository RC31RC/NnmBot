package com.nnm.bot.core.mq.consumerdescriptor.topology;

import io.github.rdt.consistency.InboxMode;
import io.github.rdt.queue.ConsumerDescriptor;
import io.github.rdt.queue.QueueOptions;
import java.time.Duration;

/** 本地消费并发与重试策略，不接受消息载荷覆盖。 */
public final class BotTopologyRefreshConsumerDescriptor extends ConsumerDescriptor {
    public static final String GROUP = "bot-topology-v1";

    @Override
    public String displayName() {
        return "机器人拓扑刷新";
    }

    @Override
    public int concurrency() {
        return 1;
    }

    @Override
    public InboxMode inbox() {
        return InboxMode.DISABLED;
    }

    @Override
    public QueueOptions options() {
        return QueueOptions.builder()
                .maxAttempts(3)
                .retryDelay(Duration.ofSeconds(1))
                .visibilityTimeout(Duration.ofSeconds(5))
                .build();
    }
}
