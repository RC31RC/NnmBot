package com.nnm.bot.warframe.runtime.client.kuva;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.config.kuva.WarframeKuvaMissionProperties;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.stereotype.Component;

/** 获取 WFCD 提供的结构化赤毒任务日志。 */
@Component
public class WarframeKuvaMissionClient {
    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeKuvaMissionProperties properties;

    /** @return 有界读取的 UTF-8 JSON 响应字节。 */
    public byte[] fetch() {
        return httpClient.getLimitedBytes(properties.getUrl().toString(), Map.of("Accept", "application/json"),
            properties.getMaxBytes());
    }
}
