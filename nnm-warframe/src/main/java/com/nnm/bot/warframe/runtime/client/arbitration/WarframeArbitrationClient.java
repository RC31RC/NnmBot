package com.nnm.bot.warframe.runtime.client.arbitration;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationPullProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class WarframeArbitrationClient {

    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeArbitrationPullProperties properties;

    /**
     * 按配置获取外部响应。
     *
     * @return 按配置获取外部响应对应的文本。
     */
    public String fetch() {
        byte[] response = httpClient.getLimitedBytes(properties.getUrl().toString(),
                Map.of("Accept", "application/json, text/plain", "Accept-Encoding", "identity"),
                properties.getMaxResponseBytes());
        return new String(response, StandardCharsets.UTF_8);
    }
}
