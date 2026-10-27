package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.constant.NotificationConstants.BlockType;

import com.nnm.bot.core.constant.CommandDeliveryConstants.Outcome;

import com.nnm.bot.core.mq.message.notification.NotificationContent;
import com.nnm.bot.core.mq.message.notification.NotificationResourceManifest;
import com.nnm.bot.core.channel.notification.store.NotificationCancellationStore;
import com.nnm.bot.core.channel.notification.store.NotificationResourceStorage;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.exception.BotMessageRejectedException;
import com.nnm.bot.core.message.model.BotMessageReceipt;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.message.model.BotProactiveTarget;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.message.sender.BotProactiveMessageSenderRouter;
import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.constant.CommandDeliveryConstants.DeliveryStatus;
import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProactiveReplyDispatcherTest {
    @Mock
    private ReplyPlanner planner;
    @Mock
    private BotProactiveMessageSenderRouter senders;
    @Mock
    private NotificationResourceStorage resources;
    @Mock
    private ClusterBotRuntime runtime;
    @Mock
    private NotificationCancellationStore cancellations;
    @Mock
    private NotificationDeliveryGuard deliveryGuard;
    @InjectMocks
    private ProactiveReplyDispatcher dispatcher;
    private final BotProactiveTarget target = new BotProactiveTarget(BotPlatform.ONEBOT, "bot", "group", List.of());
    private final NotificationResourceManifest manifest = new NotificationResourceManifest(UUID.randomUUID(), Map.of(), Instant.now().plusSeconds(600));
    private final NotificationContent content = new NotificationContent(List.of(new NotificationContent.Message(List.of(
            new NotificationContent.Block(BlockType.TEXT, "正文", null, null)), List.of(), null)),
            CompositionPolicy.ALLOW_SPLIT, InteractionPolicy.COMMAND_TEXT);
    private final List<ResponsePlan> plans = List.of(plan(0), plan(1));

    @BeforeEach
    void setup() {
        ReplyTarget targetCapabilities = mock(ReplyTarget.class);
        when(senders.target(target)).thenReturn(targetCapabilities);
        when(planner.plan(eq(targetCapabilities), any(), any(), any())).thenReturn(Optional.of(plans));
        when(planner.compile(targetCapabilities, plans)).thenReturn(plans);
        when(runtime.isLocalOwner(any())).thenReturn(true);
    }

    @Test
    void secondUnknownDoesNotReplayAcceptedFirstMessageAndCleanupDoesNotChangeAcceptance() {
        AtomicInteger attempts = new AtomicInteger();
        when(senders.prepare(eq(target), any())).thenAnswer(call -> new PreparedBotMessage(() -> {
            if (attempts.getAndIncrement() == 1) throw new IllegalStateException("timeout");
            return new BotMessageReceipt("accepted");
        }, () -> { throw new IllegalStateException("cleanup failed"); }, BotMessageSendPermit.unrestricted()));
        ReplyDeliveryException failure = catchThrowableOfType(() -> dispatcher.dispatch(target, content, manifest, manifest.expiresAt()), ReplyDeliveryException.class);
        assertThat(failure.report().items()).extracting(item -> item.status()).containsExactly(DeliveryStatus.ACCEPTED, DeliveryStatus.UNKNOWN);
        assertThat(failure.report().cleanupFailures()).hasSize(2);
        assertThat(attempts).hasValue(2);
        verify(senders, times(2)).prepare(eq(target), any());
    }

    @Test
    void cancellationBetweenMessagesStopsOnlyTheUnsubmittedRemainder() {
        AtomicBoolean cancelled = new AtomicBoolean(); AtomicInteger attempts = new AtomicInteger();
        when(cancellations.cancelled(manifest.submissionId())).thenAnswer(call -> cancelled.get());
        when(senders.prepare(eq(target), any())).thenAnswer(call -> new PreparedBotMessage(() -> {
            attempts.incrementAndGet(); cancelled.set(true); return new BotMessageReceipt("accepted");
        }, () -> {}, BotMessageSendPermit.unrestricted()));
        ReplyDeliveryException failure = catchThrowableOfType(() -> dispatcher.dispatch(target, content, manifest, manifest.expiresAt()), ReplyDeliveryException.class);
        assertThat(failure.report().items()).extracting(item -> item.status()).containsExactly(DeliveryStatus.ACCEPTED, DeliveryStatus.NOT_SENT);
        assertThat(attempts).hasValue(1);
        verify(deliveryGuard, times(3)).validate(manifest.submissionId(), target);
    }

    @ParameterizedTest
    @EnumSource(TerminalFailure.class)
    void terminalSendOutcomesAreDistinctAndNeverRetried(TerminalFailure kind) {
        RuntimeException failure = switch (kind) {
            case REJECTED -> new BotMessageRejectedException("platform rejected", false);
            case NOT_SENT -> new BotMessageNotSentException("owner changed before submit");
            case UNKNOWN -> new IllegalStateException("connection lost after submit");
        };
        DeliveryStatus expected = switch (kind) {
            case REJECTED -> DeliveryStatus.REJECTED;
            case NOT_SENT -> DeliveryStatus.NOT_SENT;
            case UNKNOWN -> DeliveryStatus.UNKNOWN;
        };
        AtomicInteger sendAttempts = new AtomicInteger();
        when(senders.prepare(eq(target), any())).thenAnswer(call -> new PreparedBotMessage(() -> {
            sendAttempts.incrementAndGet();
            throw failure;
        }, () -> {}, BotMessageSendPermit.unrestricted()));

        ReplyDeliveryException result = catchThrowableOfType(
                () -> dispatcher.dispatch(target, content, manifest, manifest.expiresAt()), ReplyDeliveryException.class);

        assertThat(result.report().items()).extracting(item -> item.status())
                .containsExactly(expected, DeliveryStatus.NOT_SENT);
        assertThat(result.report().outcome()).isEqualTo(kind == TerminalFailure.UNKNOWN
                ? Outcome.UNKNOWN : Outcome.FAILED);
        assertThat(result.report().definitelyUnsent()).isEqualTo(kind != TerminalFailure.UNKNOWN);
        assertThat(sendAttempts).hasValue(1);
        verify(senders, times(2)).prepare(eq(target), any());
    }

    private enum TerminalFailure {
        REJECTED,
        NOT_SENT,
        UNKNOWN
    }

    private static ResponsePlan plan(int index) {
        return new ResponsePlan(index, 0, List.of(new ReplyBlock.Text("正文" + index)), List.of(), null,
                CompositionPolicy.ALLOW_SPLIT, ViewFormat.TEXT, false);
    }
}
