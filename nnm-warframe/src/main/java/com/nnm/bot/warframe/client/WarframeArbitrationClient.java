package com.nnm.bot.warframe.client;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.client.exchange.WarframeArbitrationHttpExchange;
import com.nnm.bot.warframe.config.properties.WarframeArbitrationProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class WarframeArbitrationClient {
    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeArbitrationHttpExchange httpExchange;
    @Resource
    private WarframeArbitrationProperties properties;

    public String fetch() {
        var result = httpClient.executeGet(
                properties.getUrl().toString(), httpExchange::fetch);
        if (!result.isSuccess() || result.body() == null || result.body().isBlank()) {
            throw new IllegalStateException("Arbitration source unavailable: " + result.statusCode());
        }
        return result.body();
    }
}
