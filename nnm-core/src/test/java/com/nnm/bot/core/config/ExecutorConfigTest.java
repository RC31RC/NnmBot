package com.nnm.bot.core.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import java.time.Duration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExecutorConfigTest {
    private final ExecutorConfig config = new ExecutorConfig();
    private final ExecutorProperties properties = new ExecutorProperties();

    @Test
    void capsDefaultPlatformWorkersAndRejectsInvalidConcurrency() {
        assertThat(properties.getTask().getCoreSize()).isEqualTo(
                Math.max(1, Math.min(2, Runtime.getRuntime().availableProcessors())));
        assertThat(properties.getTask().getMaxSize()).isEqualTo(properties.getTask().getCoreSize());
        properties.setDiscoveryConcurrency(0);
        assertThatThrownBy(() -> config.memberDiscoveryExecutor(properties))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validatesIngressBytesAndComputationDeadlines() {
        properties.getCommandIngress().setQueueCapacity(0);
        assertThatThrownBy(properties::validate).isInstanceOf(IllegalArgumentException.class);
        properties.getCommandIngress().setQueueCapacity(1);
        properties.getCommandIngress().setMaxPendingBytes(1);
        assertThatThrownBy(properties::validate).isInstanceOf(IllegalArgumentException.class);
        properties.getCommandIngress().setMaxPendingBytes(65536);
        properties.getComputation().setResultTimeout(Duration.ofMillis(1));
        assertThatThrownBy(properties::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void discoveryUsesVirtualThreadsAndBackpressuresBeforeStartingNextTask() throws Exception {
        properties.setDiscoveryConcurrency(1);
        try (SimpleAsyncTaskExecutor executor =
                     (SimpleAsyncTaskExecutor) config.memberDiscoveryExecutor(properties)) {
            assertBackpressure(executor);
        }
    }

    @Test
    void applicationUsesIndependentlyBoundedVirtualThreads() throws Exception {
        properties.setApplicationConcurrency(1);
        try (SimpleAsyncTaskExecutor executor =
                     (SimpleAsyncTaskExecutor) config.applicationTaskExecutor(properties)) {
            assertBackpressure(executor);
        }
    }

    @Test
    void connectionsUseSeparateBoundedVirtualThreads() throws Exception {
        properties.setConnectionConcurrency(1);
        try (SimpleAsyncTaskExecutor executor =
                     (SimpleAsyncTaskExecutor) config.webSocketConnectExecutor(properties)) {
            assertBackpressure(executor);
        }
        properties.setConnectionConcurrency(0);
        assertThatThrownBy(() -> config.webSocketConnectExecutor(properties))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cpuWorkRunsOnPlatformThreads() throws Exception {
        properties.getTask().setCoreSize(2);
        properties.getTask().setMaxSize(2);
        properties.getTask().setQueueCapacity(3);
        ThreadPoolTaskExecutor executor = config.cpuTaskExecutor(properties);
        executor.initialize();
        try {
            assertThat(executor.submit(() -> Thread.currentThread().isVirtual())
                    .get(5, TimeUnit.SECONDS)).isFalse();
            assertThat(executor.getCorePoolSize()).isEqualTo(2);
            assertThat(executor.getMaxPoolSize()).isEqualTo(2);
            assertThat(executor.getQueueCapacity()).isEqualTo(3);
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void saturatedCpuPoolRejectsInsteadOfRunningOnSubmitter() throws Exception {
        properties.getTask().setCoreSize(1);
        properties.getTask().setMaxSize(1);
        properties.getTask().setQueueCapacity(1);
        ThreadPoolTaskExecutor executor = config.cpuTaskExecutor(properties);
        executor.initialize();
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AtomicBoolean ranOnCaller = new AtomicBoolean();
        try {
            executor.execute(() -> {
                started.countDown();
                try {
                    release.await();
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(interrupted);
                }
            });
            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
            executor.execute(() -> { });
            assertThatThrownBy(() -> executor.execute(() -> ranOnCaller.set(true)))
                    .isInstanceOf(TaskRejectedException.class);
            assertThat(ranOnCaller).isFalse();
        } finally {
            release.countDown();
            executor.shutdown();
        }
    }

    private void assertBackpressure(AsyncTaskExecutor executor) throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch submitting = new CountDownLatch(1);
        AtomicBoolean virtual = new AtomicBoolean();
        executor.execute(() -> {
            virtual.set(Thread.currentThread().isVirtual());
            started.countDown();
            try {
                release.await();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        });
        CompletableFuture<Boolean> second = new CompletableFuture<>();
        Thread submitter = Thread.ofPlatform().unstarted(() -> {
            submitting.countDown();
            executor.execute(() -> second.complete(Thread.currentThread().isVirtual()));
        });
        try {
            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(virtual).isTrue();
            submitter.start();
            assertThat(submitting.await(5, TimeUnit.SECONDS)).isTrue();
            assertThatThrownBy(() -> second.get(100, TimeUnit.MILLISECONDS))
                    .isInstanceOf(TimeoutException.class);
            release.countDown();
            assertThat(second.get(5, TimeUnit.SECONDS)).isTrue();
        } finally {
            release.countDown();
            submitter.join(5000);
        }
        assertThat(submitter.isAlive()).isFalse();
    }
}
