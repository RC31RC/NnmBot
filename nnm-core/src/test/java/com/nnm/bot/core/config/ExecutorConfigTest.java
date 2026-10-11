package com.nnm.bot.core.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExecutorConfigTest {
    private final ExecutorConfig config = new ExecutorConfig();
    private final ExecutorProperties properties = new ExecutorProperties();

    @Test
    void usesCpuCountAsDefaultAndRejectsInvalidConcurrency() {
        assertThat(properties.getTask().getCoreSize()).isEqualTo(
                Runtime.getRuntime().availableProcessors());
        assertThat(properties.getTask().getMaxSize()).isEqualTo(properties.getTask().getCoreSize());
        properties.setDiscoveryConcurrency(0);
        assertThatThrownBy(() -> config.memberDiscoveryExecutor(properties))
                .isInstanceOf(IllegalArgumentException.class);
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
        ThreadPoolTaskExecutor executor = config.cpuTaskExecutor(properties);
        executor.initialize();
        try {
            assertThat(executor.submit(() -> Thread.currentThread().isVirtual())
                    .get(5, TimeUnit.SECONDS)).isFalse();
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void communitySourcesUseIndependentBoundedWorkersAndCallerBackpressure() throws Exception {
        properties.setCommunitySourceConcurrency(1);
        properties.setCommunitySourceQueueCapacity(1);
        ThreadPoolTaskExecutor executor = config.communitySourceExecutor(properties);
        executor.initialize();
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        try {
            executor.execute(() -> {
                started.countDown();
                try {
                    release.await();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            });
            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
            var queued = executor.submit(() -> Thread.currentThread().getName());
            Thread submitter = Thread.currentThread();
            assertThat(executor.submit(Thread::currentThread).get(5, TimeUnit.SECONDS))
                    .isSameAs(submitter);
            assertThat(executor.getMaxPoolSize()).isEqualTo(1);
            assertThat(executor.getQueueSize()).isEqualTo(1);
            release.countDown();
            assertThat(queued.get(5, TimeUnit.SECONDS)).startsWith("nnm-community-source-");
        } finally {
            release.countDown();
            executor.shutdown();
        }
        assertThatThrownBy(() -> executor.execute(() -> {}))
                .isInstanceOf(RejectedExecutionException.class);
        properties.setCommunitySourceQueueCapacity(-1);
        assertThatThrownBy(() -> config.communitySourceExecutor(properties))
                .isInstanceOf(IllegalArgumentException.class);
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
