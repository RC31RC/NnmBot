package com.nnm.bot.warframe.runtime.client.arbitration;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.client.arbitration.exchange.WarframeArbitrationHttpExchange;
import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationSourceProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class WarframeArbitrationClient {

    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeArbitrationHttpExchange httpExchange;
    @Resource
    private WarframeArbitrationSourceProperties properties;

    /**
     * 按配置获取外部响应。
     *
     * @return 按配置获取外部响应对应的文本。
     */
    public String fetch() {
        String body =
            httpClient.executeGetOrThrow(properties.getUrl().toString(), () -> httpExchange.fetch("identity")).body();
        return body == null ? "" : body;
    }
}
