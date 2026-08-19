package com.nnm.bot.core.qqofficial.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(QqOfficialProperties.class)
public class QqOfficialConfiguration {

    @Resource
    private QqOfficialProperties properties;

    @PostConstruct
    void validateProperties() {
        properties.validate();
    }
}
