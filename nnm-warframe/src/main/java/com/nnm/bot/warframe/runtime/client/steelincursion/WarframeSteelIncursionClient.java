package com.nnm.bot.warframe.runtime.client.steelincursion;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.client.steelincursion.exchange.WarframeSteelIncursionHttpExchange;
import com.nnm.bot.warframe.runtime.config.steelincursion.WarframeSteelIncursionProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/** 获取 browse.wf 的每日钢铁侵袭文本排期。 */
@Component
public class WarframeSteelIncursionClient {
    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeSteelIncursionHttpExchange httpExchange;
    @Resource
    private WarframeSteelIncursionProperties properties;

    /** @return 有界读取的 UTF-8 文本响应。 */
    public String fetch() {
        String body =
            httpClient.executeGetOrThrow(properties.getUrl().toString(), () -> httpExchange.fetch("identity")).body();
        return body == null ? "" : body;
    }
}
