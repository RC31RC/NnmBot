package com.nnm.bot.admin.service.bot.notification.impl;

import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationReportDTO;
import com.nnm.bot.common.task.AdminTaskSubmission;
import com.nnm.bot.core.mq.message.notification.NotificationResourceManifest;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.config.NotificationJobProperties;
import com.nnm.bot.admin.pojo.entity.bot.notification.BotNotificationJob;
import com.nnm.bot.admin.constant.BotNotificationConstants.JobStatus;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationJobDTO;
import com.nnm.bot.admin.repository.bot.notification.BotNotificationJobRepository;
import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.admin.service.bot.notification.BotGroupNotificationService;
import com.nnm.bot.admin.service.bot.notification.BotNotificationJobService;
import com.nnm.bot.admin.service.system.OperationAuditService;
import com.nnm.bot.admin.service.bot.notification.BotNotificationStateService;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.task.AdminTaskService;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.channel.notification.store.NotificationCancellationStore;
import com.nnm.bot.core.channel.notification.store.NotificationResourceStorage;
import com.nnm.bot.core.config.BotNotificationProperties;
import com.nnm.bot.core.constant.CommandDeliveryConstants.DeliveryStatus;
import com.nnm.bot.core.task.AdminTaskCoordination;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BotNotificationJobServiceImpl implements BotNotificationJobService {
    private static final List<JobStatus> ACTIVE = List.of(JobStatus.SCHEDULED, JobStatus.SUBMITTING, JobStatus.RUNNING, JobStatus.CANCEL_REQUESTED);
    @Resource
    private BotNotificationJobRepository jobs;
    @Resource
    private BotNotificationStateService store;
    @Resource
    private BotGroupNotificationService notifications;
    @Resource
    private NotificationResourceStorage resources;
    @Resource
    private NotificationCancellationStore cancellations;
    @Resource
    private AdminTaskService tasks;
    @Resource
    private AdminTaskCoordination coordination;
    @Resource
    private NotificationJobProperties properties;
    @Resource
    private BotNotificationProperties notificationProperties;
    @Resource
    private SysUserRepository users;
    @Resource
    private OperationAuditService audit;

    @Override
    public BotNotificationJobDTO submit(long ownerId, BotGroupNotificationDTO request) {
        BusinessAssert.notNull(request.submissionId(), CommonErrorCode.PARAM_INVALID, "缺少通知提交标识");
        UUID id = UUID.nameUUIDFromBytes((ownerId + ":" + request.submissionId()).getBytes(StandardCharsets.UTF_8));
        BotNotificationJob job = coordination.reserve("notification-owner:" + ownerId, () -> {
            BotNotificationJob existing = jobs.findById(id).orElse(null);
            if (existing != null) {
                BusinessAssert.isTrue(existing.getOwnerId() == ownerId && existing.getRequest().equals(request),
                        CommonErrorCode.PARAM_INVALID, "同一提交标识不能用于不同通知内容");
                return existing;
            }
            Instant now = Instant.now();
            Instant due = request.scheduledAt() == null ? now : request.scheduledAt();
            BusinessAssert.isTrue(request.scheduledAt() == null || due.isAfter(now), CommonErrorCode.PARAM_INVALID,
                    "定时发送时间必须晚于当前时间");
            BusinessAssert.isTrue(!due.isAfter(now.plus(notificationProperties.maxScheduleDelay())),
                    CommonErrorCode.PARAM_INVALID, "定时发送时间超出允许范围");
            BusinessAssert.isTrue(jobs.countByOwnerIdAndStatusIn(ownerId, ACTIVE) < properties.getMaxPendingPerUser(),
                    CommonErrorCode.PARAM_INVALID, "待发送通知任务数量已达上限");
            notifications.preview(ownerId, request);
            NotificationResourceManifest manifest = resources.retain(ownerId, id, request.content().resourceIds(), due.plus(notificationProperties.resourceRetention()));
            BotNotificationJob created = new BotNotificationJob();
            created.setId(id);
            created.setOwnerId(ownerId);
            created.setRequest(request);
            created.setResources(manifest);
            created.setScheduledAt(due);
            created.setStatus(JobStatus.SCHEDULED);
            created.setCreatedAt(now);
            created.setUpdatedAt(now);
            try {
                return jobs.saveAndFlush(created);
            } catch (RuntimeException failure) {
                // 没有持久化任务的资源仍由有界保留期回收；不冒险删除提交结果未知的资源。
                log.error("Notification job persistence failed id={} owner={}", id, ownerId, failure);
                throw failure;
            }
        });
        auditSafely(ownerId, "BOT_NOTIFICATION_SUBMIT", job.getId(),
                "提交通知任务 status=" + job.getStatus() + " scheduledAt=" + job.getScheduledAt()
                        + " groups=" + request.groupIds() + " " + contentSummary(request)
                        + " composition=" + request.content().composition()
                        + " interaction=" + request.content().interaction());
        if (request.scheduledAt() == null && job.getStatus() == JobStatus.SCHEDULED) dispatch(job.getId());
        return item(jobs.findById(id).orElseThrow());
    }

    @Override
    public List<BotNotificationJobDTO> list(long ownerId) {
        return jobs.findTop100ByOwnerIdOrderByCreatedAtDesc(ownerId).stream().map(this::item).toList();
    }

    @Override
    public BotNotificationJobDTO cancel(long ownerId, UUID id) {
        BotNotificationJob result = store.update(id, job -> {
            BusinessAssert.isTrue(job.getOwnerId() == ownerId, CommonErrorCode.PARAM_INVALID, "无权操作该通知任务");
            if (job.getStatus().terminal()) return;
            // 先发布取消，再提交 DB；任一节点读取失败会阻止新的平台发送。
            cancellations.cancel(job.getId(), job.getResources().expiresAt());
            job.setStatus(job.getStatus() == JobStatus.SCHEDULED ? JobStatus.CANCELLED : JobStatus.CANCEL_REQUESTED);
        });
        auditSafely(ownerId, "BOT_NOTIFICATION_CANCEL", result.getId(),
                "取消通知任务 status=" + result.getStatus() + " groups=" + result.getRequest().groupIds());
        return item(result);
    }

    @Override
    public void dispatchDue() {
        Instant now = Instant.now();
        for (BotNotificationJob job : jobs.findTop50ByStatusAndScheduledAtLessThanEqualOrderByScheduledAtAsc(JobStatus.SCHEDULED, now)) {
            try {
                dispatch(job.getId());
            } catch (RuntimeException failure) {
                log.error("Notification due dispatch failed id={}", job.getId(), failure);
            }
        }
        for (BotNotificationJob job : jobs.findTop50ByStatusInAndUpdatedAtBefore(
                List.of(JobStatus.SUBMITTING, JobStatus.RUNNING, JobStatus.CANCEL_REQUESTED), now.minus(properties.getStaleTimeout()))) {
            try {
                store.update(job.getId(), current -> {
                    if (current.getStatus().terminal() || !current.getUpdatedAt().isBefore(now.minus(properties.getStaleTimeout()))) return;
                    current.setStatus(JobStatus.UNKNOWN);
                    current.setError("未取得任务最终结果，不自动重新提交；请核实群消息及逐目标回执");
                });
            } catch (RuntimeException failure) {
                log.error("Notification stale reconciliation failed id={}", job.getId(), failure);
            }
        }
        for (BotNotificationJob job : jobs.findTop50ByStatusInAndUpdatedAtBefore(
                List.of(JobStatus.COMPLETED, JobStatus.CANCELLED, JobStatus.FAILED, JobStatus.UNKNOWN), now.minus(properties.getHistoryRetention()))) {
            try {
                if (job.getResources().expiresAt().isBefore(now)) jobs.deleteById(job.getId());
            } catch (RuntimeException failure) {
                log.error("Notification history cleanup failed id={}", job.getId(), failure);
            }
        }
    }

    private void dispatch(UUID id) {
        // DB 行锁原子认领；提交前落 SUBMITTING，进程崩溃后只标未知，不重放外部副作用。
        boolean[] claimed = {false};
        BotNotificationJob job = store.update(id, current -> {
            if (current.getStatus() != JobStatus.SCHEDULED || current.getScheduledAt().isAfter(Instant.now())) return;
            if (current.getScheduledAt().plus(properties.getDispatchGrace()).isBefore(Instant.now())) {
                current.setStatus(JobStatus.FAILED);
                current.setError("已超过定时发送允许的补发窗口，未自动补发");
                return;
            }
            if (!users.existsByIdAndEnabledTrue(current.getOwnerId())
                    || !StpUtil.hasPermission(current.getOwnerId(), "bot:notification:send")) {
                current.setStatus(JobStatus.FAILED);
                current.setError("发起人已停用或发送权限已撤销");
                return;
            }
            current.setStatus(JobStatus.SUBMITTING);
            claimed[0] = true;
        });
        if (!claimed[0]) return;
        try {
            AdminTaskSubmission task = tasks.submit("BOT_GROUP_NOTIFICATION:" + id, "bot:notification:send", reporter -> {
                try {
                    BotGroupNotificationReportDTO report = notifications.send(job.getRequest(), job.getResources(), reporter);
                    store.update(id, current -> {
                        current.setReport(report);
                        boolean unknown = report.deliveries().stream().anyMatch(delivery -> "UNKNOWN".equals(delivery.status())
                                || delivery.report() != null && delivery.report().items().stream().anyMatch(item -> item.status() == DeliveryStatus.UNKNOWN));
                        current.setStatus(unknown ? JobStatus.UNKNOWN : current.getStatus() == JobStatus.CANCEL_REQUESTED ? JobStatus.CANCELLED : JobStatus.COMPLETED);
                        if (unknown) current.setError("部分平台发送结果未知，请核对逐目标回执；不会自动重发");
                    });
                    try {
                        jobs.findById(id).ifPresent(current -> auditSafely(current.getOwnerId(),
                                "BOT_NOTIFICATION_RESULT", id,
                                "status=" + current.getStatus() + " groups=" + current.getRequest().groupIds()
                                        + " deliveries=" + (report == null ? 0 : report.deliveries().size())));
                    } catch (RuntimeException auditLookupFailure) {
                        log.warn("Notification result audit lookup failed id={}", id, auditLookupFailure);
                    }
                    return report;
                } catch (Exception failure) {
                    recordUnknown(id, "执行未取得完整结果，不自动重发", failure);
                    throw failure;
                }
            });
            store.update(id, current -> {
                current.setTaskId(task.taskId());
                if (current.getStatus() == JobStatus.SUBMITTING) current.setStatus(JobStatus.RUNNING);
            });
        } catch (RuntimeException failure) {
            recordUnknown(id, "提交或记录任务结果失败，不自动重新提交", failure);
            throw failure;
        }
    }

    private void recordUnknown(UUID id, String reason, Exception failure) {
        try {
            store.update(id, current -> {
                if (!current.getStatus().terminal()) {
                    current.setStatus(JobStatus.UNKNOWN);
                    current.setError(reason);
                }
            });
        } catch (RuntimeException persistenceFailure) {
            failure.addSuppressed(persistenceFailure);
        }
    }

    private BotNotificationJobDTO item(BotNotificationJob job) {
        return new BotNotificationJobDTO(job.getId(), job.getStatus(), job.getScheduledAt(), job.getTaskId(),
                job.getRequest().groupIds(), job.getReport(), job.getError());
    }

    private String contentSummary(BotGroupNotificationDTO request) {
        int blocks = request.content().messages().stream().mapToInt(message -> message.blocks().size()).sum();
        int actions = request.content().messages().stream().mapToInt(message -> message.actions().size()).sum();
        return "messages=" + request.content().messages().size() + " blocks=" + blocks + " actions=" + actions;
    }

    private void auditSafely(long ownerId, String action, UUID id, String detail) {
        try {
            audit.recordForOperator(ownerId, action, "BOT_NOTIFICATION_JOB", id, detail);
        } catch (RuntimeException failure) {
            log.warn("Notification audit record failed id={} action={}", id, action, failure);
        }
    }
}
