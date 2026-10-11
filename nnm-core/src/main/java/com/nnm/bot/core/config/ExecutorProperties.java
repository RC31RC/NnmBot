package com.nnm.bot.core.config;

import cn.hutool.core.util.ObjectUtil;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "nnm.executor")
public class ExecutorProperties {

    private final Task task = new Task();
    private final Scheduler scheduler = new Scheduler();
    private Duration awaitTermination = Duration.ofSeconds(30);
    /** 通用 I/O 虚拟线程执行器允许同时运行的业务任务数。 */
    private int applicationConcurrency = 64;
    private int discoveryConcurrency = 4;
    /** 建连与账号探测独立限流，避免业务线程等待同一执行器中的握手任务。 */
    private int connectionConcurrency = 4;
    /** 社区来源下载与解析共用小并发预算，不在工作线程中访问数据库。 */
    private int communitySourceConcurrency = 2;
    private int communitySourceQueueCapacity = 8;

    public Task getTask() {
        return task;
    }

    public Scheduler getScheduler() {
        return scheduler;
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

    public int getCommunitySourceConcurrency() {
        return communitySourceConcurrency;
    }

    public void setCommunitySourceConcurrency(int communitySourceConcurrency) {
        this.communitySourceConcurrency = communitySourceConcurrency;
    }

    public int getCommunitySourceQueueCapacity() {
        return communitySourceQueueCapacity;
    }

    public void setCommunitySourceQueueCapacity(int communitySourceQueueCapacity) {
        this.communitySourceQueueCapacity = communitySourceQueueCapacity;
    }

    void validate() {
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
        if (communitySourceConcurrency < 1 || communitySourceQueueCapacity < 0) {
            throw new IllegalArgumentException(
                    "nnm.executor.community-source-concurrency must be positive "
                            + "and community-source-queue-capacity must not be negative");
        }
    }

    public static class Task {

        // 使用 JVM 可见的 CPU 数；容器部署需正确设置 CPU 配额。
        private int coreSize = Math.max(1, Runtime.getRuntime().availableProcessors());
        private int maxSize = coreSize;
        private int queueCapacity = 100;
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
}
