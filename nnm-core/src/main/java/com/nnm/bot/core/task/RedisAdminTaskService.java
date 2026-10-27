package com.nnm.bot.core.task;

import com.nnm.bot.common.constant.TaskConstants.AdminTaskStatus;

import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.exception.DataNotFoundException;
import com.nnm.bot.common.task.*;
import com.nnm.bot.core.config.ClusterProperties;
import io.github.rdt.state.SharedStateStore;
import io.github.rdt.exception.RdtException;
import io.github.rdt.exception.RdtErrorCode;
import jakarta.annotation.Resource;
import java.lang.reflect.UndeclaredThrowableException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

/** Share snapshots, not Java closures. A crashed worker is reported as failed, never replayed blindly. */
@Slf4j
@Service
public class RedisAdminTaskService implements AdminTaskService {
    @Resource
    private SharedStateStore stateStore;
    @Resource
    private AdminTaskCoordination coordination;
    @Resource
    private ClusterProperties properties;
    @Resource(name = "applicationTaskExecutor")
    private Executor executor;
    @Resource(name = "taskScheduler")
    private ThreadPoolTaskScheduler scheduler;
    private final AtomicInteger subscribers = new AtomicInteger();

    @Override
    public AdminTaskSubmission submit(String type, String permission, AdminTaskWork work) {
        if (type == null || type.isBlank() || permission == null || permission.isBlank() || work == null) {
            throw new IllegalArgumentException("Task type, permission and work are required");
        }
        // Submit outside the distributed lock: the bounded executor can block the submitting thread.
        Reservation reservation = coordination.reserve(type, () -> {
            Optional<UUID> active = findActive(type);
            if (active.isPresent()) return new Reservation(active.get(), false);
            UUID id = UUID.randomUUID();
            save(new StoredTask(permission, Instant.now().plus(properties.taskTimeout()),
                    event(id, type, AdminTaskStatus.QUEUED, "QUEUED", "任务已排队", 0, 0, null, null, null, 1)));
            stateStore.put("admin-task-active:" + type, id, properties.taskTimeout().plus(properties.taskRetention()));
            log.info("管理端任务已提交, type={}, taskId={}, mode=redis", type, id);
            return new Reservation(id, true);
        });
        if (reservation.created()) {
            try {
                executor.execute(() -> execute(reservation.id(), work));
            } catch (RuntimeException exception) {
                log.warn("管理端任务提交失败, type={}, taskId={}, mode=redis",
                        type, reservation.id(), exception);
                fail(reservation.id(), exception);
                throw exception;
            }
        }
        return new AdminTaskSubmission(reservation.id(), type);
    }

    @Override
    public Optional<AdminTaskEvent> find(UUID id) {
        return load(id).map(this::expireIfNecessary).map(StoredTask::event);
    }

    @Override
    public Optional<String> requiredPermission(UUID id) {
        return load(id).map(StoredTask::permission);
    }

    @Override
    public Optional<UUID> findActive(String type) {
        return stateStore.<UUID>get("admin-task-active:" + type, UUID.class)
                .flatMap(this::find).filter(RedisAdminTaskService::active).map(AdminTaskEvent::taskId);
    }

    @Override
    public TaskSubscription subscribe(UUID id, Consumer<AdminTaskEvent> listener) {
        if (listener == null) throw new IllegalArgumentException("listener is required");
        if (find(id).isEmpty()) throw new DataNotFoundException("任务不存在或已过期");
        if (subscribers.incrementAndGet() > properties.maxTaskSubscriptions()) {
            subscribers.decrementAndGet();
            throw new BusinessException(CommonErrorCode.TASK_LIMIT_REACHED);
        }
        ProgressSubscription subscription = new ProgressSubscription(id, listener);
        try {
            subscription.poll();
            subscription.start();
            return subscription::close;
        } catch (RuntimeException exception) {
            subscription.close();
            throw exception;
        }
    }

    private void execute(UUID id, AdminTaskWork work) {
        long startedAt = System.nanoTime();
        String taskType = load(id).map(task -> task.event().taskType()).orElse("unknown");
        log.info("管理端任务开始执行, type={}, taskId={}, mode=redis", taskType, id);
        try {
            boolean completed = executeWork(id, work);
            if (completed) {
                log.info("管理端任务执行完成, type={}, taskId={}, mode=redis, elapsedMs={}",
                        taskType, id, elapsedMillis(startedAt));
            } else {
                log.warn("管理端任务未获得执行锁, type={}, taskId={}, mode=redis, elapsedMs={}",
                        taskType, id, elapsedMillis(startedAt));
            }
        } catch (Exception exception) {
            try { fail(id, exception); }
            catch (RuntimeException unavailable) {
                log.warn("Cannot persist failure; task will time out: {}", id, unavailable);
            }
            log.warn("管理端任务执行失败, type={}, taskId={}, mode=redis, elapsedMs={}",
                    taskType, id, elapsedMillis(startedAt), exception);
        }
    }

    private boolean executeWork(UUID id, AdminTaskWork work) throws Exception {
        StoredTask task = load(id).orElseThrow();
        try {
            coordination.execute(task.event().taskType(), () -> {
                update(id, AdminTaskStatus.RUNNING, "RUNNING", "任务开始执行", 0, 0, null, null, null);
                Object result = work.execute((stage, message, current, total) ->
                        update(id, AdminTaskStatus.RUNNING, stage, message, current, total, null, null, null));
                update(id, AdminTaskStatus.COMPLETED, "COMPLETED", "任务执行完成", 1, 1, result, null, null);
                return null;
            });
            return true;
        } catch (RdtException exception) {
            if (exception.code() != RdtErrorCode.LOCK_TIMEOUT) throw exception;
            fail(id, new BusinessException(CommonErrorCode.TASK_LIMIT_REACHED));
            return false;
        }
    }

    private static Throwable unwrap(Throwable failure) {
        Throwable current = failure;
        while ((current instanceof CompletionException
                || current instanceof ExecutionException
                || current instanceof UndeclaredThrowableException)
                && current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private void fail(UUID id, Exception exception) {
        log.warn("Admin task failed: {}", id, exception);
        Throwable cause = unwrap(exception);
        int code = CommonErrorCode.SYSTEM_ERROR.getCode();
        String message = CommonErrorCode.SYSTEM_ERROR.getMessage();
        String type = "SYSTEM";
        if (cause instanceof BusinessException business) {
            code = business.getCode();
            message = business.getMessage();
            type = "BUSINESS";
        } else if (cause instanceof SystemException system) {
            code = system.getCode();
            message = system.getDescription();
        }
        update(id, AdminTaskStatus.FAILED, "FAILED", message, 0, 0, null, code, type);
    }

    private void update(UUID id, AdminTaskStatus status, String stage, String message,
                                     long current, long total, Object result, Integer code, String errorType) {
        StoredTask task = load(id).orElseThrow();
        coordination.reserve(task.event().taskType(), () -> {
            StoredTask latest = load(id).orElseThrow();
            StoredTask currentTask = expireIfNecessary(latest);
            if (!active(currentTask.event())) {
                return null;
            }
            Instant deadline = Instant.now().plus(properties.taskTimeout());
            AdminTaskEvent nextEvent = event(id, currentTask.event().taskType(), status, stage,
                    message, current, total, result, code, errorType,
                    currentTask.event().sequence() + 1);
            save(new StoredTask(currentTask.permission(), deadline, nextEvent));
            if (active(status)) {
                refreshActive(currentTask.event().taskType(), id);
            } else {
                clearActive(currentTask.event().taskType(), id);
            }
            return null;
        });
    }

    private StoredTask expireIfNecessary(StoredTask task) {
        if (!active(task.event()) || !Instant.now().isAfter(task.deadline())) {
            return task;
        }
        return coordination.reserve(task.event().taskType(), () -> {
            StoredTask latest = load(task.event().taskId()).orElse(task);
            if (!active(latest.event()) || !Instant.now().isAfter(latest.deadline())) {
                return latest;
            }
            AdminTaskEvent timeout = event(latest.event().taskId(), latest.event().taskType(),
                    AdminTaskStatus.FAILED, "TIMED_OUT",
                    "执行节点失联或任务超过时限，请核对业务结果后重试", 0, 0, null,
                    CommonErrorCode.SYSTEM_ERROR.getCode(), "SYSTEM",
                    latest.event().sequence() + 1);
            StoredTask expired = new StoredTask(latest.permission(), latest.deadline(), timeout);
            save(expired);
            clearActive(latest.event().taskType(), latest.event().taskId());
            log.warn("管理端任务已超时, type={}, taskId={}, deadline={}",
                    latest.event().taskType(), latest.event().taskId(), latest.deadline());
            return expired;
        });
    }

    private void clearActive(String type, UUID taskId) {
        if (stateStore.<UUID>get("admin-task-active:" + type, UUID.class).filter(taskId::equals).isPresent()) {
            stateStore.evict("admin-task-active:" + type);
        }
    }

    private void refreshActive(String type, UUID taskId) {
        stateStore.put("admin-task-active:" + type, taskId,
                properties.taskTimeout().plus(properties.taskRetention()));
    }

    private Optional<StoredTask> load(UUID id) {
        return stateStore.get("admin-task:" + id, StoredTask.class);
    }

    private void save(StoredTask task) {
        stateStore.put("admin-task:" + task.event().taskId(), task,
                properties.taskRetention().plus(properties.taskTimeout()));
    }

    private static long elapsedMillis(long startedAt) {
        return Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
    }

    private static boolean active(AdminTaskEvent event) {
        return event.status() == AdminTaskStatus.QUEUED || event.status() == AdminTaskStatus.RUNNING;
    }

    private static boolean active(AdminTaskStatus status) {
        return status == AdminTaskStatus.QUEUED || status == AdminTaskStatus.RUNNING;
    }

    private static AdminTaskEvent event(UUID id, String type, AdminTaskStatus status, String stage,
            String message, long current, long total, Object result, Integer code, String errorType, long sequence) {
        int percent = total <= 0 ? 0 : (int) Math.max(0, Math.min(100, 100.0 * current / total));
        return new AdminTaskEvent(id, type, status, stage, message, Math.max(0, current), Math.max(0, total),
                status == AdminTaskStatus.COMPLETED ? 100 : percent, result,
                status == AdminTaskStatus.FAILED ? message : null, code, errorType, sequence, Instant.now());
    }

    public record StoredTask(String permission, Instant deadline, AdminTaskEvent event) { }
    private record Reservation(UUID id, boolean created) { }

    private final class ProgressSubscription {
        private final ReentrantLock subscriptionLock = new ReentrantLock();

        private final UUID id;
        private final Consumer<AdminTaskEvent> listener;
        private long sequence = -1;
        private boolean closed;
        private ScheduledFuture<?> future;
        private ProgressSubscription(UUID id, Consumer<AdminTaskEvent> listener) {
            this.id = id;
            this.listener = listener;
        }
        private void start() {
            subscriptionLock.lock();
            try {
                if (!closed) future = scheduler.scheduleWithFixedDelay(this::poll, properties.taskPollInterval());
            } finally {
                subscriptionLock.unlock();
            }
        }
        private void poll() {
            subscriptionLock.lock();
            try {
                if (closed) return;
                try {
                    find(id).ifPresent(event -> {
                        if (event.sequence() > sequence) {
                            sequence = event.sequence();
                            listener.accept(event);
                        }
                        if (!active(event)) close();
                    });
                } catch (RuntimeException exception) {
                    log.debug("Task progress polling failed: {}", id, exception);
                }
            } finally {
                subscriptionLock.unlock();
            }
        }
        private void close() {
            subscriptionLock.lock();
            try {
                if (closed) return;
                closed = true;
                if (future != null) future.cancel(false);
                subscribers.decrementAndGet();
            } finally {
                subscriptionLock.unlock();
            }
        }
    }
}
