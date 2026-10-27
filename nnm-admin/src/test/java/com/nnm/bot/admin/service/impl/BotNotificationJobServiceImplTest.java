package com.nnm.bot.admin.service.impl;

import com.nnm.bot.admin.service.bot.notification.impl.BotNotificationJobServiceImpl;
import com.nnm.bot.core.constant.NotificationConstants.BlockType;

import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationJobDTO;
import com.nnm.bot.common.task.AdminTaskService.AdminTaskWork;
import org.mockito.MockedStatic;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.config.NotificationJobProperties;
import com.nnm.bot.admin.pojo.entity.bot.notification.BotNotificationJob;
import com.nnm.bot.admin.constant.BotNotificationConstants.JobStatus;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationReportDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.repository.bot.notification.BotNotificationJobRepository;
import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.admin.service.bot.notification.BotGroupNotificationService;
import com.nnm.bot.admin.service.bot.notification.BotNotificationStateService;
import com.nnm.bot.common.task.AdminTaskService;
import com.nnm.bot.common.task.AdminTaskSubmission;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.core.mq.message.notification.NotificationContent;
import com.nnm.bot.core.mq.message.notification.NotificationResourceManifest;
import com.nnm.bot.core.channel.notification.store.NotificationCancellationStore;
import com.nnm.bot.core.channel.notification.store.NotificationResourceStorage;
import com.nnm.bot.core.config.BotNotificationProperties;
import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;
import com.nnm.bot.core.task.AdminTaskCoordination;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BotNotificationJobServiceImplTest {
    @Mock
    private BotNotificationJobRepository jobs;
    @Mock
    private BotNotificationStateService store;
    @Mock
    private BotGroupNotificationService notifications;
    @Mock
    private NotificationResourceStorage resources;
    @Mock
    private NotificationCancellationStore cancellations;
    @Mock
    private AdminTaskService tasks;
    @Mock
    private AdminTaskCoordination coordination;
    @Mock
    private SysUserRepository users;
    @InjectMocks
    private BotNotificationJobServiceImpl service;
    private final Map<UUID, BotNotificationJob> rows = new HashMap<>();
    private final NotificationJobProperties properties = new NotificationJobProperties();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "properties", properties);
        ReflectionTestUtils.setField(service, "notificationProperties", new BotNotificationProperties(Duration.ofSeconds(1),
                Duration.ofMinutes(5), Duration.ofMillis(100), Duration.ofDays(1), Duration.ofDays(30), Duration.ofHours(1)));
        lenient().when(coordination.reserve(anyString(), any())).thenAnswer(call -> ((Supplier<?>) call.getArgument(1)).get());
        lenient().when(jobs.findById(any())).thenAnswer(call -> Optional.ofNullable(rows.get(call.getArgument(0))));
        lenient().when(jobs.saveAndFlush(any())).thenAnswer(call -> {
            BotNotificationJob job = call.getArgument(0); rows.put(job.getId(), job); return job;
        });
        lenient().when(store.update(any(), any())).thenAnswer(call -> {
            BotNotificationJob job = rows.get(call.getArgument(0));
            Consumer<BotNotificationJob> mutation = call.getArgument(1); mutation.accept(job); return job;
        });
        lenient().when(resources.retain(anyLong(), any(), any(), any())).thenAnswer(call ->
                new NotificationResourceManifest(call.getArgument(1), Map.of(), call.getArgument(3)));
    }

    @Test
    void futureSubmissionPersistsSnapshotAndRetryDoesNotRefreezeOrEnqueue() {
        BotGroupNotificationDTO request = request(UUID.randomUUID(), Instant.now().plusSeconds(120));
        BotNotificationJobDTO first = service.submit(42, request);
        BotNotificationJobDTO again = service.submit(42, request);
        assertThat(first.id()).isEqualTo(again.id());
        assertThat(first.status()).isEqualTo(JobStatus.SCHEDULED);
        assertThat(first.taskId()).isNull();
        BotNotificationJob frozen = rows.get(first.id());
        assertThat(frozen.getRequest()).isEqualTo(request);
        assertThat(frozen.getResources().expiresAt()).isEqualTo(request.scheduledAt().plus(Duration.ofDays(1)));
        verify(resources, times(1)).retain(eq(42L), any(), any(), any());
        verifyNoInteractions(tasks);
        assertThatThrownBy(() -> service.submit(42, request(request.submissionId(), request.scheduledAt().plusSeconds(1))))
                .hasMessageContaining("同一提交标识");
    }

    @Test
    void futureTimesAndPendingQuotaAreBounded() {
        assertThatThrownBy(() -> service.submit(42, request(UUID.randomUUID(), Instant.now().minusSeconds(1))))
                .hasMessageContaining("晚于");
        assertThatThrownBy(() -> service.submit(42, request(UUID.randomUUID(), Instant.now().plus(Duration.ofDays(31)))))
                .hasMessageContaining("超出");
        when(jobs.countByOwnerIdAndStatusIn(eq(42L), any())).thenReturn(20L);
        assertThatThrownBy(() -> service.submit(42, request(UUID.randomUUID(), Instant.now().plusSeconds(120))))
                .hasMessageContaining("上限");
        verifyNoInteractions(resources, tasks);
    }

    @Test
    void scheduledCancelPublishesIdentityBeforeStatusAndRejectsAnotherOwner() {
        BotNotificationJobDTO item = service.submit(42, request(UUID.randomUUID(), Instant.now().plusSeconds(120)));
        assertThatThrownBy(() -> service.cancel(99, item.id())).hasMessageContaining("无权");
        assertThat(service.cancel(42, item.id()).status()).isEqualTo(JobStatus.CANCELLED);
        verify(cancellations).cancel(eq(item.id()), any());
        service.cancel(42, item.id());
        verify(cancellations, times(1)).cancel(any(), any());
        verifyNoInteractions(tasks);
    }

    @Test
    void failedCancelPublicationLeavesTaskScheduled() {
        BotNotificationJobDTO item = service.submit(42, request(UUID.randomUUID(), Instant.now().plusSeconds(120)));
        doThrow(new IllegalStateException("redis unavailable")).when(cancellations).cancel(any(), any());
        assertThatThrownBy(() -> service.cancel(42, item.id())).hasMessage("redis unavailable");
        assertThat(rows.get(item.id()).getStatus()).isEqualTo(JobStatus.SCHEDULED);
    }

    @Test
    void dueTaskIsClaimedOnceAndFastWorkerCompletionIsNotOverwrittenByRunning() throws Exception {
        BotNotificationJob job = due();
        when(users.existsByIdAndEnabledTrue(42L)).thenReturn(true);
        BotGroupNotificationReportDTO report = new BotGroupNotificationReportDTO(1, 0, 0, List.of());
        when(notifications.send(any(), any(), any())).thenReturn(report);
        when(tasks.submit(anyString(), anyString(), any())).thenAnswer(call -> {
            AdminTaskWork work = call.getArgument(2); work.execute(TaskProgressReporter.noop());
            return new AdminTaskSubmission(UUID.randomUUID(), "notification");
        });
        try (MockedStatic<StpUtil> permissions = mockStatic(StpUtil.class)) {
            permissions.when(() -> StpUtil.hasPermission(42L, "bot:notification:send")).thenReturn(true);
            service.dispatchDue();
            service.dispatchDue();
        }
        assertThat(job.getStatus()).isEqualTo(JobStatus.COMPLETED);
        assertThat(job.getReport()).isSameAs(report);
        assertThat(job.getTaskId()).isNotNull();
        verify(tasks, times(1)).submit(anyString(), anyString(), any());
    }

    @Test
    void revokedPermissionAndMissedGraceWindowPreventEnqueue() {
        BotNotificationJob job = due();
        when(users.existsByIdAndEnabledTrue(42L)).thenReturn(true);
        try (MockedStatic<StpUtil> permissions = mockStatic(StpUtil.class)) { service.dispatchDue(); }
        assertThat(job.getStatus()).isEqualTo(JobStatus.FAILED);
        assertThat(job.getError()).contains("权限");
        job.setStatus(JobStatus.SCHEDULED);
        job.setScheduledAt(Instant.now().minus(Duration.ofHours(1)));
        service.dispatchDue();
        assertThat(job.getStatus()).isEqualTo(JobStatus.FAILED);
        assertThat(job.getError()).contains("补发窗口");
        verifyNoInteractions(tasks);
    }

    @Test
    void uncertainSubmissionIsNeverAutomaticallyReplayed() {
        BotNotificationJob job = due();
        when(users.existsByIdAndEnabledTrue(42L)).thenReturn(true);
        when(tasks.submit(anyString(), anyString(), any())).thenThrow(new IllegalStateException("submission uncertain"));
        try (MockedStatic<StpUtil> permissions = mockStatic(StpUtil.class)) {
            permissions.when(() -> StpUtil.hasPermission(42L, "bot:notification:send")).thenReturn(true);
            service.dispatchDue(); service.dispatchDue();
        }
        assertThat(job.getStatus()).isEqualTo(JobStatus.UNKNOWN);
        verify(tasks, times(1)).submit(anyString(), anyString(), any());
    }

    @Test
    void cancellationDuringRunningPreservesAcceptedAndUnknownReport() throws Exception {
        BotNotificationJob job = due();
        when(users.existsByIdAndEnabledTrue(42L)).thenReturn(true);
        BotGroupNotificationReportDTO report = new BotGroupNotificationReportDTO(0, 1, 0, List.of(new BotGroupNotificationReportDTO.DeliveryDTO(1, "group", "ONEBOT", "g", "UNKNOWN", "timeout", null)));
        when(notifications.send(any(), any(), any())).thenAnswer(call -> {
            service.cancel(42, job.getId()); return report;
        });
        when(tasks.submit(anyString(), anyString(), any())).thenAnswer(call -> {
            AdminTaskWork work = call.getArgument(2); work.execute(TaskProgressReporter.noop());
            return new AdminTaskSubmission(UUID.randomUUID(), "notification");
        });
        try (MockedStatic<StpUtil> permissions = mockStatic(StpUtil.class)) {
            permissions.when(() -> StpUtil.hasPermission(42L, "bot:notification:send")).thenReturn(true);
            service.dispatchDue();
        }
        assertThat(job.getStatus()).isEqualTo(JobStatus.UNKNOWN);
        assertThat(job.getReport()).isSameAs(report);
        verify(cancellations).cancel(eq(job.getId()), any());
    }

    @Test
    void staleWorkerIsMarkedUnknownWithoutReplayAndResourcesSurviveUntilExpiry() {
        BotNotificationJob job = due(); job.setStatus(JobStatus.RUNNING); job.setUpdatedAt(Instant.now().minus(Duration.ofHours(1)));
        when(jobs.findTop50ByStatusInAndUpdatedAtBefore(any(), any())).thenReturn(List.of(job), List.of());
        service.dispatchDue();
        assertThat(job.getStatus()).isEqualTo(JobStatus.UNKNOWN);
        verifyNoInteractions(tasks);
        verify(jobs, never()).deleteById(any());
    }

    private BotNotificationJob due() {
        BotNotificationJobDTO item = service.submit(42, request(UUID.randomUUID(), Instant.now().plusSeconds(120)));
        BotNotificationJob job = rows.get(item.id()); job.setScheduledAt(Instant.now().minusSeconds(1));
        when(jobs.findTop50ByStatusAndScheduledAtLessThanEqualOrderByScheduledAtAsc(eq(JobStatus.SCHEDULED), any())).thenReturn(List.of(job));
        return job;
    }

    private BotGroupNotificationDTO request(UUID id, Instant due) {
        return new BotGroupNotificationDTO(id, List.of(1L), new NotificationContent(List.of(new NotificationContent.Message(
                List.of(new NotificationContent.Block(BlockType.TEXT, "通知", null, null)), List.of(), null)),
                CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT), due);
    }
}
