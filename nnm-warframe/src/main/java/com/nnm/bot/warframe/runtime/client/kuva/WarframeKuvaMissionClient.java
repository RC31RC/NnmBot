package com.nnm.bot.warframe.runtime.client.kuva;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.client.kuva.exchange.WarframeKuvaMissionHttpExchange;
import com.nnm.bot.warframe.runtime.config.kuva.WarframeKuvaMissionProperties;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

/** 获取 WFCD 提供的结构化赤毒任务日志。 */
@Component
public class WarframeKuvaMissionClient {
    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeKuvaMissionHttpExchange httpExchange;
    @Resource
    private WarframeKuvaMissionProperties properties;

    /** @return 有界读取的 UTF-8 JSON 响应字节。 */
    public byte[] fetch() {
        String body = httpClient.executeGetOrThrow(properties.getUrl().toString(), httpExchange::fetch).body();
        return body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
    }
}
