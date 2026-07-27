package com.nnm.bot.core.config;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExecutorConfigTest {

    private final ExecutorConfig config = new ExecutorConfig();

    @Test
    void createsBoundedTaskExecutorWithBackpressure() {
        ExecutorProperties properties = new ExecutorProperties();
        properties.getTask().setCoreSize(1);
        properties.getTask().setMaxSize(3);
        properties.getTask().setQueueCapacity(7);

        ThreadPoolTaskExecutor executor = config.taskExecutor(properties);
        executor.initialize();
        try {
            ThreadPoolExecutor delegate = executor.getThreadPoolExecutor();
            assertThat(delegate.getCorePoolSize()).isEqualTo(1);
            assertThat(delegate.getMaximumPoolSize()).isEqualTo(3);
            assertThat(delegate.getQueue().remainingCapacity()).isEqualTo(7);
            assertThat(delegate.getRejectedExecutionHandler())
                    .isInstanceOf(ThreadPoolExecutor.CallerRunsPolicy.class);
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void createsConfiguredScheduler() {
        ExecutorProperties properties = new ExecutorProperties();
        properties.getScheduler().setPoolSize(3);

        ThreadPoolTaskScheduler scheduler = config.scheduledExecutor(properties);
        scheduler.initialize();
        try {
            assertThat(scheduler.getScheduledThreadPoolExecutor().getCorePoolSize()).isEqualTo(3);
        } finally {
            scheduler.shutdown();
        }
    }

    @Test
    void rejectsInvalidResourceLimits() {
        ExecutorProperties properties = new ExecutorProperties();
        properties.getTask().setCoreSize(4);
        properties.getTask().setMaxSize(2);
        properties.setAwaitTermination(Duration.ofSeconds(10));

        assertThatThrownBy(() -> config.taskExecutor(properties))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("max-size");
    }
}
