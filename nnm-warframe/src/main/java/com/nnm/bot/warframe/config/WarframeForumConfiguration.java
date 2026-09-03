package com.nnm.bot.warframe.config;

import cn.hutool.core.lang.Snowflake;
import com.nnm.bot.warframe.config.properties.WarframeForumProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WarframeForumProperties.class)
public class WarframeForumConfiguration {

    @Bean
    public Snowflake warframeForumSnowflake(WarframeForumProperties properties) {
        properties.validate();
        return new Snowflake(
                properties.getSnowflakeWorkerId(),
                properties.getSnowflakeDataCenterId());
    }
}
