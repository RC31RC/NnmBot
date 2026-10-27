package com.nnm.bot.admin.service.impl;

import com.nnm.bot.admin.service.bot.notification.impl.BotGroupNotificationServiceImpl;
import com.nnm.bot.core.constant.NotificationConstants.GroupResultStatus;

import com.nnm.bot.core.constant.NotificationConstants.BlockType;

import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationReportDTO;
import com.nnm.bot.core.mq.message.notification.GroupNotificationRequest;
import jakarta.validation.ValidatorFactory;
import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.core.mq.message.notification.GroupNotificationMessage;
import com.nnm.bot.core.channel.notification.GroupNotificationResult;
import com.nnm.bot.core.channel.notification.store.GroupNotificationResultStore;
import com.nnm.bot.core.channel.notification.store.NotificationCancellationStore;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.config.BotNotificationProperties;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotChannelStatus;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import io.github.rdt.queue.RdtMessageSendManager;
import io.github.rdt.exception.RdtQueueException;
import io.github.rdt.exception.RdtErrorCode;
import jakarta.validation.Validation;
import com.nnm.bot.core.plugin.response.ProactiveReplyDispatcher;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;
import com.nnm.bot.core.mq.message.notification.NotificationContent;
import com.nnm.bot.core.mq.message.notification.NotificationResourceManifest;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BotGroupNotificationServiceImplTest {
    @Mock
    private BotGroupRepository groups;
    @Mock
    private BotGroupBotRepository bindings;
    @Mock
    private BotChannelInstanceRepository instances;
    @Mock
    private RdtMessageSendManager messages;
    @Mock
    private GroupNotificationResultStore results;
    @Mock
    private ClusterBotRuntime runtime;
    @Mock
    private TaskProgressReporter reporter;
    @Mock
    private ProactiveReplyDispatcher dispatcher;
    @Mock
    private NotificationCancellationStore cancellations;
    @InjectMocks
    private BotGroupNotificationServiceImpl service;

    @BeforeEach
    void configure() {
        lenient().when(dispatcher.preview(any(), any(), any())).thenReturn(List.of(new ResponsePlan(0, 0,
                List.of(new ReplyBlock.Text("notice")), List.of(), null, CompositionPolicy.PREFER_SINGLE, ViewFormat.TEXT, false)));
        ReflectionTestUtils.setField(service, "properties", new BotNotificationProperties(
                Duration.ofSeconds(1), Duration.ofMinutes(5), Duration.ofMillis(100), Duration.ofHours(24), Duration.ofDays(30), Duration.ofHours(1)));
        ReflectionTestUtils.setField(service, "replyLimits", new ReplyProperties());
    }

    @Test
    void routesToOnlinePreferredRobotAndDeduplicatesPlatformGroups() throws Exception {
        when(groups.findAllById(any())).thenReturn(List.of(group(1), group(2)));
        when(bindings.findAllByGroupIdInAndEnabledTrue(any())).thenReturn(List.of(
                binding(1, 1, 10, 1), binding(2, 1, 11, 2), binding(3, 2, 11, 3)));
        when(instances.findAllById(any())).thenReturn(List.of(bot(10), bot(11)));
        BotChannelKey key = new BotChannelKey(BotPlatform.ONEBOT, "11");
        when(runtime.snapshots(any())).thenReturn(Map.of(key, new BotChannelSnapshot("node-11", key,
                Set.of(BotChannelCapability.TEXT_MESSAGE), BotChannelStatus.CONNECTED, Instant.now(), null)));
        when(results.findAll(any())).thenAnswer(invocation -> {
            Collection<UUID> ids = invocation.getArgument(0);
            LinkedHashMap<UUID,GroupNotificationResult> found = new LinkedHashMap<UUID, GroupNotificationResult>();
            ids.forEach(id -> found.put(id, new GroupNotificationResult(
                    GroupResultStatus.SUCCEEDED, "sent", null)));
            return found;
        });
        BotGroupNotificationReportDTO report = service.send(request(List.of(1L, 2L), "hello"), manifest(), reporter);
        assertThat(report.succeeded()).isEqualTo(1);
        assertThat(report.skipped()).isEqualTo(0);
        verify(messages).send(argThat(descriptor -> {
            GroupNotificationRequest request = ((GroupNotificationMessage) descriptor).payload();
            return request.target().botAccountId().equals("11") && request.content().messages().getFirst().blocks().getFirst().text().equals("hello");
        }));
    }

    @Test
    void capacityRejectionIsReportedWithoutWaitingForAResponse() throws Exception {
        when(groups.findAllById(any())).thenReturn(List.of(group(1)));
        when(bindings.findAllByGroupIdInAndEnabledTrue(any())).thenReturn(List.of(binding(1, 1, 10, 1)));
        when(instances.findAllById(any())).thenReturn(List.of(bot(10)));
        BotChannelKey key = new BotChannelKey(BotPlatform.ONEBOT, "10");
        when(runtime.snapshots(any())).thenReturn(Map.of(key, new BotChannelSnapshot("node-10", key,
                Set.of(BotChannelCapability.TEXT_MESSAGE), BotChannelStatus.CONNECTED, Instant.now(), null)));
        when(messages.send(any())).thenThrow(new RdtQueueException(RdtErrorCode.QUEUE_CAPACITY, "full"));
        BotGroupNotificationReportDTO report = service.send(request(List.of(1L), "notice"), manifest(), reporter);
        assertThat(report.failed()).isEqualTo(1);
        assertThat(report.deliveries()).singleElement().satisfies(delivery ->
                assertThat(delivery.message()).contains("队列已满"));
        verifyNoInteractions(results);
    }

    @Test
    void unexpectedQueueFailurePropagatesInsteadOfBeingReportedAsDefiniteRejection() {
        when(groups.findAllById(any())).thenReturn(List.of(group(1)));
        when(bindings.findAllByGroupIdInAndEnabledTrue(any())).thenReturn(List.of(binding(1, 1, 10, 1)));
        when(instances.findAllById(any())).thenReturn(List.of(bot(10)));
        BotChannelKey key = new BotChannelKey(BotPlatform.ONEBOT, "10");
        when(runtime.snapshots(any())).thenReturn(Map.of(key, new BotChannelSnapshot("node-10", key,
                Set.of(BotChannelCapability.TEXT_MESSAGE), BotChannelStatus.CONNECTED, Instant.now(), null)));
        IllegalStateException failure = new IllegalStateException("redis connection lost");
        when(messages.send(any())).thenThrow(failure);

        assertThatThrownBy(() -> service.send(request(List.of(1L), "notice"), manifest(), reporter))
                .isSameAs(failure);
    }

    @Test
    void offlineGroupsAreSkippedWithoutSubmittingMessages() throws Exception {
        when(groups.findAllById(any())).thenReturn(List.of(group(1)));
        when(bindings.findAllByGroupIdInAndEnabledTrue(any())).thenReturn(List.of(binding(1, 1, 10, 1)));
        when(instances.findAllById(any())).thenReturn(List.of(bot(10)));
        when(runtime.snapshots(any())).thenReturn(Map.of());
        BotGroupNotificationReportDTO report = service.send(request(List.of(1L), "notice"), manifest(), reporter);
        assertThat(report.skipped()).isEqualTo(1);
        verifyNoInteractions(messages, results);
    }

    @Test
    void rejectsMissingGroupsBeforeSending() {
        when(groups.findAllById(any())).thenReturn(List.of());
        assertThatThrownBy(() -> service.send(request(List.of(1L), "notice"), manifest(), reporter))
                .isInstanceOf(BusinessException.class);
        verifyNoInteractions(messages);
    }

    @Test
    void requestValidationRejectsNullIdsAndBlankText() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            BotGroupNotificationDTO request = new BotGroupNotificationDTO(null, List.of(), null, null);
            assertThat(factory.getValidator().validate(request)).hasSize(3);
        }
    }

    private BotGroupNotificationDTO request(List<Long> ids, String text) {
        return new BotGroupNotificationDTO(UUID.randomUUID(), ids,
                new NotificationContent(List.of(new NotificationContent.Message(List.of(new NotificationContent.Block(
                        BlockType.TEXT, text, null, null)), List.of(), null)),
                        CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT), null);
    }

    private NotificationResourceManifest manifest() {
        return new NotificationResourceManifest(UUID.randomUUID(), Map.of(), Instant.now().plusSeconds(600));
    }

    private BotGroup group(long id) {
        BotGroup group = new BotGroup(); group.setId(id); group.setName("group-" + id); group.setEnabled(true); return group;
    }
    private BotChannelInstance bot(long id) {
        BotChannelInstance bot = new BotChannelInstance(); bot.setId(id); bot.setPlatform(BotPlatform.ONEBOT);
        bot.setBotAccountId(Long.toString(id)); bot.setEnabled(true); return bot;
    }
    private BotGroupBot binding(long id, long groupId, long botId, int priority) {
        BotGroupBot binding = new BotGroupBot(); binding.setId(id); binding.setGroupId(groupId);
        binding.setBotInstanceId(botId); binding.setPlatform("ONEBOT"); binding.setScopeType("GROUP");
        binding.setScopeKey("10001"); binding.setReplyPriority(priority); return binding;
    }
}
