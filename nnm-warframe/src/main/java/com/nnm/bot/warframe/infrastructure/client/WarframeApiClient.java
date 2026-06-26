package com.nnm.bot.warframe.infrastructure.client;

import com.nnm.bot.common.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class WarframeApiClient {

    private static final String WORLD_STATE_URL = "https://api.warframestat.us/pc";
    private static final String MARKET_API_URL = "https://api.warframe.market/v1";

    @Value("${warframe.api.language:zh}")
    private String language;

    public String fetchWorldState() {
        var result = HttpUtils.get(WORLD_STATE_URL,
                Map.of("Accept-Language", language));
        if (result.isSuccess()) {
            return result.body();
        }
        log.warn("Failed to fetch world state: {}", result.statusCode());
        return null;
    }

    public String fetchMarketOrders(String itemUrlName) {
        String url = MARKET_API_URL + "/items/" + itemUrlName + "/orders";
        var result = HttpUtils.get(url, Map.of(
                "Platform", "pc",
                "Language", language
        ));
        if (result.isSuccess()) {
            return result.body();
        }
        log.warn("Failed to fetch market orders for {}: {}", itemUrlName, result.statusCode());
        return null;
    }
}
