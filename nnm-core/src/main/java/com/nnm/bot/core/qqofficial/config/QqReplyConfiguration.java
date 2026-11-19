package com.nnm.bot.core.qqofficial.config;

import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/** 启用 QQ 官方回复窗口及账号能力白名单配置。 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(QqReplyProperties.class)
public class QqReplyConfiguration {
    @Bean
    @Primary
    public Clock qqReplyClock() {
        return Clock.systemUTC();
    }
}
