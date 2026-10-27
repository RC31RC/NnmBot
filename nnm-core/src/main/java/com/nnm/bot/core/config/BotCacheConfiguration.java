package com.nnm.bot.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({BotCacheProperties.class, CommandRoutingProperties.class, ReplyProperties.class})
public class BotCacheConfiguration {
}
