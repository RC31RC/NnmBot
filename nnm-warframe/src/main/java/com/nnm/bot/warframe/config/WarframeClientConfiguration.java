package com.nnm.bot.warframe.config;

import com.nnm.bot.common.http.NnmRestClientFactory;
import com.nnm.bot.warframe.runtime.client.market.exchange.WarframeMarketV1HttpExchange;
import com.nnm.bot.warframe.runtime.client.market.exchange.WarframeMarketV2HttpExchange;
import com.nnm.bot.warframe.runtime.client.worldstate.exchange.WarframeWorldStateHttpExchange;
import com.nnm.bot.warframe.runtime.config.market.WarframeMarketApiProperties;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeOfficialApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class WarframeClientConfiguration {

    /**
     * 注册统一使用 UTC 的业务时钟，供运行时刷新与过期判断使用。
     * @return 时钟对象。
     */
    @Bean
    public Clock warframeClock() {
        return Clock.systemUTC();
    }

    /**
     * 取得游戏资料市场HTTP声明式接口。
     *
     * @param clientFactory 客户端工厂。
     * @param properties 当前业务配置。
     * @return 游戏资料市场HTTP声明式接口对象。
     */
    @Bean
    public WarframeMarketV1HttpExchange warframeMarketV1HttpExchange(
            NnmRestClientFactory clientFactory,
            WarframeMarketApiProperties properties) {
        return clientFactory.createClient(
                properties.getV1BaseUrl(), WarframeMarketV1HttpExchange.class);
    }

    /**
     * 取得游戏资料市场HTTP声明式接口。
     *
     * @param clientFactory 客户端工厂。
     * @param properties 当前业务配置。
     * @return 游戏资料市场HTTP声明式接口对象。
     */
    @Bean
    public WarframeMarketV2HttpExchange warframeMarketV2HttpExchange(
            NnmRestClientFactory clientFactory,
            WarframeMarketApiProperties properties) {
        return clientFactory.createClient(
                properties.getV2BaseUrl(), WarframeMarketV2HttpExchange.class);
    }

    /**
     * 取得游戏资料世界状态HTTP声明式接口。
     *
     * @param clientFactory 客户端工厂。
     * @param properties 当前业务配置。
     * @return 游戏资料世界状态HTTP声明式接口对象。
     */
    @Bean
    public WarframeWorldStateHttpExchange warframeWorldStateHttpExchange(
            NnmRestClientFactory clientFactory,
            WarframeOfficialApiProperties properties) {
        return clientFactory.createClient(
                properties.getWorldStateUrl(), WarframeWorldStateHttpExchange.class);
    }
}
