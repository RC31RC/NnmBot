package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.message.model.BotMessageReceipt;
import com.nnm.bot.core.plugin.model.DeliveryReport.Item;
import com.nnm.bot.core.mq.message.notification.NotificationContent;
import com.nnm.bot.core.mq.message.notification.NotificationResourceManifest;
import com.nnm.bot.core.channel.notification.store.NotificationResourceStorage;
import com.nnm.bot.core.channel.notification.store.NotificationCancellationStore;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.exception.BotMessageRejectedException;
import com.nnm.bot.core.message.model.BotProactiveTarget;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.message.sender.BotProactiveMessageSenderRouter;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.plugin.model.DeliveryReport;
import com.nnm.bot.core.constant.CommandDeliveryConstants.DeliveryStatus;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** 与命令共用内容、规划和报告；主动发送不执行插件、不扣费、不借被动回执。 */
@Slf4j
@Component
public class ProactiveReplyDispatcher {
    @Resource
    private ReplyPlanner planner;
    @Resource
    private BotProactiveMessageSenderRouter senders;
    @Resource
    private NotificationResourceStorage resources;
    @Resource
    private ClusterBotRuntime runtime;
    @Resource
    private NotificationCancellationStore cancellations;
    @Resource
    private NotificationDeliveryGuard deliveryGuard;

    public List<ResponsePlan> preview(BotProactiveTarget target, NotificationContent content,
                                      NotificationResourceManifest manifest) {
        ReplyTarget capabilities = senders.target(target);
        Map<UUID, CommandAttachment> attachments = new LinkedHashMap<>();
        RuntimeException failure = null;
        try {
            for (UUID id : content.resourceIds()) attachments.put(id, resources.materialize(manifest, id));
            return planner.plan(capabilities, content.presentation().resolve(target.platform(), BotMessageScope.GROUP), List.of(), content.replies(attachments::get))
                    .map(plans -> planner.compile(capabilities, plans)).orElse(List.of());
        } catch (RuntimeException exception) {
            failure = exception;
            throw exception;
        } finally {
            RuntimeException cleanup = null;
            for (CommandAttachment attachment : attachments.values()) {
                try { attachment.close(); } catch (RuntimeException exception) {
                    if (cleanup == null) cleanup = exception;
                    else cleanup.addSuppressed(exception);
                }
            }
            if (cleanup != null) {
                if (failure != null) failure.addSuppressed(cleanup);
                else throw cleanup;
            }
        }
    }

    public DeliveryReport dispatch(BotProactiveTarget target, NotificationContent content,
                                   NotificationResourceManifest manifest, Instant expiresAt) {
        List<ResponsePlan> plans = List.of();
        List<PreparedBotMessage> prepared = new ArrayList<>();
        Map<UUID, CommandAttachment> attachments = new LinkedHashMap<>();
        List<Item> items = new ArrayList<>();
        List<String> cleanupFailures = new ArrayList<>();
        RuntimeException failure = null;
        int attempted = -1;
        try {
            for (UUID id : content.resourceIds()) attachments.put(id, resources.materialize(manifest, id));
            ReplyTarget capabilities = senders.target(target);
            plans = planner.plan(capabilities, content.presentation().resolve(target.platform(), BotMessageScope.GROUP), List.of(), content.replies(attachments::get))
                    .map(selected -> planner.compile(capabilities, selected))
                    .orElseThrow(() -> new BotMessageNotSentException("目标能力已变化，通知无法完整发送"));
            for (ResponsePlan plan : plans) {
                validate(target, expiresAt, manifest.submissionId());
                prepared.add(senders.prepare(target, plan.parts()));
            }
            for (int index = 0; index < prepared.size(); index++) {
                validate(target, expiresAt, manifest.submissionId());
                attempted = index;
                BotMessageReceipt receipt = prepared.get(index).send();
                ResponsePlan plan = plans.get(index);
                items.add(new DeliveryReport.Item(plan.logicalMessage(), plan.part(), target.botAccountId(),
                        receipt.platformMessageId(), DeliveryStatus.ACCEPTED, null));
                attempted = -1;
            }
        } catch (RuntimeException exception) {
            failure = exception;
            if (plans.isEmpty()) {
                for (int index = 0; index < content.messages().size(); index++) {
                    items.add(new DeliveryReport.Item(index, 0, target.botAccountId(), null,
                            DeliveryStatus.NOT_SENT, exception.getMessage()));
                }
            } else {
                for (int index = items.size(); index < plans.size(); index++) {
                    DeliveryStatus status = index != attempted || exception instanceof BotMessageNotSentException
                            ? DeliveryStatus.NOT_SENT : exception instanceof BotMessageRejectedException
                            ? DeliveryStatus.REJECTED : DeliveryStatus.UNKNOWN;
                    ResponsePlan plan = plans.get(index);
                    items.add(new DeliveryReport.Item(plan.logicalMessage(), plan.part(), target.botAccountId(), null,
                            status, exception.getMessage()));
                }
            }
        } finally {
            prepared.forEach(message -> cleanup(message, cleanupFailures));
            attachments.values().forEach(attachment -> cleanup(attachment, cleanupFailures));
        }
        DeliveryReport report = DeliveryReport.from(items, plans, cleanupFailures);
        if (failure != null) throw new ReplyDeliveryException(report, failure);
        return report;
    }

    private void validate(BotProactiveTarget target, Instant expiresAt, UUID submissionId) {
        if (cancellations.cancelled(submissionId)) throw new BotMessageNotSentException("通知已取消，未提交部分停止发送");
        deliveryGuard.validate(submissionId, target);
        if (!Instant.now().isBefore(expiresAt)
                || !runtime.isLocalOwner(new BotChannelKey(target.platform(), target.botAccountId()))) {
            throw new BotMessageNotSentException("通知已过期或当前节点不再持有目标连接");
        }
    }

    private void cleanup(AutoCloseable resource, List<String> failures) {
        try {
            resource.close();
        } catch (Exception exception) {
            failures.add(exception.toString());
            log.error("Notification resource cleanup failed; accepted/unknown outcomes remain unchanged", exception);
        }
    }
}
