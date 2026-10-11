package com.nnm.bot.warframe.client;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.http.HttpDiagnostics;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** 社区 DataSource CDN 客户端，只负责传输文本，不参与数据合并。 */
@Component
@Slf4j
public class WarframeCommunityApiClient {

    @Resource
    private HttpClient httpClient;

    public HttpClient.TextResult getText(URI url, Map<String, String> headers) {
        HttpClient.TextResult result = httpClient.downloadText(url.toString(), headers);
        String body = result.body() == null
                ? null : decodeUtf8(result.body());
        return new HttpClient.TextResult(
                result.statusCode(), body, result.headers(), result.bytes(), result.error());
    }

    /** 候选地址属于同一份数据，按配置顺序回退，不同时下载多个 CDN 副本。 */
    public FetchedText fetch(String sourceCode, List<URI> candidates) {
        List<String> failures = new ArrayList<>();
        for (int index = 0; index < candidates.size(); index++) {
            URI url = candidates.get(index);
            long startedAt = System.nanoTime();
            try {
                HttpClient.TextResult result = getText(url, Map.of("Accept", "application/json"));
                String contentType = result.headers().getFirst("Content-Type");
                log.info("社区来源请求完成, source={}, endpoint={}, attempt={}/{}, status={}, "
                                + "contentType={}, bytes={}, chars={}, elapsedMs={}, error={}",
                        sourceCode, safeEndpoint(url), index + 1, candidates.size(),
                        result.statusCode(), contentType, result.bytes(),
                        result.body() == null ? 0 : result.body().length(),
                        Duration.ofNanos(System.nanoTime() - startedAt).toMillis(),
                        failureMessage(result.error()));
                if (result.isSuccess() && StrUtil.isNotBlank(result.body())
                        && (contentType == null
                        || !contentType.toLowerCase(Locale.ROOT).contains("html"))) {
                    return new FetchedText(url, result.body());
                }
                failures.add(safeEndpoint(url) + " -> HTTP " + result.statusCode()
                        + (StrUtil.isBlank(contentType) ? "" : " " + contentType)
                        + (result.error() == null ? "" : " " + failureMessage(result.error())));
            } catch (RuntimeException exception) {
                failures.add(safeEndpoint(url) + " -> " + failureMessage(exception));
                log.warn("社区来源请求异常, source={}, endpoint={}, attempt={}/{}, message={}",
                        sourceCode, safeEndpoint(url), index + 1, candidates.size(),
                        failureMessage(exception));
            }
        }
        throw new BusinessException(
                WarframeManagementErrorCode.COMMUNITY_DATA_SOURCE_UNAVAILABLE,
                "社区数据来源拉取失败：" + sourceCode + "；" + String.join("；", failures));
    }

    public record FetchedText(URI endpoint, String body) {
    }

    public static String safeEndpoint(URI uri) {
        if (uri == null) {
            return null;
        }
        return HttpDiagnostics.safeUrl(uri.toString());
    }

    public static String failureMessage(Throwable exception) {
        if (exception == null) {
            return null;
        }
        return HttpDiagnostics.safeFailureMessage(StrUtil.blankToDefault(
                exception.getMessage(), exception.getClass().getSimpleName()), null);
    }

    private static String decodeUtf8(String body) {
        return body.startsWith("\uFEFF") ? body.substring(1) : body;
    }
}
