package com.nnm.bot.warframe.infrastructure.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WarframeApiProperties.class)
public class WarframeClientConfiguration {
}
