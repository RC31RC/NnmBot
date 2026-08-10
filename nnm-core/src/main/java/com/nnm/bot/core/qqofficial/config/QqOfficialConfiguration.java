package com.nnm.bot.core.qqofficial.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(QqOfficialProperties.class)
public class QqOfficialConfiguration {

    private final QqOfficialProperties properties;

    public QqOfficialConfiguration(QqOfficialProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void validateProperties() {
        properties.validate();
    }
}
