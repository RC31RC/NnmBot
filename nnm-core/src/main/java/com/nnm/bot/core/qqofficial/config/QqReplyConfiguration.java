package com.nnm.bot.core.qqofficial.config;

import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(QqReplyProperties.class)
public class QqReplyConfiguration {
    @Bean
    @Primary
    public Clock qqReplyClock() {
        return Clock.systemUTC();
    }
}
