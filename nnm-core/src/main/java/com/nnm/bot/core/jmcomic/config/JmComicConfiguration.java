package com.nnm.bot.core.jmcomic.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.github.jukomu.jmcomic.api.enums.ClientType;
import io.github.jukomu.jmcomic.core.JmComic;
import io.github.jukomu.jmcomic.core.client.AbstractJmClient;
import io.github.jukomu.jmcomic.core.config.JmConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JmComicProperties.class)
public class JmComicConfiguration {

    @Bean
    public JmConfiguration jmClientConfiguration(
            JmComicProperties properties,
            @Qualifier("jmDownloadExecutor")
            ThreadPoolTaskExecutor executor) {
        JmConfiguration.Builder builder = new JmConfiguration.Builder()
                .clientType(ClientType.API)
                .timeout(properties.getRequestTimeout())
                .imageTimeout(properties.getImageTimeout())
                .retryTimes(properties.getRetryTimes())
                .downloadThreadPoolSize(properties.getDownloadThreads())
                .executor(executor.getThreadPoolExecutor())
                .cacheSize(properties.getClientCacheBytes());
        if (CollUtil.isNotEmpty(properties.getApiDomains())) {
            builder.apiDomains(properties.getApiDomains());
        }
        if (StrUtil.isNotBlank(properties.getProxyHost())
                && ObjectUtil.isNotNull(properties.getProxyPort())) {
            builder.proxy(properties.getProxyHost(), properties.getProxyPort());
        }
        return builder.build();
    }

    /**
     * JM 客户端构造时会异步探测上游域名。
     *
     * <p>客户端不是应用启动所必需的基础设施，延迟到真正执行下载时再创建，
     * 避免上游网络异常拖慢启动并制造误导性的启动失败日志。</p>
     */
    @Bean(destroyMethod = "close")
    @Lazy
    public AbstractJmClient jmClient(JmConfiguration configuration) {
        return JmComic.newApiClient(configuration);
    }
}
