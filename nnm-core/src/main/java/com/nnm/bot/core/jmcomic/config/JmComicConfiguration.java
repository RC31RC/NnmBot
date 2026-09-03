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

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JmComicProperties.class)
public class JmComicConfiguration {

    @Bean
    public JmConfiguration jmClientConfiguration(JmComicProperties properties) {
        JmConfiguration.Builder builder = new JmConfiguration.Builder()
                .clientType(ClientType.API)
                .timeout(properties.getRequestTimeout())
                .imageTimeout(properties.getImageTimeout())
                .retryTimes(properties.getRetryTimes())
                .downloadThreadPoolSize(properties.getDownloadThreads())
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

    @Bean(destroyMethod = "close")
    public AbstractJmClient jmClient(JmConfiguration configuration) {
        return JmComic.newApiClient(configuration);
    }
}
