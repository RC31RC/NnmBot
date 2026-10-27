package com.nnm.bot.common.concurrent;

import java.util.concurrent.ExecutorService;
import com.nnm.bot.common.exception.BusinessException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BoundedTaskExecutorTest {
    @Test
    void refusesToHoldDatabaseTransactionWhileWaitingForAnotherThread() {
        AsyncTaskExecutor executor = mock(AsyncTaskExecutor.class);
        try (BoundedTaskExecutor boundary = new BoundedTaskExecutor(executor, 1, Duration.ofSeconds(1), Duration.ofSeconds(2))) {
            TransactionSynchronizationManager.setActualTransactionActive(true);
            try {
                assertThatThrownBy(() -> boundary.execute(() -> "unused"))
                        .isInstanceOf(IllegalStateException.class).hasMessageContaining("database transaction");
                verifyNoInteractions(executor);
            } finally {
                TransactionSynchronizationManager.clear();
            }
        }
    }

    @Test
    void callerTimeoutDoesNotReleaseRunningNativeWork() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        try (ExecutorService workers = Executors.newFixedThreadPool(1);
             BoundedTaskExecutor boundary = new BoundedTaskExecutor(new TaskExecutorAdapter(workers), 1,
                     Duration.ofMillis(200), Duration.ofMillis(300))) {
            try {
                assertThatThrownBy(() -> boundary.execute(() -> {
                    assertThat(Thread.currentThread().isVirtual()).isFalse();
                    started.countDown();
                    waitFor(release);
                    return "finished";
                })).isInstanceOf(BusinessException.class).hasMessageContaining("等待计算结果超时");
                assertThat(started.getCount()).isZero();
                assertThat(boundary.inFlight()).isOne();
                assertThatThrownBy(() -> boundary.execute(() -> "another"))
                        .isInstanceOf(BusinessException.class).hasMessageContaining("繁忙");
            } finally {
                release.countDown();
            }
            await().untilAsserted(() -> assertThat(boundary.inFlight()).isZero());
            assertThat(boundary.execute(() -> "next")).isEqualTo("next");
        }
    }

    @Test
    void rejectionAndWorkFailureReleaseExactlyOnceAndPreserveCause() {
        AsyncTaskExecutor rejected = mock(AsyncTaskExecutor.class);
        TaskRejectedException rejection = new TaskRejectedException("full");
        doThrow(rejection).when(rejected).execute(any(Runnable.class));
        try (BoundedTaskExecutor boundary = new BoundedTaskExecutor(rejected, 1, Duration.ofSeconds(1), Duration.ofSeconds(2))) {
            assertThatThrownBy(() -> boundary.execute(() -> "unused"))
                    .isInstanceOf(BusinessException.class).hasCause(rejection);
            assertThat(boundary.inFlight()).isZero();
        }
        try (BoundedTaskExecutor boundary = new BoundedTaskExecutor(new TaskExecutorAdapter(Runnable::run), 1,
                Duration.ofSeconds(1), Duration.ofSeconds(2))) {
            IllegalStateException failure = new IllegalStateException("engine failed");
            assertThatThrownBy(() -> boundary.execute(() -> { throw failure; })).isSameAs(failure);
            assertThat(boundary.inFlight()).isZero();
        }
    }

    @Test
    void queueDeadlineCancelsUnstartedWorkBeforeTheLongerResultDeadline() {
        AtomicReference<Runnable> queued = new AtomicReference<>();
        try (BoundedTaskExecutor boundary = new BoundedTaskExecutor(new TaskExecutorAdapter(queued::set), 1,
                Duration.ofMillis(20), Duration.ofSeconds(30))) {
            Runnable work = mock(Runnable.class);
            assertThatThrownBy(() -> boundary.execute(() -> { work.run(); return "unused"; }))
                    .isInstanceOf(BusinessException.class).hasMessageContaining("排队超时");
            queued.get().run();
            assertThat(boundary.inFlight()).isZero();
            verifyNoInteractions(work);
        }
    }

    @Test
    void shutdownCancelsQueuedWorkAndWakesWaiterWithoutRunningIt() throws Exception {
        AtomicReference<Runnable> queued = new AtomicReference<>();
        CountDownLatch submitted = new CountDownLatch(1);
        BoundedTaskExecutor boundary = new BoundedTaskExecutor(new TaskExecutorAdapter(task -> {
            queued.set(task);
            submitted.countDown();
        }), 1, Duration.ofSeconds(5), Duration.ofSeconds(10));
        Runnable work = mock(Runnable.class);
        AtomicReference<Throwable> result = new AtomicReference<>();
        Thread caller = Thread.ofVirtual().start(() -> {
            try {
                boundary.execute(() -> { work.run(); return "unused"; });
            } catch (RuntimeException failure) {
                result.set(failure);
            }
        });
        assertThat(submitted.await(5, TimeUnit.SECONDS)).isTrue();
        boundary.close();
        caller.join(5000);
        assertThat(caller.isAlive()).isFalse();
        assertThat(result.get()).isInstanceOf(BusinessException.class).hasMessageContaining("停止");
        queued.get().run();
        assertThat(boundary.inFlight()).isZero();
        verifyNoInteractions(work);
    }

    @Test
    void interruptionRestoresFlagAndCancelsUnstartedWork() {
        AsyncTaskExecutor executor = mock(AsyncTaskExecutor.class);
        try (BoundedTaskExecutor boundary = new BoundedTaskExecutor(executor, 1, Duration.ofSeconds(1), Duration.ofSeconds(2))) {
            Thread.currentThread().interrupt();
            try {
                assertThatThrownBy(() -> boundary.execute(() -> "unused"))
                        .isInstanceOf(BusinessException.class).hasMessageContaining("中断");
                assertThat(Thread.currentThread().isInterrupted()).isTrue();
                assertThat(boundary.inFlight()).isZero();
            } finally {
                Thread.interrupted();
            }
        }
    }

    private static void waitFor(CountDownLatch latch) {
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Test latch timed out");
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(interrupted);
        }
    }
}
