package com.nnm.bot.core.plugin.execution;

import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.mockito.InOrder;
import org.mockito.Mockito;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.api.CommandAccessPolicy;
import com.nnm.bot.core.plugin.api.CommandCooldownGuard;
import com.nnm.bot.core.plugin.api.CommandCostGuard;
import com.nnm.bot.core.plugin.model.CommandAccessDecision;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.plugin.model.CommandCostReservation;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandResultStatus;
import com.nnm.bot.core.plugin.model.DeliveryReport;
import com.nnm.bot.core.constant.CommandDeliveryConstants.DeliveryStatus;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ReplyMessage;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.response.CommandResponseDispatcher;
import com.nnm.bot.core.plugin.response.ReplyDeliveryException;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.nio.file.Files;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandExecutionPipelineTest {

    @Mock
    private CommandResponseDispatcher responseDispatcher;
    @Mock
    private CommandCostGuard costGuard;
    @Mock
    private CommandAccessPolicy accessPolicy;
    @Mock
    private ObjectProvider<CommandAccessPolicy> accessPolicyProvider;
    @Mock
    private ObjectProvider<CommandCooldownGuard> cooldownGuardProvider;
    @Mock
    private ObjectProvider<CommandCostGuard> costGuardProvider;
    @Mock
    private BotGroupMemberIdentityResolver groupMemberIdentityResolver;

    private CommandExecutionPipeline pipeline;
    private CommandCostReservation reservation;

    @BeforeEach
    void setUp() {
        this.pipeline = new CommandExecutionPipeline();
        this.reservation = CommandCostReservation.approved(
                false, null, 10, "COMMAND_REWARD:ONEBOT:upload:message-id");
        ReflectionTestUtils.setField(
                this.pipeline, "responseDispatcher", this.responseDispatcher);
        ReflectionTestUtils.setField(
                this.pipeline, "accessPolicyProvider", this.accessPolicyProvider);
        ReflectionTestUtils.setField(
                this.pipeline, "cooldownGuardProvider", this.cooldownGuardProvider);
        ReflectionTestUtils.setField(
                this.pipeline, "costGuardProvider", this.costGuardProvider);
        ReflectionTestUtils.setField(
                this.pipeline, "groupMemberIdentityResolver", this.groupMemberIdentityResolver);
        Mockito.lenient().when(this.accessPolicyProvider.getIfAvailable()).thenReturn(this.accessPolicy);
        Mockito.lenient().when(this.accessPolicy.check(any(), any(), any()))
                .thenReturn(CommandAccessDecision.permit());
        Mockito.lenient().when(this.costGuardProvider.getIfAvailable()).thenReturn(this.costGuard);
        Mockito.lenient().when(this.costGuard.reserve(any(), any())).thenReturn(this.reservation);
        Mockito.lenient().when(this.responseDispatcher.dispatch(any(), any(), any()))
                .thenReturn(report(DeliveryStatus.ACCEPTED));
    }

    @Test
    void commitsRewardBeforeDispatchingTheFinalResult() {
        stubSuccessfulCommit();
        CommandMatch match = match(CommandResult.text("上传成功"));

        this.pipeline.execute(match);

        InOrder inOrder =
                Mockito.inOrder(this.costGuard, this.responseDispatcher);
        inOrder.verify(this.costGuard).commit(
                match.invocation().context(), match.command().descriptor(),
                this.reservation, CommandResult.text("上传成功"));
        inOrder.verify(this.responseDispatcher).dispatch(
                match.invocation().context(), match.command(), CommandResult.text("上传成功"));
        verify(this.costGuard, never()).refund(any(), any());
    }

    @Test
    void doesNotCommitRewardForRejectedBusinessResult() {
        CommandMatch match = match(CommandResult.rejected("上传内容无效"));

        this.pipeline.execute(match);

        verify(this.responseDispatcher).dispatch(
                match.invocation().context(), match.command(),
                CommandResult.rejected("上传内容无效"));
        verify(this.costGuard, never()).commit(any(), any(), any(), any());
        verify(this.costGuard, never()).refund(any(), any());
    }

    @Test
    void keepsSuccessfulRewardSettlementWhenResponseSendingFails() {
        stubSuccessfulCommit();
        CommandMatch match = match(CommandResult.text("上传成功"));
        RuntimeException failure = new RuntimeException("send failed");
        Mockito.doThrow(failure).when(this.responseDispatcher)
                .dispatch(any(), any(), any());

        Assertions.assertThatThrownBy(() -> this.pipeline.execute(match))
                .isSameAs(failure);

        verify(this.costGuard).commit(any(), any(), any(), any());
        verify(this.costGuard, never()).refund(any(), any());
    }

    @Test
    void unknownDeliveryDoesNotRefundReservedCost() {
        this.reservation = CommandCostReservation.approved(
                true,
                "COMMAND_COST:ONEBOT:upload:message-id",
                10,
                "COMMAND_REWARD:ONEBOT:upload:message-id");
        when(this.costGuard.reserve(any(), any())).thenReturn(this.reservation);
        stubSuccessfulCommit();
        CommandMatch match = match(CommandResult.text("上传成功"));
        RuntimeException failure = new RuntimeException("send failed");
        Mockito.doThrow(failure).when(this.responseDispatcher)
                .dispatch(any(), any(), any());

        Assertions.assertThatThrownBy(() -> this.pipeline.execute(match))
                .isSameAs(failure);

        verify(this.costGuard).commit(any(), any(), any(), any());
        verify(this.costGuard, never()).refund(any(), any());
    }

    @Test
    void confirmedUnsentRefundsReservedCostExactlyOnce() {
        reserveCost();
        stubSuccessfulCommit();
        ReplyDeliveryException failure = new ReplyDeliveryException(report(DeliveryStatus.NOT_SENT),
                new IllegalStateException("owner lost"));
        when(responseDispatcher.dispatch(any(), any(), any())).thenThrow(failure);
        Assertions.assertThatThrownBy(() -> pipeline.execute(match(CommandResult.text("done")))).isSameAs(failure);
        verify(costGuard).refund(reservation, failure);
    }

    @Test
    void rejectedResultRefundIsNotRepeatedWhenRejectionNoticeFails() {
        reserveCost();
        ReplyDeliveryException failure = new ReplyDeliveryException(report(DeliveryStatus.REJECTED),
                new IllegalStateException("notice rejected"));
        when(responseDispatcher.dispatch(any(), any(), any())).thenThrow(failure);
        Assertions.assertThatThrownBy(() -> pipeline.execute(match(CommandResult.rejected("invalid")))).isSameAs(failure);
        verify(costGuard).refund(reservation, null);
        verify(costGuard, never()).commit(any(), any(), any(), any());
    }

    @Test
    void rejectedResultAttachmentsAreClosedWhenRefundFailsBeforeDispatch() throws Exception {
        Path directory = Files.createTempDirectory("nnm-pipeline-rejected-");
        CommandAttachment resource = CommandAttachment.temporaryFile("result.txt", "text/plain",
                Files.writeString(directory.resolve("result.txt"), "result"), directory);
        CommandResult result = new CommandResult(CommandResultStatus.REJECTED,
                List.of(ReplyMessage.content(List.of(new ReplyBlock.File(resource)))), java.util.Map.of());
        IllegalStateException refundFailure = new IllegalStateException("refund failed");
        reserveCost();
        Mockito.doThrow(refundFailure).when(costGuard).refund(reservation, null);

        Assertions.assertThatThrownBy(() -> pipeline.execute(match(result))).isSameAs(refundFailure);

        Assertions.assertThat(directory).doesNotExist();
        verify(responseDispatcher, never()).dispatch(any(), any(), any());
    }

    @Test
    void refundFailureIsSuppressedRatherThanMaskingDeliveryFailure() {
        reserveCost();
        stubSuccessfulCommit();
        ReplyDeliveryException failure = new ReplyDeliveryException(report(DeliveryStatus.REJECTED),
                new IllegalStateException("rejected"));
        IllegalStateException refundFailure = new IllegalStateException("refund failed");
        when(responseDispatcher.dispatch(any(), any(), any())).thenThrow(failure);
        Mockito.doThrow(refundFailure).when(costGuard).refund(reservation, failure);
        Assertions.assertThatThrownBy(() -> pipeline.execute(match(CommandResult.text("done"))))
                .isSameAs(failure).hasSuppressedException(refundFailure);
    }

    private void reserveCost() {
        reservation = CommandCostReservation.approved(true, "cost-key", 10, "reward-key");
        when(costGuard.reserve(any(), any())).thenReturn(reservation);
    }

    private static DeliveryReport report(DeliveryStatus status) {
        return new DeliveryReport(List.of(new DeliveryReport.Item(0, 0, "bot", null, status, null)),
                List.of(), List.of());
    }

    @Test
    void rejectsBusinessCommandWhenAccessPolicyIsMissing() {
        when(this.accessPolicyProvider.getIfAvailable()).thenReturn(null);
        CommandMatch match = match("holdfasts", CommandResult.text("不应执行"));

        this.pipeline.execute(match);

        verify(match.command().plugin(), never()).handle(any());
        verify(this.costGuard, never()).reserve(any(), any());
        verify(this.responseDispatcher, never()).dispatch(any(), any(), any());
    }

    @Test
    void permitsSystemCommandWhenAccessPolicyIsMissing() {
        when(this.accessPolicyProvider.getIfAvailable()).thenReturn(null);
        stubSuccessfulCommit();
        CommandMatch match = match(SystemCommandCodes.STATUS, CommandResult.text("运行正常"));

        this.pipeline.execute(match);

        verify(match.command().plugin()).handle(match.invocation());
        verify(this.responseDispatcher).dispatch(
                match.invocation().context(), match.command(), CommandResult.text("运行正常"));
    }

    @Test
    void checksAuthorizationBeforeClaimAndDuplicateDoesNotConsumeCost() {
        GroupCommandCoordinator coordinator = mock(GroupCommandCoordinator.class);
        ReflectionTestUtils.setField(pipeline, "coordinator", coordinator);
        CommandMatch match = match(CommandResult.text("done"));
        when(accessPolicy.check(any(), any(), any())).thenReturn(CommandAccessDecision.denied("denied"));
        pipeline.execute(match);
        verifyNoClaim(coordinator);
        when(accessPolicy.check(any(), any(), any())).thenReturn(CommandAccessDecision.permit());
        when(coordinator.claim(any())).thenReturn(false);
        pipeline.execute(match);
        InOrder order = Mockito.inOrder(accessPolicy, coordinator);
        order.verify(accessPolicy, Mockito.times(2)).check(any(), any(), any());
        order.verify(coordinator).claim(match.invocation().context());
        verify(costGuard, never()).reserve(any(), any());
        verify(match.command().plugin(), never()).handle(any());
    }

    private static void verifyNoClaim(GroupCommandCoordinator coordinator) {
        verify(coordinator, never()).claim(any());
    }

    private void stubSuccessfulCommit() {
        when(this.costGuard.commit(any(), any(), any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(3));
    }

    private static CommandMatch match(CommandResult result) {
        return match("upload", result);
    }

    private static CommandMatch match(String commandCode, CommandResult result) {
        BotPlugin plugin = mock(BotPlugin.class);
        Mockito.lenient().when(plugin.handle(any())).thenReturn(result);
        CommandDescriptor descriptor = new CommandDescriptor(
                commandCode, commandCode, List.of(), "", "", MatchType.EXACT, 1);
        RuntimeCommand command = new RuntimeCommand(
                plugin, descriptor, CommandExecutionConfiguration.defaults());
        BotMessageContext context = BotMessageContext.of(
                BotPlatform.ONEBOT,
                BotMessageScope.GROUP,
                "bot-account",
                "sender-id",
                "group-id",
                "message-id",
                "上传",
                "上传",
                null);
        return new CommandMatch(
                command,
                new CommandInvocation(context, commandCode, commandCode, ""));
    }
}
