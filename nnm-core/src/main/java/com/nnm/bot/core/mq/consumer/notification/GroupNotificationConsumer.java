package com.nnm.bot.core.mq.consumer.notification;

import com.nnm.bot.core.constant.NotificationConstants.GroupResultStatus;

import com.nnm.bot.core.mq.message.notification.GroupNotificationRequest;
import com.nnm.bot.core.message.model.BotProactiveTarget;
import io.github.rdt.lock.LockScope;
import com.nnm.bot.core.mq.message.notification.GroupNotificationMessage;
import com.nnm.bot.core.channel.notification.GroupNotificationResult;
import com.nnm.bot.core.channel.notification.store.GroupNotificationResultStore;
import com.nnm.bot.core.channel.notification.store.NotificationCancellationStore;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.plugin.response.ProactiveReplyDispatcher;
import com.nnm.bot.core.plugin.response.ReplyDeliveryException;
import com.nnm.bot.core.plugin.model.DeliveryReport;
import io.github.rdt.lock.DistributedLockManager;
import com.nnm.bot.core.mq.messagedescriptor.notification.GroupNotificationMessageDescriptor;
import com.nnm.bot.core.mq.consumerdescriptor.notification.GroupNotificationConsumerDescriptor;
import io.github.rdt.queue.RdtMessageConsumer;
import io.github.rdt.spring.annotation.RdtQueueListener;
import jakarta.annotation.Resource;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RdtQueueListener(topic = GroupNotificationMessageDescriptor.TOPIC, group = GroupNotificationConsumerDescriptor.GROUP)
public class GroupNotificationConsumer implements RdtMessageConsumer<GroupNotificationMessage> {
    @Resource
    private ClusterBotRuntime runtime;
    @Resource
    private ProactiveReplyDispatcher dispatcher;
    @Resource
    private GroupNotificationResultStore results;
    @Resource
    private DistributedLockManager locks;
    @Resource
    private NotificationCancellationStore cancellations;

    @Override
    public void consume(GroupNotificationMessage message) {
        GroupNotificationRequest request = message.payload();
        if (request == null || request.requestId() == null || request.expiresAt() == null
                || !Instant.now().isBefore(request.expiresAt())) return;
        BotProactiveTarget target = request.target();
        BotChannelKey key = new BotChannelKey(target.platform(), target.botAccountId());
        if (!runtime.isLocalOwner(key)) return;
        try (LockScope scope = locks.beginScope()) {
            locks.lock("group-notification:" + request.requestId());
            if (results.find(request.requestId()).isPresent() || !runtime.isLocalOwner(key)
                    || !Instant.now().isBefore(request.expiresAt())) return;
            if (cancellations.cancelled(request.resources().submissionId())) {
                results.save(request.requestId(), new GroupNotificationResult(GroupResultStatus.CANCELLED,
                        "通知已取消，尚未发送", null));
                return;
            }
            results.save(request.requestId(), new GroupNotificationResult(
                    GroupResultStatus.IN_PROGRESS, "通知正在发送", null));
            try {
                DeliveryReport report = dispatcher.dispatch(target, request.content(), request.resources(), request.expiresAt());
                results.save(request.requestId(), outcome(report));
            } catch (RuntimeException error) {
                GroupNotificationResult result = error instanceof ReplyDeliveryException delivery
                        ? outcome(delivery.report()) : new GroupNotificationResult(GroupResultStatus.UNKNOWN,
                                "渠道发送结果未知，请核实后再决定是否重试", null);
                try {
                    results.save(request.requestId(), result);
                } catch (RuntimeException persistenceFailure) {
                    error.addSuppressed(persistenceFailure);
                }
                log.error("Group notification outcome unknown request={} platform={} account={} conversation={}",
                        request.requestId(), target.platform(), target.botAccountId(), target.conversationId(), error);
                throw error;
            }
            log.info("Group notification sent request={} platform={} account={} conversation={}",
                    request.requestId(), target.platform(), target.botAccountId(), target.conversationId());
        }
    }

    private GroupNotificationResult outcome(DeliveryReport report) {
        return switch (report.outcome()) {
            case COMPLETE -> new GroupNotificationResult(GroupResultStatus.SUCCEEDED, "全部平台消息已接受", report);
            case PARTIAL -> new GroupNotificationResult(GroupResultStatus.PARTIAL, "部分消息已接受，后续消息停止发送", report);
            case UNKNOWN -> new GroupNotificationResult(GroupResultStatus.UNKNOWN, "部分消息结果未知，不自动重发", report);
            case FAILED, SILENT -> new GroupNotificationResult(GroupResultStatus.FAILED, "通知未发送，请查看逐条结果", report);
        };
    }
}
