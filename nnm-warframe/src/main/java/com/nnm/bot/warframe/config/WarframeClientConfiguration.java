package com.nnm.bot.warframe.config;

import com.nnm.bot.warframe.config.properties.WarframeApiProperties;
import com.nnm.bot.warframe.config.properties.WarframeForumStorageProperties;
import com.nnm.bot.warframe.config.properties.WarframeOfficialDataProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        WarframeApiProperties.class,
        WarframeForumStorageProperties.class,
        WarframeOfficialDataProperties.class
})
public class WarframeClientConfiguration {
}
