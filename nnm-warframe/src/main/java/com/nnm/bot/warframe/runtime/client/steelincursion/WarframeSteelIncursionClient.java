package com.nnm.bot.warframe.runtime.client.steelincursion;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.config.steelincursion.WarframeSteelIncursionProperties;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.stereotype.Component;

/** 获取 browse.wf 的每日钢铁侵袭文本排期。 */
@Component
public class WarframeSteelIncursionClient {
    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeSteelIncursionProperties properties;

    /** @return 有界读取的 UTF-8 文本响应。 */
    public String fetch() {
        byte[] response = httpClient.getLimitedBytes(properties.getUrl().toString(),
            Map.of("Accept", "text/plain", "Accept-Encoding", "identity"), properties.getMaxResponseBytes());
        return new String(response, StandardCharsets.UTF_8);
    }
}
