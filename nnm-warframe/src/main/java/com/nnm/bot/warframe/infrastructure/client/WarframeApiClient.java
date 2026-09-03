package com.nnm.bot.warframe.infrastructure.client;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.config.properties.WarframeApiProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class WarframeApiClient {

    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeApiProperties properties;

    @PostConstruct
    void validateProperties() {
        properties.validate();
    }

    public HttpClient.HttpResult fetchWorldStateResult() {
        HttpClient.HttpResult result = httpClient.get(properties.getWorldStateUrl().toString(),
                Map.of("Accept-Language", properties.getLanguage()));
        if (!result.isSuccess()) {
            log.warn("Failed to fetch world state: {}", result.statusCode());
        }
        return result;
    }

    public HttpClient.ByteResult getBytes(URI url, Map<String, String> headers) {
        return httpClient.getBytes(url.toString(), headers);
    }

    public HttpClient.DownloadResult downloadTo(
            URI url, Map<String, String> headers, java.nio.file.Path target) {
        return httpClient.downloadTo(url.toString(), headers, target);
    }

    public HttpClient.HttpResult getText(URI url, Map<String, String> headers) {
        // PublicExport 和 DataSource 的响应有时没有 charset；不能让
        // StringHttpMessageConverter 猜测编码，否则中文会变成 ä½、å...。
        HttpClient.ByteResult result = httpClient.getBytes(url.toString(), headers);
        String body = result.body() == null ? null : decodeUtf8(result.body());
        return new HttpClient.HttpResult(result.statusCode(), body, result.error());
    }

    private static String decodeUtf8(byte[] body) {
        String value = new String(body, StandardCharsets.UTF_8);
        return value.startsWith("\uFEFF") ? value.substring(1) : value;
    }

    public String publicExportIndexUrl() {
        return properties.getPublicExportIndexUrl();
    }

    public String publicExportManifestUrl() {
        return properties.getPublicExportManifestUrl();
    }

    public String language() {
        return properties.getLanguage();
    }

    public String englishLanguage() {
        return properties.getEnglishLanguage();
    }

    public String fetchMarketOrders(String itemUrlName) {
        return fetchMarketOrders(itemUrlName, properties.getPlatform());
    }

    public String fetchMarketOrders(String itemUrlName, String platform) {
        String requestPlatform = StrUtil.isBlank(platform)
                ? properties.getPlatform()
                : platform.trim().toLowerCase(java.util.Locale.ROOT);
        String url = UriComponentsBuilder.fromUri(properties.getMarketBaseUrl())
                .pathSegment("items", itemUrlName, "orders")
                .build()
                .encode()
                .toUriString();
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Platform", requestPlatform);
        headers.put("Language", properties.getLanguage());
        headers.put("User-Agent", properties.getMarketUserAgent());
        if (ObjectUtil.isNotNull(properties.getMarketApiKey())
                && !properties.getMarketApiKey().isBlank()) {
            headers.put("Authorization", "Bearer " + properties.getMarketApiKey().trim());
        }
        HttpClient.HttpResult result = httpClient.get(url, headers);
        if (result.isSuccess()) {
            return result.body();
        }
        if (result.isTooManyRequests()) {
            log.warn("Warframe.market rate limited (429) for {}", itemUrlName);
        } else {
            log.warn("Failed to fetch market orders for {}: {}", itemUrlName, result.statusCode());
        }
        return null;
    }

    public String configuredMarketPlatform() {
        return properties.getPlatform();
    }
}
