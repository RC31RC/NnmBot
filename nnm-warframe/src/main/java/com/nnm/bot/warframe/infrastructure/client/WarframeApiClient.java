package com.nnm.bot.warframe.infrastructure.client;

import com.nnm.bot.common.http.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Component
public class WarframeApiClient {

    private final HttpClient httpClient;
    private final WarframeApiProperties properties;

    public WarframeApiClient(HttpClient httpClient, WarframeApiProperties properties) {
        properties.validate();
        this.httpClient = httpClient;
        this.properties = properties;
    }

    public String fetchWorldState() {
        var result = httpClient.get(properties.getWorldStateUrl().toString(),
                Map.of("Accept-Language", properties.getLanguage()));
        if (result.isSuccess()) {
            return result.body();
        }
        log.warn("Failed to fetch world state: {}", result.statusCode());
        return null;
    }

    public String fetchMarketOrders(String itemUrlName) {
        String url = UriComponentsBuilder.fromUri(properties.getMarketBaseUrl())
                .pathSegment("items", itemUrlName, "orders")
                .build()
                .encode()
                .toUriString();
        var result = httpClient.get(url, Map.of(
                "Platform", properties.getPlatform(),
                "Language", properties.getLanguage()
        ));
        if (result.isSuccess()) {
            return result.body();
        }
        if (result.isTooManyRequests()) {
            log.warn("Warframe.market rate limited (429) for {}", itemUrlName);
        } else {
            log.warn("Failed to fetch market orders for {}: {}", itemUrlName, result.statusCode());
        }
        return null;
    }
}
