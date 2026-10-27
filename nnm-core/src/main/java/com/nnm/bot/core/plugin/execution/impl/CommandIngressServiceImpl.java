package com.nnm.bot.core.plugin.execution.impl;

import com.nnm.bot.core.constant.CommandExecutionConstants.IngressState;

import com.nnm.bot.core.config.ExecutorProperties.CommandIngress;
import java.util.Map.Entry;
import com.nnm.bot.core.config.ExecutorProperties;
import com.nnm.bot.core.config.CommandRoutingProperties;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.execution.CommandDispatcher;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandIngressResult;
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
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommandIngressServiceImpl implements CommandIngressService, SmartLifecycle {

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
            throw new IllegalArgumentException("Receipt retention must cover command queue wait and correlation window");
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
        event("received");
        long bytes = payloadBytes(context);
        if (bytes > properties.getCommandIngress().getMaxMessageBytes()) {
            return rejected(CommandIngressResult.REJECTED_TOO_LARGE, context, null);
        }
        CommandMatch match;
        try {
            match = registry.match(context).orElse(null);
        } catch (RuntimeException failure) {
            event("match_failed");
            throw failure;
        }
        if (match == null) {
            event("unmatched");
            return CommandIngressResult.NOT_MATCHED;
        }
        stateLock.lock();
        try {
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
                throw new IllegalStateException("Command queue and admission accounting diverged");
            }
            pending.add(task);
            waiting++;
            reservedBytes += bytes;
            event("accepted");
            return CommandIngressResult.ACCEPTED;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public void start() {
        stateLock.lock();
        try {
            if (accepting) return;
            if (dispatchThread != null && dispatchThread.isAlive()) {
                throw new IllegalStateException("Previous command dispatcher is still stopping");
            }
            stopping = false;
            accepting = true;
            // 独立生命周期线程可以等待应用容量，绝不占用它所提交的应用执行额度。
            dispatchThread = Thread.ofPlatform().daemon().name("nnm-command-ingress").start(this::drain);
        } finally {
            stateLock.unlock();
        }
    }

    private void drain() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                stateLock.lock();
                try {
                    if (stopping && waiting == 0) return;
                } finally {
                    stateLock.unlock();
                }
                PendingCommand task = queue.poll(100, TimeUnit.MILLISECONDS);
                if (task == null) continue;
                stateLock.lock();
                try {
                    if (task.state != IngressState.QUEUED) continue;
                    task.state = IngressState.DISPATCHING;
                    dispatching++;
                } finally {
                    stateLock.unlock();
                }
                try {
                    applicationExecutor.execute(() -> execute(task));
                } catch (TaskRejectedException failure) {
                    finish(task, "submission_rejected");
                    if (!isStopping()) throw failure;
                }
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        } finally {
            stateLock.lock();
            try {
                accepting = false;
                stopping = true;
                cancelWaiting();
                drained.signalAll();
            } finally {
                stateLock.unlock();
            }
        }
    }

    private void execute(PendingCommand task) {
        long started = System.nanoTime();
        stateLock.lock();
        try {
            if (task.state != IngressState.DISPATCHING) return;
            if (started - task.submitted >= properties.getCommandIngress().getMaxQueueWait().toNanos()) {
                finish(task, "expired");
                return;
            }
            task.state = IngressState.RUNNING;
            waiting--;
            dispatching--;
            running++;
        } finally {
            stateLock.unlock();
        }
        timing("wait", started - task.submitted);
        try {
            if (!task.connectionValid.getAsBoolean()) {
                event("connection_invalid");
                return;
            }
            dispatcher.dispatch(task.match);
            event("completed");
        } catch (RuntimeException | Error failure) {
            event("failed");
            throw failure;
        } finally {
            timing("execution", System.nanoTime() - started);
            finish(task, "terminated");
        }
    }

    private void finish(PendingCommand task, String outcome) {
        stateLock.lock();
        try {
            if (task.state == IngressState.TERMINATED) return;
            if (task.state == IngressState.RUNNING) running--;
            else waiting--;
            if (task.state == IngressState.DISPATCHING) dispatching--;
            task.state = IngressState.TERMINATED;
            pending.remove(task);
            reservedBytes -= task.bytes;
            event(outcome);
            drained.signalAll();
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public void stop() {
        long deadline = System.nanoTime() + properties.getAwaitTermination().toNanos();
        Thread worker;
        stateLock.lock();
        try {
            accepting = false;
            stopping = true;
            worker = dispatchThread;
            try {
                long remaining;
                while (!pending.isEmpty() && (remaining = deadline - System.nanoTime()) > 0) {
                    drained.awaitNanos(remaining);
                }
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while draining command ingress", interrupted);
            } finally {
                cancelWaiting();
                if (worker != null) worker.interrupt();
            }
        } finally {
            stateLock.unlock();
        }
    }

    private void cancelWaiting() {
        queue.clear();
        for (PendingCommand task : new ArrayList<>(pending)) {
            if (task.state != IngressState.RUNNING) finish(task, "cancelled");
        }
    }

    private boolean isStopping() {
        stateLock.lock();
        try {
            return stopping;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public boolean isRunning() {
        stateLock.lock();
        try {
            return accepting;
        } finally {
            stateLock.unlock();
        }
    }

    public Snapshot snapshot() {
        stateLock.lock();
        try {
            return new Snapshot(waiting - dispatching, dispatching, running, pending.size(), reservedBytes);
        } finally {
            stateLock.unlock();
        }
    }

    private CommandIngressResult rejected(CommandIngressResult result, BotMessageContext context, CommandMatch match) {
        event(result.name().toLowerCase(Locale.ROOT));
        log.debug("Command ingress rejected platform={} account={} messageId={} command={} reason={}",
                context.platform(), context.botAccountId(), context.messageId(),
                match == null ? null : match.invocation().commandCode(), result);
        return result;
    }

    private void event(String outcome) {
        if (meters != null) meters.counter("nnm.command.ingress.events", "outcome", outcome).increment();
    }

    private void timing(String phase, long nanos) {
        if (meters != null) meters.timer("nnm.command.ingress.duration", "phase", phase)
                .record(Duration.ofNanos(nanos));
    }

    private long payloadBytes(BotMessageContext context) {
        // UTF-16 每字符最多预留 3 个 UTF-8 字节；原生来源仅保留发送定位信息。
        long characters = length(context.rawMessage()) * 2 + length(context.message())
                + length(context.botAccountId()) + length(context.senderId())
                + length(context.conversationId()) + length(context.messageId());
        for (BotIncomingAttachment attachment : context.attachments()) {
            characters += length(attachment.fileName()) + length(attachment.contentType())
                    + length(attachment.url()) + length(attachment.fileId());
            for (Entry<String,String> entry : attachment.metadata().entrySet()) {
                characters += length(entry.getKey()) + length(entry.getValue());
            }
        }
        return 512 + characters * 3 + context.attachments().size() * 128L;
    }

    private static long length(String value) {
        return value == null ? 0 : value.length();
    }

    public record Snapshot(int queued, int dispatching, int running, int inFlight, long reservedBytes) { }

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
