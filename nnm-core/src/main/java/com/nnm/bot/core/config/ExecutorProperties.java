package com.nnm.bot.core.config;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "nnm.executor")
public class ExecutorProperties {

    private final Task task = new Task();
    private final Scheduler scheduler = new Scheduler();
    private final CommandIngress commandIngress = new CommandIngress();
    private final GatewayEvents gatewayEvents = new GatewayEvents();
    private final Computation computation = new Computation();
    private Duration awaitTermination = Duration.ofSeconds(30);
    /** 通用 I/O 虚拟线程执行器允许同时运行的业务任务数。 */
    private int applicationConcurrency = 64;
    private int discoveryConcurrency = 4;
    /** 建连与账号探测独立限流，避免业务线程等待同一执行器中的握手任务。 */
    private int connectionConcurrency = 4;

    public Task getTask() {
        return task;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public CommandIngress getCommandIngress() {
        return commandIngress;
    }

    public GatewayEvents getGatewayEvents() {
        return gatewayEvents;
    }

    public Computation getComputation() {
        return computation;
    }

    public Duration getAwaitTermination() {
        return awaitTermination;
    }

    public void setAwaitTermination(Duration awaitTermination) {
        this.awaitTermination = awaitTermination;
    }

    public int getApplicationConcurrency() {
        return applicationConcurrency;
    }

    public void setApplicationConcurrency(int applicationConcurrency) {
        this.applicationConcurrency = applicationConcurrency;
    }

    public int getDiscoveryConcurrency() {
        return discoveryConcurrency;
    }

    public void setDiscoveryConcurrency(int discoveryConcurrency) {
        this.discoveryConcurrency = discoveryConcurrency;
    }

    public int getConnectionConcurrency() {
        return connectionConcurrency;
    }

    public void setConnectionConcurrency(int connectionConcurrency) {
        this.connectionConcurrency = connectionConcurrency;
    }

    public void validate() {
        if (task.coreSize < 1) {
            throw new IllegalArgumentException("nnm.executor.task.core-size must be at least 1");
        }
        if (task.maxSize < task.coreSize) {
            throw new IllegalArgumentException(
                    "nnm.executor.task.max-size must be greater than or equal to core-size");
        }
        if (task.queueCapacity < 0) {
            throw new IllegalArgumentException(
                    "nnm.executor.task.queue-capacity must not be negative");
        }
        if (ObjectUtil.isNull(task.keepAlive) || task.keepAlive.isNegative()) {
            throw new IllegalArgumentException(
                    "nnm.executor.task.keep-alive must not be negative");
        }
        if (scheduler.poolSize < 1) {
            throw new IllegalArgumentException(
                    "nnm.executor.scheduler.pool-size must be at least 1");
        }
        if (ObjectUtil.isNull(awaitTermination) || awaitTermination.isNegative()) {
            throw new IllegalArgumentException(
                    "nnm.executor.await-termination must not be negative");
        }
        if (applicationConcurrency < 1) {
            throw new IllegalArgumentException(
                    "nnm.executor.application-concurrency must be at least 1");
        }
        if (discoveryConcurrency < 1) {
            throw new IllegalArgumentException(
                    "nnm.executor.discovery-concurrency must be at least 1");
        }
        if (connectionConcurrency < 1) {
            throw new IllegalArgumentException(
                    "nnm.executor.connection-concurrency must be at least 1");
        }
        if (commandIngress.queueCapacity < 1 || commandIngress.maxMessageBytes < 1
                || commandIngress.maxPendingBytes < commandIngress.maxMessageBytes
                || commandIngress.maxQueueWait == null
                || commandIngress.maxQueueWait.toMillis() < 1) {
            throw new IllegalArgumentException("Invalid nnm.executor.command-ingress capacity or timing");
        }
        if (gatewayEvents.concurrency < 1 || gatewayEvents.queueCapacity < 1) {
            throw new IllegalArgumentException("Invalid nnm.executor.gateway-events capacity");
        }
        if (computation.ocrInFlight < 1 || computation.maxQueueWait == null
                || computation.maxQueueWait.toMillis() < 1 || computation.resultTimeout == null
                || computation.resultTimeout.compareTo(computation.maxQueueWait) < 0) {
            throw new IllegalArgumentException("Invalid nnm.executor.computation capacity or timing");
        }
    }

    public static class Task {

        // 与下载、渲染共用 4GB 预算，默认不随开发机核心数无限放大。
        private int coreSize = Math.max(1, Math.min(2, Runtime.getRuntime().availableProcessors()));
        private int maxSize = coreSize;
        private int queueCapacity = 4;
        private Duration keepAlive = Duration.ofSeconds(60);

        public int getCoreSize() {
            return coreSize;
        }

        public void setCoreSize(int coreSize) {
            this.coreSize = coreSize;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public Duration getKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(Duration keepAlive) {
            this.keepAlive = keepAlive;
        }
    }

    public static class Scheduler {

        private int poolSize = 2;

        public int getPoolSize() {
            return poolSize;
        }

        public void setPoolSize(int poolSize) {
            this.poolSize = poolSize;
        }
    }

    @Getter
    @Setter
    public static class CommandIngress {
        /** 包含派发器暂存但尚未开始的任务，不包含正在执行的命令。 */
        private int queueCapacity = 32;
        private int maxMessageBytes = 32 * 1024;
        /** 编码载荷预留，不是 JVM 堆大小。 */
        private long maxPendingBytes = 2 * 1024 * 1024;
        private Duration maxQueueWait = Duration.ofSeconds(10);
    }

    @Getter
    @Setter
    public static class GatewayEvents {
        private int concurrency = 1;
        private int queueCapacity = 16;
    }

    @Getter
    @Setter
    public static class Computation {
        /** 完整 OCR 任务（排队与执行），不是原生引擎内部线程数。 */
        private int ocrInFlight = 1;
        private Duration maxQueueWait = Duration.ofSeconds(5);
        private Duration resultTimeout = Duration.ofSeconds(30);
    }
}
