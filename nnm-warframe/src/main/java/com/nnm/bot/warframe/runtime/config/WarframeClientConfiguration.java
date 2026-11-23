package com.nnm.bot.warframe.runtime.config;

import com.nnm.bot.common.http.NnmRestClientFactory;
import com.nnm.bot.warframe.runtime.client.arbitration.exchange.WarframeArbitrationHttpExchange;
import com.nnm.bot.warframe.runtime.client.kuva.exchange.WarframeKuvaMissionHttpExchange;
import com.nnm.bot.warframe.runtime.client.market.exchange.WarframeMarketV1HttpExchange;
import com.nnm.bot.warframe.runtime.client.market.exchange.WarframeMarketV2HttpExchange;
import com.nnm.bot.warframe.runtime.client.steelincursion.exchange.WarframeSteelIncursionHttpExchange;
import com.nnm.bot.warframe.runtime.client.worldstate.exchange.WarframeBountyCycleHttpExchange;
import com.nnm.bot.warframe.runtime.client.worldstate.exchange.WarframeInvasionHttpExchange;
import com.nnm.bot.warframe.runtime.client.worldstate.exchange.WarframeWorldStateHttpExchange;
import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationSourceProperties;
import com.nnm.bot.warframe.runtime.config.kuva.WarframeKuvaMissionProperties;
import com.nnm.bot.warframe.runtime.config.market.WarframeMarketApiProperties;
import com.nnm.bot.warframe.runtime.config.steelincursion.WarframeSteelIncursionProperties;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeBountyCycleProperties;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeInvasionProperties;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeOfficialApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class WarframeClientConfiguration {

    /**
     * 注册统一使用 UTC 的业务时钟，供运行时刷新与过期判断使用。
     *
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
    public WarframeMarketV1HttpExchange warframeMarketV1HttpExchange(NnmRestClientFactory clientFactory,
        WarframeMarketApiProperties properties) {
        return clientFactory.createClient(properties.getV1BaseUrl(), WarframeMarketV1HttpExchange.class);
    }

    /**
     * 取得游戏资料市场HTTP声明式接口。
     *
     * @param clientFactory 客户端工厂。
     * @param properties 当前业务配置。
     * @return 游戏资料市场HTTP声明式接口对象。
     */
    @Bean
    public WarframeMarketV2HttpExchange warframeMarketV2HttpExchange(NnmRestClientFactory clientFactory,
        WarframeMarketApiProperties properties) {
        return clientFactory.createClient(properties.getV2BaseUrl(), WarframeMarketV2HttpExchange.class);
    }

    /**
     * 取得游戏资料世界状态HTTP声明式接口。
     *
     * @param clientFactory 客户端工厂。
     * @param properties 当前业务配置。
     * @return 游戏资料世界状态HTTP声明式接口对象。
     */
    @Bean
    public WarframeWorldStateHttpExchange warframeWorldStateHttpExchange(NnmRestClientFactory clientFactory,
        WarframeOfficialApiProperties properties) {
        return clientFactory.createClient(properties.getWorldStateUrl(), WarframeWorldStateHttpExchange.class);
    }

    /**
     * 创建使用来源响应上限的赤毒任务 HTTP 接口。
     *
     * @param clientFactory HTTP 声明式接口工厂。
     * @param properties 赤毒任务来源配置。
     * @return 赤毒任务 HTTP 接口。
     */
    @Bean
    public WarframeKuvaMissionHttpExchange warframeKuvaMissionHttpExchange(NnmRestClientFactory clientFactory,
        WarframeKuvaMissionProperties properties) {
        return clientFactory.createClient(properties.getUrl(), WarframeKuvaMissionHttpExchange.class,
            properties.getMaxBytes());
    }

    /**
     * 创建使用来源响应上限的仲裁排期 HTTP 接口。
     *
     * @param clientFactory HTTP 声明式接口工厂。
     * @param properties 仲裁来源配置。
     * @return 仲裁排期 HTTP 接口。
     */
    @Bean
    public WarframeArbitrationHttpExchange warframeArbitrationHttpExchange(NnmRestClientFactory clientFactory,
        WarframeArbitrationSourceProperties properties) {
        return clientFactory.createClient(properties.getUrl(), WarframeArbitrationHttpExchange.class,
            properties.getMaxResponseBytes());
    }

    /**
     * 创建使用来源响应上限的钢铁侵袭 HTTP 接口。
     *
     * @param clientFactory HTTP 声明式接口工厂。
     * @param properties 钢铁侵袭来源配置。
     * @return 钢铁侵袭 HTTP 接口。
     */
    @Bean
    public WarframeSteelIncursionHttpExchange warframeSteelIncursionHttpExchange(NnmRestClientFactory clientFactory,
        WarframeSteelIncursionProperties properties) {
        return clientFactory.createClient(properties.getUrl(), WarframeSteelIncursionHttpExchange.class,
            properties.getMaxResponseBytes());
    }

    /**
     * 创建赏金轮换补充数据 HTTP 接口。
     *
     * @param clientFactory HTTP 声明式接口工厂。
     * @param properties 赏金轮换来源配置。
     * @return 赏金轮换 HTTP 接口。
     */
    @Bean
    public WarframeBountyCycleHttpExchange warframeBountyCycleHttpExchange(NnmRestClientFactory clientFactory,
        WarframeBountyCycleProperties properties) {
        return clientFactory.createClient(properties.getUrl(), WarframeBountyCycleHttpExchange.class,
            properties.getMaxBytes());
    }

    /**
     * 创建入侵任务类型补充数据 HTTP 接口。
     *
     * @param clientFactory HTTP 声明式接口工厂。
     * @param properties 入侵任务来源配置。
     * @return 入侵任务 HTTP 接口。
     */
    @Bean
    public WarframeInvasionHttpExchange warframeInvasionHttpExchange(NnmRestClientFactory clientFactory,
        WarframeInvasionProperties properties) {
        return clientFactory.createClient(properties.getUrl(), WarframeInvasionHttpExchange.class,
            properties.getMaxBytes());
    }
}
