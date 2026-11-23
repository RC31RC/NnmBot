package com.nnm.bot.warframe.runtime.client.worldstate;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.client.worldstate.exchange.WarframeBountyCycleHttpExchange;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeBountyCycleProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/** 获取 oracle.browse.wf 当前赏金轮换补充数据。 */
@Component
public class WarframeBountyCycleClient {
    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeBountyCycleHttpExchange httpExchange;
    @Resource
    private WarframeBountyCycleProperties properties;

    /**
     * 获取赏金轮换 JSON 文本。
     *
     * @return 有界读取的 JSON 文本。
     */
    public String fetch() {
        String body = httpClient.executeGetOrThrow(properties.getUrl().toString(), httpExchange::fetch).body();
        return body == null ? "" : body;
    }
}
