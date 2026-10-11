package com.nnm.bot.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.nnm.bot.core.jmcomic.config.JmComicProperties;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ExecutorProperties.class)
public class ExecutorConfig {

    /** 图片下载与解码独立限流；队列满时由调用方承担工作，避免无界积压图片任务。 */
    @Bean
    public ThreadPoolTaskExecutor jmDownloadExecutor(
            JmComicProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getDownloadThreads());
        executor.setMaxPoolSize(properties.getDownloadThreads());
        executor.setQueueCapacity(properties.getMaxPages());
        executor.setThreadNamePrefix("nnm-jm-download-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }

    /**
     * CPU 密集型任务使用有界平台线程池，避免无限创建线程争抢 CPU。
     */
    @Bean(name = "cpuTaskExecutor")
    public ThreadPoolTaskExecutor cpuTaskExecutor(ExecutorProperties properties) {
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

    /**
     * WebSocket、外部 I/O 和管理长任务使用虚拟线程。
     *
     * <p>虚拟线程本身创建成本低，但外部服务仍然需要限流，因此通过
     * {@code application-concurrency} 为执行器设置并发上限。达到上限时调用方
     * 会阻塞等待许可，而不会无限制地向外部服务发起请求。</p>
     */
    @Bean(name = {"applicationTaskExecutor", "taskExecutor"}, destroyMethod = "close")
    public AsyncTaskExecutor applicationTaskExecutor(ExecutorProperties properties) {
        properties.validate();
        return virtualExecutor("nnm-io-", properties.getApplicationConcurrency(), properties);
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
    public AsyncTaskExecutor memberDiscoveryExecutor(ExecutorProperties properties) {
        properties.validate();
        return virtualExecutor("nnm-discovery-", properties.getDiscoveryConcurrency(), properties);
    }

    /** 只执行建连工作，不承载业务消息处理或底层网络协议调度。 */
    @Bean(name = "webSocketConnectExecutor", destroyMethod = "close")
    public AsyncTaskExecutor webSocketConnectExecutor(ExecutorProperties properties) {
        properties.validate();
        return virtualExecutor("nnm-connect-", properties.getConnectionConcurrency(), properties);
    }

    /** 父刷新任务不能占用此池；有界队列和调用方背压限制下载、JSON 解析的内存峰值。 */
    @Bean
    public ThreadPoolTaskExecutor communitySourceExecutor(ExecutorProperties properties) {
        properties.validate();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCommunitySourceConcurrency());
        executor.setMaxPoolSize(properties.getCommunitySourceConcurrency());
        executor.setQueueCapacity(properties.getCommunitySourceQueueCapacity());
        executor.setThreadNamePrefix("nnm-community-source-");
        executor.setRejectedExecutionHandler((task, pool) -> {
            if (pool.isShutdown()) {
                throw new RejectedExecutionException(
                        "Community source executor is shutting down");
            }
            task.run();
        });
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(
                Math.toIntExact(properties.getAwaitTermination().toSeconds()));
        return executor;
    }

    private SimpleAsyncTaskExecutor virtualExecutor(
            String prefix, int concurrency, ExecutorProperties properties) {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor(prefix);
        executor.setVirtualThreads(true);
        // 在创建虚拟线程前背压，避免已排队的请求各自占用线程和业务对象。
        executor.setConcurrencyLimit(concurrency);
        executor.setCancelRemainingTasksOnClose(true);
        executor.setTaskTerminationTimeout(properties.getAwaitTermination().toMillis());
        return executor;
    }
}
