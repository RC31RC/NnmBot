package com.nnm.bot.core.config;

import cn.hutool.core.util.ObjectUtil;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "nnm.executor")
public class ExecutorProperties {

    private final Task task = new Task();
    private final Scheduler scheduler = new Scheduler();
    private Duration awaitTermination = Duration.ofSeconds(30);
    private int discoveryConcurrency = 4;

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

    public int getDiscoveryConcurrency() {
        return discoveryConcurrency;
    }

    public void setDiscoveryConcurrency(int discoveryConcurrency) {
        this.discoveryConcurrency = discoveryConcurrency;
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
        if (discoveryConcurrency < 1) {
            throw new IllegalArgumentException(
                    "nnm.executor.discovery-concurrency must be at least 1");
        }
    }

    public static class Task {

        private int coreSize = 2;
        private int maxSize = 8;
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
