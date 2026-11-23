package com.nnm.bot.warframe.persistent.official.config;

import com.nnm.bot.common.http.NnmRestClientFactory;
import com.nnm.bot.warframe.persistent.official.client.github.exchange.GithubWarframeDataHttpExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 官方数据源声明式 HTTP 接口配置。 */
@Configuration(proxyBeanMethods = false)
public class WarframeOfficialClientConfiguration {

    /**
     * 创建受官方版本响应上限约束的 GitHub API 接口。
     *
     * @param clientFactory HTTP 声明式接口工厂。
     * @param properties 官方数据源配置。
     * @return GitHub 官方数据 API 接口。
     */
    @Bean
    public GithubWarframeDataHttpExchange githubWarframeDataHttpExchange(NnmRestClientFactory clientFactory,
        WarframeOfficialImportProperties properties) {
        return clientFactory.createClient(properties.getApiBaseUrl(), GithubWarframeDataHttpExchange.class,
            properties.getMaxVersionResponseBytes());
    }
}
