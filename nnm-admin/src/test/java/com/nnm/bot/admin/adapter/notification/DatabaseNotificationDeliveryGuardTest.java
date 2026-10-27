package com.nnm.bot.admin.adapter.notification;

import org.mockito.MockedStatic;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.admin.pojo.entity.bot.notification.BotNotificationJob;
import com.nnm.bot.admin.constant.BotNotificationConstants.JobStatus;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.admin.repository.bot.notification.BotNotificationJobRepository;
import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.core.message.model.BotProactiveTarget;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseNotificationDeliveryGuardTest {
    @Mock
    private BotNotificationJobRepository jobs;
    @Mock
    private SysUserRepository users;
    @Mock
    private BotChannelInstanceRepository instances;
    @Mock
    private BotGroupRepository groups;
    @Mock
    private BotGroupBotRepository bindings;
    @InjectMocks
    private DatabaseNotificationDeliveryGuard guard;
    private final BotNotificationJob job = new BotNotificationJob();
    private final BotProactiveTarget target = new BotProactiveTarget(BotPlatform.ONEBOT, "bot", "group", List.of());

    @BeforeEach
    void setup() {
        job.setId(UUID.randomUUID()); job.setOwnerId(42); job.setStatus(JobStatus.RUNNING);
        job.setRequest(new BotGroupNotificationDTO(UUID.randomUUID(), List.of(1L), null, null));
        when(jobs.findById(job.getId())).thenReturn(Optional.of(job));
    }

    @Test
    void checksCurrentPermissionBindingGroupAndBotBeforeEverySideEffect() {
        when(users.existsByIdAndEnabledTrue(42L)).thenReturn(true);
        BotChannelInstance bot = new BotChannelInstance(); bot.setId(2L); bot.setEnabled(true);
        when(instances.findByPlatformAndBotAccountId(BotPlatform.ONEBOT, "bot")).thenReturn(Optional.of(bot));
        BotGroup group = new BotGroup(); group.setId(1L); group.setEnabled(true);
        when(groups.findAllById(List.of(1L))).thenReturn(List.of(group));
        BotGroupBot binding = new BotGroupBot(); binding.setBotInstanceId(2L); binding.setPlatform("ONEBOT");
        binding.setScopeKey("group"); binding.setScopeType("GROUP");
        when(bindings.findAllByGroupIdInAndEnabledTrue(List.of(1L))).thenReturn(List.of(binding));
        try (MockedStatic<StpUtil> permission = mockStatic(StpUtil.class)) {
            permission.when(() -> StpUtil.hasPermission(42L, "bot:notification:send")).thenReturn(true);
            assertThatCode(() -> guard.validate(job.getId(), target)).doesNotThrowAnyException();
            when(bindings.findAllByGroupIdInAndEnabledTrue(any())).thenReturn(List.of());
            assertThatThrownBy(() -> guard.validate(job.getId(), target)).hasMessageContaining("绑定");
            permission.when(() -> StpUtil.hasPermission(42L, "bot:notification:send")).thenReturn(false);
            assertThatThrownBy(() -> guard.validate(job.getId(), target)).hasMessageContaining("权限");
        }
    }

    @Test
    void terminalAndCancelRequestedJobsCannotSendFromDelayedQueues() {
        for (JobStatus status : List.of(JobStatus.CANCELLED, JobStatus.CANCEL_REQUESTED, JobStatus.COMPLETED, JobStatus.UNKNOWN, JobStatus.FAILED)) {
            job.setStatus(status);
            assertThatThrownBy(() -> guard.validate(job.getId(), target)).hasMessageContaining("结束");
        }
        verifyNoInteractions(instances, groups, bindings);
    }
}
