package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.constant.CommandExecutionConstants.CommandIngressResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import com.nnm.bot.core.config.ExecutorConfig;
import com.nnm.bot.core.config.ExecutorProperties;
import com.nnm.bot.core.config.CommandRoutingProperties;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.execution.impl.CommandIngressServiceImpl;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommandIngressServiceTest {
    private final CommandIngressServiceImpl service = new CommandIngressServiceImpl();
    private final CommandRegistry registry = mock(CommandRegistry.class);
    private final CommandDispatcher dispatcher = mock(CommandDispatcher.class);
    private final ExecutorProperties properties = new ExecutorProperties();
    private final SimpleMeterRegistry meters = new SimpleMeterRegistry();
    private final CountDownLatch release = new CountDownLatch(1);
    private SimpleAsyncTaskExecutor executor;

    @BeforeEach
    void setup() {
        properties.setApplicationConcurrency(1);
        properties.setAwaitTermination(Duration.ofMillis(100));
        properties.getCommandIngress().setQueueCapacity(1);
        executor = (SimpleAsyncTaskExecutor) new ExecutorConfig().applicationTaskExecutor(properties);
        ReflectionTestUtils.setField(service, "properties", properties);
        ReflectionTestUtils.setField(service, "routingProperties", new CommandRoutingProperties(
                Duration.ofSeconds(3), Duration.ofMinutes(2), 2048));
        ReflectionTestUtils.setField(service, "registry", registry);
        ReflectionTestUtils.setField(service, "dispatcher", dispatcher);
        ReflectionTestUtils.setField(service, "applicationExecutor", executor);
        StaticListableBeanFactory beans = new StaticListableBeanFactory(Map.of("meters", meters));
        ReflectionTestUtils.setField(service, "meterRegistryProvider", beans.getBeanProvider(MeterRegistry.class));
        when(registry.match(any())).thenAnswer(call -> Optional.of(match(call.getArgument(0))));
        service.initialize();
        service.start();
    }

    @AfterEach
    void close() {
        release.countDown();
        service.stop();
        executor.close();
        meters.close();
    }

    @Test
    void unmatchedDoesNotCreateBusinessTaskAndMatchingFailurePropagates() {
        BotMessageContext context = context("1");
        when(registry.match(context)).thenReturn(Optional.empty());
        assertThat(service.accept(context, () -> true)).isEqualTo(CommandIngressResult.NOT_MATCHED);
        verifyNoInteractions(dispatcher);
        assertThat(service.snapshot().inFlight()).isZero();
        IllegalStateException failure = new IllegalStateException("broken index");
        when(registry.match(context)).thenThrow(failure);
        assertThatThrownBy(() -> service.accept(context, () -> true)).isSameAs(failure);
    }

    @Test
    void saturationReturnsImmediatelyAndCountsDispatchingAsWaiting() throws Exception {
        CountDownLatch started = blockingCommand();
        assertThat(service.accept(context("1"), () -> true)).isEqualTo(CommandIngressResult.ACCEPTED);
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(service.accept(context("2"), () -> true)).isEqualTo(CommandIngressResult.ACCEPTED);
        await().untilAsserted(() -> assertThat(service.snapshot().dispatching()).isOne());

        assertThat(service.accept(context("3"), () -> true)).isEqualTo(CommandIngressResult.REJECTED_CAPACITY);
        assertThat(service.snapshot().inFlight()).isEqualTo(2);
        assertThat(service.snapshot().running()).isOne();
        verify(registry, times(3)).match(any());
        release.countDown();
        await().untilAsserted(() -> assertThat(service.snapshot().reservedBytes()).isZero());
        verify(dispatcher, times(2)).dispatch(any());
    }

    @Test
    void shutdownCancelsUnstartedWithoutReleasingRunningWorkEarly() throws Exception {
        CountDownLatch started = blockingCommand();
        service.accept(context("1"), () -> true);
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        service.accept(context("2"), () -> true);
        await().untilAsserted(() -> assertThat(service.snapshot().dispatching()).isOne());
        service.stop();
        assertThat(service.accept(context("3"), () -> true)).isEqualTo(CommandIngressResult.REJECTED_SHUTDOWN);
        assertThat(service.snapshot().inFlight()).isOne();
        release.countDown();
        await().untilAsserted(() -> assertThat(service.snapshot().inFlight()).isZero());
        verify(dispatcher).dispatch(any());
    }

    @Test
    void virtualThreadShutdownReleasesTheLockWhileDrainingAndWakesOnCompletion() throws Exception {
        properties.setAwaitTermination(Duration.ofSeconds(30));
        CountDownLatch started = blockingCommand();
        assertThat(service.accept(context("running"), () -> true)).isEqualTo(CommandIngressResult.ACCEPTED);
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        try (ExecutorService callers = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<?> stopped = callers.submit(() -> { service.stop(); });
            try {
                await().atMost(Duration.ofSeconds(5)).until(() -> !service.isRunning());
                assertThat(stopped.isDone()).isFalse();
                assertThat(callers.submit(service::snapshot).get(5, TimeUnit.SECONDS).running()).isOne();
                release.countDown();
                stopped.get(5, TimeUnit.SECONDS);
                assertThat(service.snapshot().inFlight()).isZero();
                assertThat(service.snapshot().reservedBytes()).isZero();
                verify(dispatcher).dispatch(any());
            } finally {
                release.countDown();
            }
        }
    }

    @Test
    void interruptedVirtualThreadShutdownRestoresInterruptAndCancelsWaitingCommands() throws Exception {
        properties.setAwaitTermination(Duration.ofSeconds(30));
        CountDownLatch started = blockingCommand();
        assertThat(service.accept(context("running"), () -> true)).isEqualTo(CommandIngressResult.ACCEPTED);
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(service.accept(context("waiting"), () -> true)).isEqualTo(CommandIngressResult.ACCEPTED);
        await().untilAsserted(() -> assertThat(service.snapshot().dispatching()).isOne());
        AtomicReference<Throwable> failure = new AtomicReference<>();
        AtomicBoolean interrupted = new AtomicBoolean();
        Thread stopper = Thread.ofVirtual().start(() -> {
            try {
                service.stop();
            } catch (RuntimeException exception) {
                failure.set(exception);
                interrupted.set(Thread.currentThread().isInterrupted());
            }
        });
        try {
            await().atMost(Duration.ofSeconds(5)).until(() -> !service.isRunning());
            stopper.interrupt();
            stopper.join(5000);
            assertThat(stopper.isAlive()).isFalse();
            assertThat(failure.get()).isInstanceOf(IllegalStateException.class)
                    .hasCauseInstanceOf(InterruptedException.class);
            assertThat(interrupted).isTrue();
            assertThat(service.snapshot().inFlight()).isOne();
            assertThat(service.snapshot().dispatching()).isZero();
            release.countDown();
            await().untilAsserted(() -> assertThat(service.snapshot().inFlight()).isZero());
            verify(dispatcher).dispatch(any());
        } finally {
            release.countDown();
            stopper.interrupt();
            stopper.join(5000);
        }
    }

    @Test
    void expiredOrStaleConnectionDoesNotExecute() throws Exception {
        CountDownLatch started = blockingCommand();
        service.accept(context("1"), () -> true);
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        properties.getCommandIngress().setMaxQueueWait(Duration.ofNanos(1));
        service.accept(context("2"), () -> true);
        release.countDown();
        await().untilAsserted(() -> assertThat(service.snapshot().inFlight()).isZero());
        verify(dispatcher).dispatch(any());
        properties.getCommandIngress().setMaxQueueWait(Duration.ofSeconds(10));
        service.accept(context("3"), () -> false);
        await().untilAsserted(() -> assertThat(service.snapshot().reservedBytes()).isZero());
        verify(dispatcher).dispatch(any());
    }

    @Test
    void payloadLimitDoesNotMatchOrCreateBusinessTask() {
        properties.getCommandIngress().setMaxMessageBytes(600);
        assertThat(service.accept(context("1"), () -> true)).isEqualTo(CommandIngressResult.REJECTED_TOO_LARGE);
        assertThat(service.snapshot().inFlight()).isZero();
        verifyNoInteractions(registry, dispatcher);
    }

    @Test
    void concurrentAdmissionsCannotOverbookTheWaitingSlot() throws Exception {
        CountDownLatch started = blockingCommand();
        service.accept(context("running"), () -> true);
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService submitters = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<CommandIngressResult>> tasks = IntStream.range(0, 8).<Callable<CommandIngressResult>>mapToObj(index -> () -> {
                start.await();
                return service.accept(context("queued-" + index), () -> true);
            }).toList();
            List<Future<CommandIngressResult>> results = tasks.stream().map(submitters::submit).toList();
            start.countDown();
            int accepted = 0;
            for (Future<CommandIngressResult> result : results) {
                if (result.get(5, TimeUnit.SECONDS) == CommandIngressResult.ACCEPTED) accepted++;
            }
            assertThat(accepted).isOne();
            assertThat(service.snapshot().inFlight()).isEqualTo(2);
        }
    }

    @Test
    void byteBudgetRejectsEvenWhenAWaitingSlotIsAvailable() throws Exception {
        properties.getCommandIngress().setMaxMessageBytes(1000);
        properties.getCommandIngress().setMaxPendingBytes(1000);
        CountDownLatch started = blockingCommand();
        service.accept(context("1"), () -> true);
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(service.accept(context("2"), () -> true)).isEqualTo(CommandIngressResult.REJECTED_CAPACITY);
        assertThat(service.snapshot().inFlight()).isOne();
    }

    private CountDownLatch blockingCommand() {
        CountDownLatch started = new CountDownLatch(1);
        doAnswer(call -> {
            assertThat(Thread.currentThread().isVirtual()).isTrue();
            started.countDown();
            assertThat(release.await(5, TimeUnit.SECONDS)).isTrue();
            return null;
        }).when(dispatcher).dispatch(any());
        return started;
    }

    private static BotMessageContext context(String id) {
        return BotMessageContext.of(BotPlatform.ONEBOT, BotMessageScope.GROUP, "bot", "user",
                "group", id, "command", "command", null);
    }

    private static CommandMatch match(BotMessageContext context) {
        RuntimeCommand command = mock(RuntimeCommand.class);
        return new CommandMatch(command, new CommandInvocation(context, "command", "command", ""));
    }
}
