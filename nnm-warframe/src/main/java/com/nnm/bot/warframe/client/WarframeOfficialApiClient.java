package com.nnm.bot.warframe.client;

import java.nio.file.Path;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.client.exchange.WarframeWorldStateHttpExchange;
import com.nnm.bot.warframe.config.properties.WarframeOfficialApiProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;

/**
 * Warframe 官方接口客户端。
 *
 * <p>这里只负责官方 WorldState、PublicExport 和文件传输，不承载社区
 * DataSource 或 Warframe Market 的请求。</p>
 */
@Slf4j
@Component
public class WarframeOfficialApiClient {

    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeWorldStateHttpExchange httpExchange;
    @Resource
    private WarframeOfficialApiProperties properties;

    @PostConstruct
    void validateProperties() {
        properties.validate();
    }

    public HttpClient.HttpResult fetchWorldStateResult() {
        HttpClient.HttpResult result = httpClient.executeGet(
                properties.getWorldStateUrl().toString(),
                () -> httpExchange.fetch(properties.getLanguage()));
        if (!result.isSuccess()) {
            log.warn("Failed to fetch Warframe world state: {}", result.statusCode());
        }
        return result;
    }

    public HttpClient.ByteResult getBytes(URI url, Map<String, String> headers) {
        return httpClient.getBytes(url.toString(), headers);
    }

    public HttpClient.DownloadResult downloadTo(
            URI url, Map<String, String> headers, Path target) {
        return httpClient.downloadTo(url.toString(), headers, target);
    }

    public HttpClient.TextResult getText(URI url, Map<String, String> headers) {
        // 官方文件偶尔不声明 charset，按 UTF-8 解码才能稳定保留中文。
        HttpClient.TextResult result = httpClient.downloadText(url.toString(), headers);
        String body = result.body() == null ? null : decodeUtf8(result.body());
        return new HttpClient.TextResult(
                result.statusCode(), body, result.headers(), result.bytes(), result.error());
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

    private static String decodeUtf8(String body) {
        return body.startsWith("\uFEFF") ? body.substring(1) : body;
    }
}
