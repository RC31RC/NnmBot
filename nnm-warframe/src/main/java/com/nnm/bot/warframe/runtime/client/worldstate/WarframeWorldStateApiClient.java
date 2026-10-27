package com.nnm.bot.warframe.runtime.client.worldstate;

import com.nnm.bot.common.http.HttpClient.HttpResult;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.client.worldstate.exchange.WarframeWorldStateHttpExchange;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeOfficialApiProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** 官方动态 PHP 客户端；不再拉取 PublicExport 原始索引和文件。 */
@Slf4j
@Component
public class WarframeWorldStateApiClient {
    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeWorldStateHttpExchange httpExchange;
    @Resource
    private WarframeOfficialApiProperties properties;

    /**
     * 获取世界状态结果。
     *
     * @return HTTP结果对象。
     */
    public HttpClient.HttpResult fetchWorldStateResult() {
        HttpResult result = httpClient.executeGet(
                properties.getWorldStateUrl().toString(), () -> httpExchange.fetch(properties.getLanguage()));
        if (!result.isSuccess()) log.warn("Failed to fetch Warframe world state: {}", result.statusCode());
        return result;
    }
}
