package com.nnm.bot.warframe.config;

import java.time.Clock;

import com.nnm.bot.common.http.NnmRestClientFactory;
import com.nnm.bot.warframe.client.exchange.WarframeArbitrationHttpExchange;
import com.nnm.bot.warframe.client.exchange.WarframeMarketV1HttpExchange;
import com.nnm.bot.warframe.client.exchange.WarframeMarketV2HttpExchange;
import com.nnm.bot.warframe.client.exchange.WarframeWorldStateHttpExchange;
import com.nnm.bot.warframe.config.properties.WarframeArbitrationProperties;
import com.nnm.bot.warframe.config.properties.WarframeCommunityDataProperties;
import com.nnm.bot.warframe.config.properties.WarframeForumStorageProperties;
import com.nnm.bot.warframe.config.properties.WarframeMarketApiProperties;
import com.nnm.bot.warframe.config.properties.WarframeOfficialApiProperties;
import com.nnm.bot.warframe.config.properties.WarframeOfficialDataProperties;
import com.nnm.bot.warframe.config.properties.WarframeWorldStateDataProperties;
import com.nnm.bot.warframe.config.properties.WarframeCycleProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        WarframeOfficialApiProperties.class,
        WarframeMarketApiProperties.class,
        WarframeCommunityDataProperties.class,
        WarframeForumStorageProperties.class,
        WarframeOfficialDataProperties.class,
        WarframeWorldStateDataProperties.class,
        WarframeCycleProperties.class,
        WarframeArbitrationProperties.class
})
public class WarframeClientConfiguration {

    @Bean
    public Clock warframeClock() {
        return Clock.systemUTC();
    }

    @Bean
    public WarframeArbitrationHttpExchange warframeArbitrationHttpExchange(
            NnmRestClientFactory clientFactory,
            WarframeArbitrationProperties properties) {
        return clientFactory.createClient(
                properties.getUrl(), WarframeArbitrationHttpExchange.class);
    }

    @Bean
    public WarframeMarketV1HttpExchange warframeMarketV1HttpExchange(
            NnmRestClientFactory clientFactory,
            WarframeMarketApiProperties properties) {
        return clientFactory.createClient(
                properties.getV1BaseUrl(), WarframeMarketV1HttpExchange.class);
    }

    @Bean
    public WarframeMarketV2HttpExchange warframeMarketV2HttpExchange(
            NnmRestClientFactory clientFactory,
            WarframeMarketApiProperties properties) {
        return clientFactory.createClient(
                properties.getV2BaseUrl(), WarframeMarketV2HttpExchange.class);
    }

    @Bean
    public WarframeWorldStateHttpExchange warframeWorldStateHttpExchange(
            NnmRestClientFactory clientFactory,
            WarframeOfficialApiProperties properties) {
        return clientFactory.createClient(
                properties.getWorldStateUrl(), WarframeWorldStateHttpExchange.class);
    }
}
