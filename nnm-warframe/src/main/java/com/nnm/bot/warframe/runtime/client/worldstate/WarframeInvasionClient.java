package com.nnm.bot.warframe.runtime.client.worldstate;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.client.worldstate.exchange.WarframeInvasionHttpExchange;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeInvasionProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/** 获取 oracle.browse.wf 入侵双方任务类型补充数据。 */
@Component
public class WarframeInvasionClient {
    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeInvasionHttpExchange httpExchange;
    @Resource
    private WarframeInvasionProperties properties;

    /**
     * 获取入侵补充数据 JSON 文本。
     *
     * @return 有界读取的 JSON 文本。
     */
    public String fetch() {
        String body = httpClient.executeGetOrThrow(properties.getUrl().toString(), httpExchange::fetch).body();
        return body == null ? "" : body;
    }
}
