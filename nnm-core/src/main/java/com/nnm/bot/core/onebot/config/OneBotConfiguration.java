package com.nnm.bot.core.onebot.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** 注册 OneBot 协议级属性并在启动时验证配置。 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OneBotProperties.class)
public class OneBotConfiguration {

    @Resource
    private OneBotProperties properties;

    @PostConstruct
    void validateProperties() {
        this.properties.validateDefaults();
    }
}
