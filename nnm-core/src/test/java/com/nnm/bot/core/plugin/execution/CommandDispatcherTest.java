package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.model.DeliveryReport;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.core.plugin.response.CommandResponseDispatcher;
import com.nnm.bot.core.plugin.response.ReplyDeliveryException;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommandDispatcherTest {
    private final CommandDispatcher dispatcher = new CommandDispatcher();
    private final CommandExecutionPipeline pipeline = mock(CommandExecutionPipeline.class);
    private final CommandResponseDispatcher responses = mock(CommandResponseDispatcher.class);
    private final ClusterBotRuntime cluster = mock(ClusterBotRuntime.class);
    private final CommandRegistry registry = mock(CommandRegistry.class);
    private final BotMessageContext context = BotMessageContext.of(BotPlatform.ONEBOT,
            BotMessageScope.GROUP, "123", "user", "group", "id", "test", "test", null);
    private CommandMatch match;

    @BeforeEach
    void setup() {
        RuntimeCommand command = mock(RuntimeCommand.class, RETURNS_DEEP_STUBS);
        when(command.descriptor().code()).thenReturn("test");
        match = new CommandMatch(command, new CommandInvocation(context, "test", "test", ""));
        when(registry.command("test")).thenReturn(Optional.of(command));
        ReflectionTestUtils.setField(dispatcher, "registry", registry);
        when(cluster.isLocalOwner(any())).thenReturn(true);
        StaticListableBeanFactory beans = new StaticListableBeanFactory(Map.of("cluster", cluster));
        ReflectionTestUtils.setField(dispatcher, "clusterProvider", beans.getBeanProvider(ClusterBotRuntime.class));
        ReflectionTestUtils.setField(dispatcher, "executionPipeline", pipeline);
        ReflectionTestUtils.setField(dispatcher, "responseDispatcher", responses);
    }

    @Test
    void synchronousDispatchExecutesAlreadyMatchedCommandOnce() {
        dispatcher.dispatch(match);
        verify(pipeline).execute(match);
        verifyNoInteractions(responses);
    }

    @Test
    void nonOwnerDoesNotExecuteOrSend() {
        when(cluster.isLocalOwner(any())).thenReturn(false);
        dispatcher.dispatch(match);
        verifyNoInteractions(pipeline, responses);
    }

    @Test
    void businessFailureIsNotSwallowedAndNoticeFailureIsSuppressed() {
        RuntimeException failure = new IllegalStateException("business failed");
        RuntimeException notice = new IllegalStateException("notice failed");
        doThrow(failure).when(pipeline).execute(match);
        doThrow(notice).when(responses).dispatchNotice(context, "命令执行失败，请稍后再试");

        assertThatThrownBy(() -> dispatcher.dispatch(match)).isSameAs(failure)
                .hasSuppressedException(notice);
        verify(pipeline).execute(match);
    }

    @Test
    void partialResponseFailurePropagatesWithoutSendingAnotherNotice() {
        ReplyDeliveryException failure = new ReplyDeliveryException(
                new DeliveryReport(List.of(), List.of(), List.of()),
                new IllegalStateException("partial send"));
        doThrow(failure).when(pipeline).execute(match);
        assertThatThrownBy(() -> dispatcher.dispatch(match)).isSameAs(failure);
        verifyNoInteractions(responses);
    }
}
