package com.nnm.bot.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** 注册核心消息、回复与路由缓存的配置属性。 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({BotCacheProperties.class, CommandRoutingProperties.class, ReplyProperties.class,
    MessageReadProperties.class})
public class BotCacheConfiguration {}
