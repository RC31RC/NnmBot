package com.nnm.bot.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Configuration
public class ExecutorConfig {

    @Bean(name = "taskExecutor")
    public ExecutorService taskExecutor() {
        Semaphore semaphore = new Semaphore(30);
        return Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual()
                        .name("nnm-vt-", 0)
                        .factory()
        );
    }

    @Bean(name = "scheduledExecutor")
    public java.util.concurrent.ScheduledExecutorService scheduledExecutor() {
        return Executors.newScheduledThreadPool(2, Thread.ofVirtual().factory());
    }
}
