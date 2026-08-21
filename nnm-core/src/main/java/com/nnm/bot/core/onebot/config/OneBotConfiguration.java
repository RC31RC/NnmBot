package com.nnm.bot.core.onebot.config;

import com.nnm.bot.core.channel.BotChannelInstanceConfiguration;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OneBotProperties.class)
public class OneBotConfiguration {

    private final OneBotProperties properties;

    public OneBotConfiguration(OneBotProperties properties,
                               BotChannelInstanceConfiguration instanceConfiguration) {
        this.properties = properties;
    }

    @PostConstruct
    void validateProperties() {
        properties.validate();
    }
}
