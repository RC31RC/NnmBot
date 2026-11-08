package com.nnm.bot.core.plugin.execution.impl;

import com.nnm.bot.core.constant.CommandExecutionConstants.IngressEvent;
import com.nnm.bot.core.constant.CommandExecutionConstants.IngressPhase;
import com.nnm.bot.core.message.cache.MessageReferenceCache;

import com.nnm.bot.core.constant.CommandExecutionConstants.IngressState;

import com.nnm.bot.core.config.ExecutorProperties.CommandIngress;
import java.util.Map.Entry;
import com.nnm.bot.core.config.ExecutorProperties;
import com.nnm.bot.core.config.CommandRoutingProperties;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.execution.CommandDispatcher;
import com.nnm.bot.core.plugin.execution.CommandCall;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandIngressResult;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandErrorCode;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.plugin.execution.CommandIngressService;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

/** 本机命令准入与有界派发；短状态锁内不执行匹配、背压等待或外部 I/O。 */
@Slf4j
@Service
public class CommandIngressServiceImpl implements CommandIngressService, SmartLifecycle {

    /** 队列轮询只用于响应停机，不承担业务等待超时。 */
    private static final long QUEUE_POLL_MILLIS = 100;
    /** 调用状态和集合节点的保守估算基数，单位字节。 */
    private static final long MESSAGE_OVERHEAD_BYTES = 512;
    /** 每个附件对象及索引的保守估算，单位字节。 */
    private static final long ATTACHMENT_OVERHEAD_BYTES = 128;
    /** 每个 UTF-16 字符对应的 UTF-8 最多预留字节。 */
    private static final int UTF8_BYTES_PER_CHARACTER = 3;

    /** 非命令图片只登记有界摘要，供后续显式引用使用。 */
    @Resource
    private MessageReferenceCache referenceCache;
    @Resource
    private ExecutorProperties properties;
    @Resource
    private CommandRoutingProperties routingProperties;
    @Resource
    private CommandRegistry registry;
    @Resource
    private CommandDispatcher dispatcher;
    @Resource(name = "applicationTaskExecutor")
    private AsyncTaskExecutor applicationExecutor;
    @Resource
    private ObjectProvider<MeterRegistry> meterRegistryProvider;

    // 接纳、任务状态与关闭共用一把短锁；锁内不匹配、不提交执行器、不执行业务。
    private final ReentrantLock stateLock = new ReentrantLock();
    private final Condition drained = stateLock.newCondition();
    private final Set<PendingCommand> pending = new HashSet<>();
    private ArrayBlockingQueue<PendingCommand> queue;
    private MeterRegistry meters;
    private Thread dispatchThread;
    private boolean accepting;
    private boolean stopping;
    private int waiting;
    private int running;
    private int dispatching;
    private long reservedBytes;

    @PostConstruct
    public void initialize() {
        properties.validate();
        if (routingProperties.receiptRetention().compareTo(
            properties.getCommandIngress().getMaxQueueWait().plus(routingProperties.correlationWindow())) < 0) {
            throw new IllegalArgumentException("回执保留时长必须覆盖命令排队与关联窗口");
        }
        queue = new ArrayBlockingQueue<>(properties.getCommandIngress().getQueueCapacity());
        meters = meterRegistryProvider.getIfAvailable();
        if (meters != null) {
            meters.gauge("nnm.command.ingress.queued", this, value -> value.snapshot().queued());
            meters.gauge("nnm.command.ingress.dispatching", this, value -> value.snapshot().dispatching());
            meters.gauge("nnm.command.ingress.running", this, value -> value.snapshot().running());
            meters.gauge("nnm.command.ingress.pending.bytes", this, value -> value.snapshot().reservedBytes());
        }
    }

    @Override
    public CommandIngressResult accept(BotMessageContext context, BooleanSupplier connectionValid) {
        Objects.requireNonNull(context, "context must not be null");
        Objects.requireNonNull(connectionValid, "connectionValid must not be null");
        event(IngressEvent.RECEIVED);
        long bytes = payloadBytes(context);
        if (bytes > properties.getCommandIngress().getMaxMessageBytes()) {
            return rejected(CommandIngressResult.REJECTED_TOO_LARGE, context, null);
        }
        referenceCache.observe(context);
        CommandMatch match = matchObserved(context);
        if (match == null) {
            event(IngressEvent.UNMATCHED);
            return CommandIngressResult.NOT_MATCHED;
        }
        return withStateLock(() -> {
            if (!accepting) {
                return rejected(CommandIngressResult.REJECTED_SHUTDOWN, context, match);
            }
            CommandIngress limits = properties.getCommandIngress();
            if (waiting >= limits.getQueueCapacity()
                || pending.size() >= properties.getApplicationConcurrency() + limits.getQueueCapacity()
                || bytes > limits.getMaxPendingBytes() - reservedBytes) {
                return rejected(CommandIngressResult.REJECTED_CAPACITY, context, match);
            }
            PendingCommand task = new PendingCommand(match, connectionValid, bytes);
            if (!queue.offer(task)) {
                throw new SystemException(CommandErrorCode.INGRESS_STATE_INVALID, "命令队列与准入容量状态不一致");
            }
            pending.add(task);
            waiting++;
            reservedBytes += bytes;
            event(IngressEvent.ACCEPTED);
            return CommandIngressResult.ACCEPTED;
        });
    }

    @Override
    public void start() {
        withStateLock(() -> {
            if (accepting)
                return;
            if (dispatchThread != null && dispatchThread.isAlive()) {
                throw new SystemException(CommandErrorCode.INGRESS_STATE_INVALID, "上一个命令派发器尚未停止");
            }
            stopping = false;
            accepting = true;
            // 独立生命周期线程可以等待应用容量，绝不占用它所提交的应用执行额度。
            dispatchThread = Thread.ofPlatform().daemon().name("nnm-command-ingress").start(this::drain);
        });
    }

    private void drain() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (withStateLock(() -> stopping && waiting == 0))
                    return;
                PendingCommand task = queue.poll(QUEUE_POLL_MILLIS, TimeUnit.MILLISECONDS);
                if (task == null)
                    continue;
                if (!markDispatching(task))
                    continue;
                try {
                    applicationExecutor.execute(() -> execute(task));
                } catch (TaskRejectedException failure) {
                    finish(task, IngressEvent.SUBMISSION_REJECTED);
                    if (!isStopping())
                        throw failure;
                }
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        } catch (RuntimeException failure) {
            log.error("命令派发线程异常退出", failure);
            throw failure;
        } finally {
            withStateLock(() -> {
                accepting = false;
                stopping = true;
                cancelWaiting();
                drained.signalAll();
            });
        }
    }

    private void execute(PendingCommand task) {
        long started = System.nanoTime();
        if (!markRunning(task, started))
            return;
        timing(IngressPhase.WAIT, started - task.submitted);
        IngressEvent outcome = IngressEvent.FAILED;
        try {
            if (!task.connectionValid.getAsBoolean()) {
                outcome = IngressEvent.CONNECTION_INVALID;
                return;
            }
            dispatcher.dispatch(CommandCall.text(task.match));
            outcome = IngressEvent.COMPLETED;
        } catch (RuntimeException failure) {
            // 线程池可能将任务异常留在 Future 中；入口负责记录，不能在这里补发机器人回复。
            log.error("命令执行任务失败 command={} messageId={}", task.match.invocation().commandCode(),
                task.match.invocation().context().messageId(), failure);
            throw failure;
        } finally {
            long elapsed = System.nanoTime() - started;
            // 容量归还不依赖指标记录成功；执行异常直接向任务边界传播。
            finish(task, IngressEvent.TERMINATED);
            timing(IngressPhase.EXECUTION, elapsed);
            event(outcome);
        }
    }

    /** 在短锁内结束一次任务；持锁调用方使用 finishLocked 避免隐藏重入。 */
    private void finish(PendingCommand task, IngressEvent outcome) {
        withStateLock(() -> finishLocked(task, outcome));
    }

    /** 调用方必须持有 stateLock，状态与容量一并归还。 */
    private void finishLocked(PendingCommand task, IngressEvent outcome) {
        if (task.state == IngressState.TERMINATED)
            return;
        if (task.state == IngressState.RUNNING)
            running--;
        else
            waiting--;
        if (task.state == IngressState.DISPATCHING)
            dispatching--;
        task.state = IngressState.TERMINATED;
        pending.remove(task);
        reservedBytes -= task.bytes;
        event(outcome);
        drained.signalAll();
    }

    /** 从队列取出只标记派发，不在持锁时提交可能背压的执行器。 */
    private boolean markDispatching(PendingCommand task) {
        return withStateLock(() -> {
            if (task.state != IngressState.QUEUED)
                return false;
            task.state = IngressState.DISPATCHING;
            dispatching++;
            return true;
        });
    }

    /** 排队已超时则直接归还容量，只有有效派发可以转为运行。 */
    private boolean markRunning(PendingCommand task, long started) {
        return withStateLock(() -> {
            if (task.state != IngressState.DISPATCHING)
                return false;
            if (started - task.submitted >= properties.getCommandIngress().getMaxQueueWait().toNanos()) {
                finishLocked(task, IngressEvent.EXPIRED);
                return false;
            }
            task.state = IngressState.RUNNING;
            waiting--;
            dispatching--;
            running++;
            return true;
        });
    }

    @Override
    public void stop() {
        long deadline = System.nanoTime() + properties.getAwaitTermination().toNanos();
        withStateLock(() -> stopAndAwaitLocked(deadline));
    }

    /** Condition 暂时释放状态锁；唤醒后重新检查，线程中断时仍完成等待任务清理。 */
    private void stopAndAwaitLocked(long deadline) {
        accepting = false;
        stopping = true;
        Thread worker = dispatchThread;
        try {
            long remaining;
            while (!pending.isEmpty() && (remaining = deadline - System.nanoTime()) > 0) {
                drained.awaitNanos(remaining);
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new SystemException(CommandErrorCode.INGRESS_INTERRUPTED, "等待命令任务结束时被中断", interrupted);
        } finally {
            cancelWaiting();
            if (worker != null)
                worker.interrupt();
        }
    }

    private void cancelWaiting() {
        queue.clear();
        for (PendingCommand task : new ArrayList<>(pending)) {
            if (task.state != IngressState.RUNNING)
                finishLocked(task, IngressEvent.CANCELLED);
        }
    }

    private boolean isStopping() {
        return withStateLock(() -> stopping);
    }

    @Override
    public boolean isRunning() {
        return withStateLock(() -> accepting);
    }

    public Snapshot snapshot() {
        return withStateLock(
            () -> new Snapshot(waiting - dispatching, dispatching, running, pending.size(), reservedBytes));
    }

    /** 仅在匹配观测边界记录失败，不让基础设施捕获散落在接纳逻辑中。 */
    private CommandMatch matchObserved(BotMessageContext context) {
        try {
            return registry.match(context).orElse(null);
        } catch (RuntimeException failure) {
            event(IngressEvent.MATCH_FAILED);
            log.error("命令匹配失败 platform={} account={} messageId={}", context.platform(), context.botAccountId(),
                context.messageId(), failure);
            throw failure;
        }
    }

    /** 短状态读取或迁移的唯一锁边界。 */
    private <T> T withStateLock(Supplier<T> action) {
        stateLock.lock();
        try {
            return action.get();
        } finally {
            stateLock.unlock();
        }
    }

    /** 无返回值的状态迁移沿用同一个锁边界。 */
    private void withStateLock(Runnable action) {
        withStateLock(() -> {
            action.run();
            return null;
        });
    }

    private CommandIngressResult rejected(CommandIngressResult result, BotMessageContext context, CommandMatch match) {
        event(switch (result) {
            case REJECTED_CAPACITY -> IngressEvent.REJECTED_CAPACITY;
            case REJECTED_SHUTDOWN -> IngressEvent.REJECTED_SHUTDOWN;
            case REJECTED_TOO_LARGE -> IngressEvent.REJECTED_TOO_LARGE;
            default -> throw new SystemException(CommandErrorCode.INGRESS_STATE_INVALID, "非拒绝状态不能登记为入口拒绝: " + result);
        });
        log.debug("Command ingress rejected platform={} account={} messageId={} command={} reason={}",
            context.platform(), context.botAccountId(), context.messageId(),
            match == null ? null : match.invocation().commandCode(), result);
        return result;
    }

    private void event(IngressEvent outcome) {
        if (meters != null)
            meters.counter("nnm.command.ingress.events", "outcome", outcome.tag()).increment();
    }

    private void timing(IngressPhase phase, long nanos) {
        if (meters != null)
            meters.timer("nnm.command.ingress.duration", "phase", phase.tag()).record(Duration.ofNanos(nanos));
    }

    private long payloadBytes(BotMessageContext context) {
        // UTF-16 每字符最多预留 3 个 UTF-8 字节；原生来源仅保留发送定位信息。
        long characters = length(context.rawMessage()) * 2 + length(context.message()) + length(context.botAccountId())
            + length(context.senderId()) + length(context.conversationId()) + length(context.messageId());
        for (BotIncomingAttachment attachment : context.attachments()) {
            characters += length(attachment.fileName()) + length(attachment.contentType()) + length(attachment.url())
                + length(attachment.fileId());
            for (Entry<String, String> entry : attachment.metadata().entrySet()) {
                characters += length(entry.getKey()) + length(entry.getValue());
            }
        }
        if (context.reference() != null) {
            characters += length(context.reference().id());
            for (BotIncomingAttachment attachment : context.reference().attachments()) {
                characters += attachment.toString().length();
            }
        }
        return MESSAGE_OVERHEAD_BYTES + characters * UTF8_BYTES_PER_CHARACTER
            + context.attachments().size() * ATTACHMENT_OVERHEAD_BYTES;
    }

    private static long length(String value) {
        return value == null ? 0 : value.length();
    }

    public record Snapshot(int queued, int dispatching, int running, int inFlight, long reservedBytes) {
    }

    private static final class PendingCommand {
        private final CommandMatch match;
        private final BooleanSupplier connectionValid;
        private final long bytes;
        private final long submitted = System.nanoTime();
        private IngressState state = IngressState.QUEUED;

        private PendingCommand(CommandMatch match, BooleanSupplier connectionValid, long bytes) {
            this.match = match;
            this.connectionValid = connectionValid;
            this.bytes = bytes;
        }
    }
}
