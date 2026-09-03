package com.nnm.bot.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ExecutorProperties.class)
public class ExecutorConfig {

    @Bean(name = {"applicationTaskExecutor", "taskExecutor"})
    public ThreadPoolTaskExecutor taskExecutor(ExecutorProperties properties) {
        properties.validate();
        ExecutorProperties.Task task = properties.getTask();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(task.getCoreSize());
        executor.setMaxPoolSize(task.getMaxSize());
        executor.setQueueCapacity(task.getQueueCapacity());
        executor.setKeepAliveSeconds(Math.toIntExact(task.getKeepAlive().toSeconds()));
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("nnm-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(
                Math.toIntExact(properties.getAwaitTermination().toSeconds()));
        return executor;
    }

    @Bean(name = {"taskScheduler", "scheduledExecutor"})
    public ThreadPoolTaskScheduler scheduledExecutor(ExecutorProperties properties) {
        properties.validate();

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(properties.getScheduler().getPoolSize());
        scheduler.setThreadNamePrefix("nnm-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(
                Math.toIntExact(properties.getAwaitTermination().toSeconds()));
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    @Bean(name = "memberDiscoveryExecutor", destroyMethod = "close")
    public ExecutorService memberDiscoveryExecutor(ExecutorProperties properties) {
        properties.validate();
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
