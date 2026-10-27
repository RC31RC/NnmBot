package com.nnm.bot.core.mq.consumerdescriptor.notification;

import io.github.rdt.consistency.InboxMode;
import io.github.rdt.queue.ConsumerDescriptor;
import io.github.rdt.queue.QueueOptions;

/** 本地消费并发与重试策略，不接受消息载荷覆盖。 */
public final class GroupNotificationConsumerDescriptor extends ConsumerDescriptor {
    public static final String GROUP = "bot-group-notification-owners";

    @Override
    public String displayName() {
        return "Group notifications";
    }

    @Override
    public int concurrency() {
        return 2;
    }

    @Override
    public InboxMode inbox() {
        return InboxMode.DISABLED;
    }

    @Override
    public QueueOptions options() {
        return QueueOptions.builder()
                .maxAttempts(1)
                .build();
    }
}
