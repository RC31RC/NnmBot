package com.nnm.bot.warframe.infrastructure.client;

import com.nnm.bot.common.http.HttpClient;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WarframeApiClientTest {

    @Test
    void usesCentralizedApiConfigurationAndEncodesItemPath() {
        HttpClient httpClient = mock(HttpClient.class);
        WarframeApiProperties properties = new WarframeApiProperties();
        properties.setMarketBaseUrl(URI.create("http://localhost:9000/v1"));
        properties.setLanguage("zh-hans");
        properties.setPlatform("pc");
        when(httpClient.get(
                "http://localhost:9000/v1/items/soma%2Fprime/orders",
                Map.of("Platform", "pc", "Language", "zh-hans")
        )).thenReturn(new HttpClient.HttpResult(200, "orders", null));

        String result = new WarframeApiClient(httpClient, properties)
                .fetchMarketOrders("soma/prime");

        assertThat(result).isEqualTo("orders");
        verify(httpClient).get(
                "http://localhost:9000/v1/items/soma%2Fprime/orders",
                Map.of("Platform", "pc", "Language", "zh-hans")
        );
    }

    @Test
    void rejectsNonHttpApiUrls() {
        WarframeApiProperties properties = new WarframeApiProperties();
        properties.setWorldStateUrl(URI.create("file:///tmp/world-state.json"));

        assertThatThrownBy(() -> new WarframeApiClient(mock(HttpClient.class), properties))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("world-state-url");
    }
}
