package com.nnm.bot.common.concurrent;

import com.nnm.bot.common.constant.ComputationErrorCode;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.exception.ExceptionCause;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** 同步结果边界，执行器由装配层提供；取消等待不等于实际计算结束。 */
@Slf4j
public final class BoundedTaskExecutor implements AutoCloseable {
    private final ReentrantLock tasksLock = new ReentrantLock();

    private final AsyncTaskExecutor executor;
    private final int maxInFlight;
    private final Duration maxQueueWait;
    private final Duration resultTimeout;
    private final Set<Task<?>> tasks = new HashSet<>();
    private boolean closed;

    public BoundedTaskExecutor(AsyncTaskExecutor executor, int maxInFlight, Duration maxQueueWait,
        Duration resultTimeout) {
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
        if (maxInFlight < 1 || maxQueueWait == null || maxQueueWait.toNanos() < 1 || resultTimeout == null
            || resultTimeout.compareTo(maxQueueWait) < 0) {
            throw new IllegalArgumentException("Invalid computation capacity or timing");
        }
        this.maxInFlight = maxInFlight;
        this.maxQueueWait = maxQueueWait;
        this.resultTimeout = resultTimeout;
    }

    /**
     * 在容量和等待预算内取得计算结果；等待结束不代表底层原生计算已经停止。
     *
     * @param action 不读取调用线程事务或锁上下文的计算动作
     * @param <T> 结果类型
     * @return 计算结果
     */
    public <T> T execute(Supplier<T> action) {
        Objects.requireNonNull(action, "action must not be null");
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("禁止在数据库事务中等待平台线程计算");
        }
        Task<T> task;
        tasksLock.lock();
        try {
            if (closed)
                throw new SystemException(ComputationErrorCode.SHUTDOWN, (Throwable)null);
            if (tasks.size() >= maxInFlight)
                throw new SystemException(ComputationErrorCode.BUSY, (Throwable)null);
            task = new Task<>(action);
            tasks.add(task);
        } finally {
            tasksLock.unlock();
        }
        try {
            executor.execute(task);
        } catch (TaskRejectedException rejected) {
            task.cancel(new SystemException(ComputationErrorCode.BUSY, "计算执行器拒绝任务", rejected));
        } catch (RuntimeException | Error failure) {
            task.cancel(failure);
            throw failure;
        }
        long deadline = task.submitted + resultTimeout.toNanos();
        long remaining = Math.max(0, maxQueueWait.toNanos() - (System.nanoTime() - task.submitted));
        while (true) {
            try {
                return task.result.get(remaining, TimeUnit.NANOSECONDS);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                SystemException failure =
                    new SystemException(ComputationErrorCode.INTERRUPTED, "等待计算结果被中断", interrupted);
                task.cancel(failure);
                throw failure;
            } catch (TimeoutException timeout) {
                tasksLock.lock();
                try {
                    if (task.result.isDone()) {
                        remaining = 0;
                        continue;
                    }
                    remaining = deadline - System.nanoTime();
                    if (task.running && remaining > 0)
                        continue;
                    ComputationErrorCode code =
                        task.running ? ComputationErrorCode.WAIT_TIMEOUT : ComputationErrorCode.QUEUE_EXPIRED;
                    SystemException failure = new SystemException(code, code.getMessage(), timeout);
                    task.cancel(failure);
                    throw failure;
                } finally {
                    tasksLock.unlock();
                }
            } catch (ExecutionException failure) {
                Throwable cause = ExceptionCause.unwrap(failure);
                if (cause instanceof RuntimeException runtime)
                    throw runtime;
                if (cause instanceof Error error)
                    throw error;
                throw new IllegalStateException("Computation failed", cause);
            }
        }
    }

    /** 返回尚未真正结束的排队与运行任务数量。 */
    public int inFlight() {
        tasksLock.lock();
        try {
            return tasks.size();
        } finally {
            tasksLock.unlock();
        }
    }

    /** 停止接纳并唤醒等待者；运行中任务在自身退出后释放额度。 */
    @Override
    public void close() {
        tasksLock.lock();
        try {
            closed = true;
            for (Task<?> task : new ArrayList<>(tasks)) {
                task.cancel(new SystemException(ComputationErrorCode.SHUTDOWN, (Throwable)null));
            }
        } finally {
            tasksLock.unlock();
        }
    }

    private final class Task<T> implements Runnable {
        private final CompletableFuture<T> result = new CompletableFuture<>();
        private final long submitted = System.nanoTime();
        private Supplier<T> action;
        private boolean running;
        private boolean terminated;

        private Task(Supplier<T> action) {
            this.action = action;
        }

        @Override
        public void run() {
            Supplier<T> work;
            tasksLock.lock();
            try {
                if (terminated)
                    return;
                if (System.nanoTime() - submitted >= maxQueueWait.toNanos()) {
                    cancel(new SystemException(ComputationErrorCode.QUEUE_EXPIRED, (Throwable)null));
                    return;
                }
                running = true;
                work = action;
                action = null;
            } finally {
                tasksLock.unlock();
            }
            try {
                result.complete(work.get());
            } catch (RuntimeException | Error failure) {
                // Future 是异常传播边界；调用方已超时/停止时仍记录未能交付的真实错误。
                if (!result.completeExceptionally(failure)) {
                    log.error("Computation failed after caller stopped waiting", failure);
                }
            } finally {
                tasksLock.lock();
                try {
                    terminated = true;
                    tasks.remove(this);
                } finally {
                    tasksLock.unlock();
                }
            }
        }

        private void cancel(Throwable failure) {
            tasksLock.lock();
            try {
                result.completeExceptionally(failure);
                if (!running && !terminated) {
                    terminated = true;
                    action = null;
                    tasks.remove(this);
                }
            } finally {
                tasksLock.unlock();
            }
        }
    }
}
