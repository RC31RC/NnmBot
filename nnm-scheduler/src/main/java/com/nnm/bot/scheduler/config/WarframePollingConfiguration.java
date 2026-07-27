package com.nnm.bot.scheduler.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WarframePollingProperties.class)
public class WarframePollingConfiguration {
}
