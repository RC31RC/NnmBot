package com.nnm.bot.warframe.mq.consumerdescriptor.persistent.curated.reference;

import io.github.rdt.consistency.InboxMode;
import io.github.rdt.queue.ConsumerDescriptor;
import io.github.rdt.queue.QueueOptions;
import java.time.Duration;

/** 本地消费并发与重试策略，不接受消息载荷覆盖。 */
public final class WarframeItemIndexInvalidationConsumerDescriptor extends ConsumerDescriptor {
    public static final String GROUP = "warframe-item-index-v1";

    /**
     * 读取管理端展示的缓存失效消费者名称。
     * @return 展示名称对应的文本。
     */
    @Override
    public String displayName() {
        return "Warframe 物品索引失效广播";
    }

    /**
     * 取得当前任务或消费者允许的并发数。
     *
     * @return 当前操作计算或读取的数值。
     */
    @Override
    public int concurrency() {
        return 1;
    }

    /**
     * 读取并组织游戏收件箱资料。
     *
     * @return 收件箱模式对象。
     */
    @Override
    public InboxMode inbox() {
        return InboxMode.DISABLED;
    }

    /**
     * 声明缓存失效消息的消费与重试策略。
     * @return 队列选项对象。
     */
    @Override
    public QueueOptions options() {
        return QueueOptions.builder()
                .maxAttempts(3)
                .retryDelay(Duration.ofSeconds(1))
                .visibilityTimeout(Duration.ofSeconds(5))
                .build();
    }
}
