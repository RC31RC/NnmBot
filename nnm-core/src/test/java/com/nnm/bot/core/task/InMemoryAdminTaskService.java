package com.nnm.bot.core.task;

import com.nnm.bot.common.constant.TaskConstants.AdminTaskStatus;

import com.nnm.bot.common.task.*;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.constant.CommonErrorCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.UndeclaredThrowableException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 管理端长任务的进程内状态中心。
 *
 * <p>任务结果需要支持 SSE 断线恢复，因此保留完成/失败任务一段时间；
 * 它不是持久化队列，应用重启后任务状态自然失效。</p>
 */
@Slf4j
public class InMemoryAdminTaskService implements AdminTaskService {

    private static final int MAX_TASKS = 100;
    private static final Duration RETENTION = Duration.ofHours(2);

    @Resource(name = "applicationTaskExecutor")
    private Executor executor;

    private final Map<UUID, TaskState> tasks = new ConcurrentHashMap<>();

    @Override
    public AdminTaskSubmission submit(
            String taskType, String requiredPermission, AdminTaskWork work) {
        if (taskType == null || taskType.isBlank()) {
            throw new IllegalArgumentException("taskType must not be blank");
        }
        if (requiredPermission == null || requiredPermission.isBlank()) {
            throw new IllegalArgumentException("requiredPermission must not be blank");
        }
        if (work == null) {
            throw new IllegalArgumentException("work must not be null");
        }
        cleanup();
        if (tasks.size() >= MAX_TASKS) {
            throw new BusinessException(
                    CommonErrorCode.TASK_LIMIT_REACHED);
        }
        UUID taskId = UUID.randomUUID();
        TaskState state = new TaskState(taskId, taskType, requiredPermission);
        tasks.put(taskId, state);
        state.publish(AdminTaskStatus.QUEUED, "任务已排队", 0, 0, null, null);
        log.info("管理端任务已提交, type={}, taskId={}, mode=in-memory", taskType, taskId);
        try {
            executor.execute(() -> execute(state, work));
        } catch (RuntimeException exception) {
            tasks.remove(taskId);
            log.warn("管理端任务提交失败, type={}, taskId={}, mode=in-memory",
                    taskType, taskId, exception);
            throw exception;
        }
        return new AdminTaskSubmission(taskId, taskType);
    }

    @Override
    public Optional<AdminTaskEvent> find(UUID taskId) {
        TaskState state = tasks.get(taskId);
        return state == null ? Optional.empty() : Optional.of(state.snapshot());
    }

    @Override
    public Optional<String> requiredPermission(UUID taskId) {
        TaskState state = tasks.get(taskId);
        return state == null ? Optional.empty() : Optional.of(state.requiredPermission);
    }

    @Override
    public Optional<UUID> findActive(String taskType) {
        return tasks.values().stream()
                .filter(state -> state.taskType.equals(taskType))
                .filter(state -> state.status == AdminTaskStatus.QUEUED
                        || state.status == AdminTaskStatus.RUNNING)
                .map(state -> state.taskId)
                .findFirst();
    }

    @Override
    public TaskSubscription subscribe(UUID taskId, Consumer<AdminTaskEvent> listener) {
        TaskState state = tasks.get(taskId);
        if (state == null) {
            throw new IllegalArgumentException("管理端任务不存在");
        }
        return state.subscribe(listener);
    }

    private void execute(TaskState state, AdminTaskWork work) {
        long startedAt = System.nanoTime();
        log.info("管理端任务开始执行, type={}, taskId={}, mode=in-memory",
                state.taskType, state.taskId);
        state.publish(AdminTaskStatus.RUNNING, "任务开始执行", 0, 0, null, null);
        try {
            Object result = work.execute((stage, message, current, total) ->
                    state.publish(AdminTaskStatus.RUNNING, message, current, total, null, stage));
            state.publish(AdminTaskStatus.COMPLETED, "任务执行完成", 1, 1, result, "COMPLETED");
            log.info("管理端任务执行完成, type={}, taskId={}, mode=in-memory, elapsedMs={}",
                    state.taskType, state.taskId, elapsedMillis(startedAt));
        } catch (Exception exception) {
            log.warn("管理端任务执行失败, type={}, taskId={}, mode=in-memory, elapsedMs={}",
                    state.taskType, state.taskId, elapsedMillis(startedAt), exception);
            TaskFailure failure = taskFailure(exception);
            state.publish(
                    AdminTaskStatus.FAILED,
                    failure.message(),
                    0,
                    0,
                    null,
                    "FAILED",
                    failure.code(),
                    failure.type());
        }
    }

    private static long elapsedMillis(long startedAt) {
        return Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
    }

    private void cleanup() {
        Instant threshold = Instant.now().minus(RETENTION);
        tasks.values().removeIf(state ->
                (state.status == AdminTaskStatus.COMPLETED
                        || state.status == AdminTaskStatus.FAILED)
                        && state.updatedAt.isBefore(threshold));
    }

    private static TaskFailure taskFailure(Throwable exception) {
        Throwable cause = unwrap(exception);
        if (cause instanceof BusinessException businessException) {
            return new TaskFailure(
                    truncate(businessException.getMessage()),
                    businessException.getCode(),
                    "BUSINESS");
        }
        if (cause instanceof SystemException systemException) {
            return new TaskFailure(
                    systemException.getDescription(),
                    systemException.getCode(),
                    "SYSTEM");
        }
        return new TaskFailure(
                CommonErrorCode.SYSTEM_ERROR.getMessage(),
                CommonErrorCode.SYSTEM_ERROR.getCode(),
                "SYSTEM");
    }

    private static String truncate(String message) {
        if (message == null || message.isBlank()) {
            return CommonErrorCode.SYSTEM_ERROR.getMessage();
        }
        return message.length() > 500 ? message.substring(0, 500) + "..." : message;
    }

    private static int percent(long current, long total) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.max(0, Math.min(100, current * 100 / total));
    }

    private static final class TaskState {
        private final ReentrantLock stateLock = new ReentrantLock();

        private final UUID taskId;
        private final String taskType;
        private final String requiredPermission;
        private final List<Consumer<AdminTaskEvent>> listeners = new ArrayList<>();
        private AdminTaskStatus status = AdminTaskStatus.QUEUED;
        private String stage = "QUEUED";
        private String message = "任务已排队";
        private long current;
        private long total;
        private int percent;
        private Object result;
        private String error;
        private Integer errorCode;
        private String errorType;
        private long lastSequence;
        private Instant updatedAt = Instant.now();

        private TaskState(UUID taskId, String taskType, String requiredPermission) {
            this.taskId = taskId;
            this.taskType = taskType;
            this.requiredPermission = requiredPermission;
        }

        private void publish(
                AdminTaskStatus status,
                String message,
                long current,
                long total,
                Object result,
                String stage) {
            publish(status, message, current, total, result, stage, null, null);
        }

        private void publish(
                AdminTaskStatus status,
                String message,
                long current,
                long total,
                Object result,
                String stage,
                Integer errorCode,
                String errorType) {
            AdminTaskEvent event;
            List<Consumer<AdminTaskEvent>> currentListeners;
            stateLock.lock();
            try {
                this.status = status;
                this.stage = stage == null || stage.isBlank() ? this.stage : stage;
                this.message = message;
                this.current = Math.max(0, current);
                this.total = Math.max(0, total);
                this.percent = status == AdminTaskStatus.COMPLETED
                        ? 100 : percent(this.current, this.total);
                this.result = result;
                this.error = status == AdminTaskStatus.FAILED ? message : null;
                this.errorCode = errorCode;
                this.errorType = errorType;
                this.updatedAt = Instant.now();
                event = snapshot(++lastSequence);
                currentListeners = List.copyOf(listeners);
            } finally {
                stateLock.unlock();
            }
            // SSE 写入可能阻塞或失败，不能占用任务状态锁影响其他订阅者和进度更新。
            for (Consumer<AdminTaskEvent> listener : currentListeners) {
                notifyListener(listener, event);
            }
        }

        private AdminTaskEvent snapshot() {
            stateLock.lock();
            try {
                return snapshot(lastSequence);
            } finally {
                stateLock.unlock();
            }
        }

        private AdminTaskEvent snapshot(long eventSequence) {
            return new AdminTaskEvent(
                    taskId,
                    taskType,
                    status,
                    stage,
                    message,
                    current,
                    total,
                    percent,
                    result,
                    error,
                    errorCode,
                    errorType,
                    eventSequence,
                    updatedAt);
        }

        private TaskSubscription subscribe(Consumer<AdminTaskEvent> listener) {
            if (listener == null) {
                throw new IllegalArgumentException("listener must not be null");
            }
            AdminTaskEvent current;
            stateLock.lock();
            try {
                listeners.add(listener);
                current = snapshot(lastSequence);
            } finally {
                stateLock.unlock();
            }
            notifyListener(listener, current);
            return () -> {
                stateLock.lock();
                try {
                    listeners.remove(listener);
                } finally {
                    stateLock.unlock();
                }
            };
        }

        /**
         * 单个浏览器断连只移除对应监听器，不能中断任务或其他 SSE 订阅者。
         */
        private void notifyListener(
                Consumer<AdminTaskEvent> listener, AdminTaskEvent event) {
            try {
                listener.accept(event);
            } catch (RuntimeException ignored) {
                stateLock.lock();
                try {
                    listeners.remove(listener);
                } finally {
                    stateLock.unlock();
                }
            }
        }
    }

    /**
     * 异步执行器和代理层可能会包裹业务异常；任务状态必须按真实异常类型输出。
     */
    static Throwable unwrap(Throwable exception) {
        Throwable current = exception;
        while (current instanceof CompletionException
                || current instanceof ExecutionException
                || current instanceof UndeclaredThrowableException) {
            Throwable cause = current.getCause();
            if (cause == null) {
                break;
            }
            current = cause;
        }
        return current;
    }

    private record TaskFailure(String message, int code, String type) {
    }
}
