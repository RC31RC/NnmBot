package com.nnm.bot.admin.service.bot.notification.impl;

import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.plugin.model.ReplyBlock.Image;
import com.nnm.bot.core.plugin.model.ReplyBlock.Markdown;
import com.nnm.bot.core.plugin.model.ReplyBlock.Text;
import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationReportDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationReportDTO.DeliveryDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationPreviewDTO;
import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.admin.service.bot.notification.BotGroupNotificationService;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.mq.message.notification.GroupNotificationMessage;
import com.nnm.bot.core.mq.message.notification.GroupNotificationRequest;
import com.nnm.bot.core.mq.message.notification.NotificationResourceManifest;
import com.nnm.bot.core.channel.notification.store.GroupNotificationResultStore;
import com.nnm.bot.core.channel.notification.store.NotificationCancellationStore;
import com.nnm.bot.core.channel.notification.store.NotificationResourceStorage;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.config.BotNotificationProperties;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.constant.BotChannelStatus;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.core.message.model.BotProactiveTarget;
import com.nnm.bot.core.plugin.model.DeliveryReport;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import com.nnm.bot.core.plugin.response.ProactiveReplyDispatcher;
import io.github.rdt.exception.RdtQueueException;
import io.github.rdt.queue.RdtMessageSendManager;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BotGroupNotificationServiceImpl implements BotGroupNotificationService {
    @Resource
    private BotGroupRepository groups;
    @Resource
    private BotGroupBotRepository bindings;
    @Resource
    private BotChannelInstanceRepository instances;
    @Resource
    private RdtMessageSendManager messages;
    @Resource
    private GroupNotificationResultStore results;
    @Resource
    private BotNotificationProperties properties;
    @Resource
    private ReplyProperties replyLimits;
    @Resource
    private ClusterBotRuntime runtime;
    @Resource
    private ProactiveReplyDispatcher dispatcher;
    @Resource
    private NotificationResourceStorage resources;
    @Resource
    private NotificationCancellationStore cancellations;

    @Override
    public List<BotNotificationPreviewDTO> preview(long ownerId, BotGroupNotificationDTO request) {
        request.content().validate(replyLimits);
        request.content().resourceIds().forEach(id -> resources.owned(ownerId, id));
        return targets(request, resources.drafts(ownerId, request.content().resourceIds())).stream().map(target -> new BotNotificationPreviewDTO(target.group().getId(),
                target.group().getName(), target.platform(), target.target() == null ? "" : target.target().botAccountId(),
                target.conversationId(), !target.plans().isEmpty(), target.reason(), target.plans().stream()
                .map(plan -> new BotNotificationPreviewDTO.MessageDTO(plan.logicalMessage(), plan.part(), plan.blocks().stream()
                        .map(block -> switch (block) {
                            case Text ignored -> "TEXT";
                            case Image ignored -> "IMAGE";
                            case Markdown ignored -> "MARKDOWN";
                            default -> throw new IllegalStateException("Unsupported notification preview block");
                        }).toList(), plan.text(), plan.actions().stream()
                        .map(action -> new BotNotificationPreviewDTO.ActionDTO(action.action().label(), action.action().value(),
                                action.presentation())).toList())).toList())).toList();
    }

    @Override
    public BotGroupNotificationReportDTO send(BotGroupNotificationDTO request, NotificationResourceManifest manifest,
                                     TaskProgressReporter reporter) throws InterruptedException {
        request.content().validate(replyLimits);
        List<DeliveryDTO> deliveries = new ArrayList<>();
        Map<UUID, DeliveryDTO> waiting = new LinkedHashMap<>();
        Instant expires = Instant.now().plus(properties.responseTimeout());
        for (Target selected : targets(request, manifest)) {
            DeliveryDTO delivery = new DeliveryDTO(selected.group().getId(), selected.group().getName(), selected.platform(),
                    selected.conversationId(), "PENDING", "", null);
            if (cancellations.cancelled(manifest.submissionId())) {
                deliveries.add(outcome(delivery, "CANCELLED", "通知已取消，未入队", null));
                continue;
            }
            if (selected.plans().isEmpty()) {
                deliveries.add(outcome(delivery, "SKIPPED", selected.reason(), null));
                continue;
            }
            UUID id = UUID.nameUUIDFromBytes((manifest.submissionId() + ":" + selected.target().identity())
                    .getBytes(StandardCharsets.UTF_8));
            try {
                messages.send(new GroupNotificationMessage(new GroupNotificationRequest(id, selected.target(),
                        request.content(), manifest, expires)));
                waiting.put(id, delivery);
            } catch (RdtQueueException rejected) {
                String reason = switch (rejected.code()) {
                    case QUEUE_CAPACITY -> "通知队列已满";
                    case MESSAGE_TOO_LARGE -> "通知内容超过队列载荷限制";
                    default -> "通知入队失败";
                };
                deliveries.add(outcome(delivery, "FAILED", reason, null));
                log.warn("Notification enqueue rejected submission={} target={} code={}", request.submissionId(), id, rejected.code());
            }
        }
        int total = deliveries.size() + waiting.size();
        long deadline = System.nanoTime() + properties.responseTimeout().toNanos();
        while (!waiting.isEmpty() && System.nanoTime() < deadline) {
            results.findAll(waiting.keySet()).forEach((id, result) -> deliveries.add(outcome(waiting.remove(id),
                    result.status().name(), result.message(), result.report())));
            reporter.report("SENDING", "等待各目标逐条发送结果", deliveries.size(), total);
            if (!waiting.isEmpty()) Thread.sleep(properties.pollInterval());
        }
        waiting.values().forEach(target -> deliveries.add(outcome(target, "UNKNOWN", "未取得最终结果，请核实后决定是否重试", null)));
        int succeeded = (int) deliveries.stream().filter(item -> "SUCCEEDED".equals(item.status())).count();
        int skipped = (int) deliveries.stream().filter(item -> ("SKIPPED".equals(item.status()) || "CANCELLED".equals(item.status()))).count();
        reporter.report("COMPLETED", "通知任务结束", total, total);
        return new BotGroupNotificationReportDTO(succeeded, deliveries.size() - succeeded - skipped, skipped, List.copyOf(deliveries));
    }

    private List<Target> targets(BotGroupNotificationDTO request, NotificationResourceManifest manifest) {
        List<Long> ids = request.groupIds().stream().distinct().toList();
        BusinessAssert.isTrue(!ids.isEmpty() && ids.size() <= 200, CommonErrorCode.PARAM_INVALID, "请选择 1–200 个群组");
        Map<Long, BotGroup> selected = groups.findAllById(ids).stream()
                .collect(Collectors.toMap(BotGroup::getId, Function.identity()));
        BusinessAssert.isTrue(selected.size() == ids.size(), CommonErrorCode.PARAM_INVALID, "部分群组已不存在");
        List<BotGroupBot> candidates = bindings.findAllByGroupIdInAndEnabledTrue(ids).stream()
                .filter(binding -> "GROUP".equals(binding.getScopeType()))
                .sorted(Comparator.comparingInt(BotGroupBot::getReplyPriority).thenComparing(BotGroupBot::getId)).toList();
        Map<Long, BotChannelInstance> bots = instances.findAllById(candidates.stream()
                .map(BotGroupBot::getBotInstanceId).distinct().toList()).stream()
                .collect(Collectors.toMap(BotChannelInstance::getId, Function.identity()));
        Map<BotChannelKey,BotChannelSnapshot> snapshots = runtime.snapshots(bots.values().stream()
                .map(bot -> new BotChannelKey(bot.getPlatform(), bot.getBotAccountId())).distinct().toList());
        Map<String, Target> destinations = new LinkedHashMap<>();
        HashSet<Long> represented = new HashSet<Long>();
        for (BotGroupBot binding : candidates) {
            BotGroup group = selected.get(binding.getGroupId());
            BotChannelInstance bot = bots.get(binding.getBotInstanceId());
            if (group == null || !group.isEnabled() || bot == null || !bot.isEnabled()) continue;
            BotProactiveTarget target = new BotProactiveTarget(bot.getPlatform(), bot.getBotAccountId(), binding.getScopeKey(), List.of());
            represented.add(group.getId());
            Target existing = destinations.get(target.identity());
            if (existing != null && !existing.plans().isEmpty()) continue;
            BotChannelSnapshot snapshot = snapshots.get(new BotChannelKey(bot.getPlatform(), bot.getBotAccountId()));
            List<ResponsePlan> plans = snapshot != null && snapshot.status() == BotChannelStatus.CONNECTED
                    ? dispatcher.preview(target, request.content(), manifest) : List.of();
            destinations.put(target.identity(), new Target(group, bot.getPlatform().name(), binding.getScopeKey(), target,
                    plans, plans.isEmpty() ? "没有在线且满足完整展示方案的机器人" : ""));
        }
        List<Target> result = new ArrayList<>(destinations.values());
        for (Long id : ids) {
            BotGroup group = selected.get(id);
            if (!represented.contains(id)) result.add(new Target(group, "", "", null, List.of(),
                    group.isEnabled() ? "没有可用群组绑定" : "群组已停用"));
        }
        return List.copyOf(result);
    }

    private DeliveryDTO outcome(DeliveryDTO target, String status, String message, DeliveryReport report) {
        return new DeliveryDTO(target.groupId(), target.groupName(), target.platform(), target.conversationId(), status, message, report);
    }

    private record Target(BotGroup group, String platform, String conversationId, BotProactiveTarget target,
                            List<ResponsePlan> plans, String reason) { }
}
