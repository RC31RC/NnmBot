package com.nnm.bot.warframe.runtime.client;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.http.HttpClient.TextResult;
import com.nnm.bot.common.http.HttpDiagnostics;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** 按容量上限拉取运行时原文；来源选择与快照生命周期由调用业务管理。 */
@Component
@Slf4j
public class WarframeRuntimeDataClient {
    @Resource
    private HttpClient httpClient;

    /**
     * 读取文本。
     *
     * @param url 本次外部请求或资源的地址。
     * @param headers 请求头。
     * @return 文本结果对象。
     */
    public TextResult getText(URI url, Map<String, String> headers) {
        TextResult result = httpClient.downloadText(url.toString(), headers);
        String body = result.body() == null ? null : decodeUtf8(result.body());
        return new TextResult(result.statusCode(), body, result.headers(), result.bytes(), result.error());
    }

    /**
     * 按配置顺序尝试同一数据源的候选端点，全部失败时抛出明确的业务异常。
     * @param sourceCode 来源代码。
     * @param candidates 候选集合。
     * @return 拉取时间文本对象。
     */
    public FetchedText fetch(String sourceCode, List<URI> candidates) {
        List<String> failures = new ArrayList<>();
        for (int index = 0; index < candidates.size(); index++) {
            URI url = candidates.get(index);
            long startedAt = System.nanoTime();
            try {
                TextResult result = getText(url, Map.of(
                        "Accept", "application/json, text/plain, application/javascript, */*"));
                String contentType = result.headers().getFirst("Content-Type");
                log.info("Runtime source request completed, source={}, endpoint={}, attempt={}/{}, status={}, "
                                + "contentType={}, bytes={}, chars={}, elapsedMs={}, error={}",
                        sourceCode, safeEndpoint(url), index + 1, candidates.size(), result.statusCode(), contentType,
                        result.bytes(), result.body() == null ? 0 : result.body().length(),
                        Duration.ofNanos(System.nanoTime() - startedAt).toMillis(), failureMessage(result.error()));
                if (result.isSuccess() && StrUtil.isNotBlank(result.body())
                        && (contentType == null || !contentType.toLowerCase(Locale.ROOT).contains("html"))) {
                    return new FetchedText(url, result.body());
                }
                failures.add(safeEndpoint(url) + " -> HTTP " + result.statusCode()
                        + (StrUtil.isBlank(contentType) ? "" : " " + contentType)
                        + (result.error() == null ? "" : " " + failureMessage(result.error())));
            } catch (RuntimeException exception) {
                failures.add(safeEndpoint(url) + " -> " + failureMessage(exception));
                log.warn("Runtime source request failed, source={}, endpoint={}, attempt={}/{}, message={}",
                        sourceCode, safeEndpoint(url), index + 1, candidates.size(), failureMessage(exception));
            }
        }
        throw new BusinessException(WarframeManagementErrorCode.EXTERNAL_DATA_SOURCE_UNAVAILABLE,
                "Warframe 外部数据来源拉取失败：" + sourceCode + "；" + String.join("；", failures));
    }

    public record FetchedText(URI endpoint, String body) {
    }

    /**
     * 生成日志可用的端点文本，避免泄漏地址中的敏感查询参数。
     * @param uri 地址。
     * @return 已去除敏感查询参数的端点文本。
     */
    public static String safeEndpoint(URI uri) {
        return uri == null ? null : HttpDiagnostics.safeUrl(uri.toString());
    }

    /**
     * 提取异常信息用于汇总各候选端点失败原因。
     * @param exception 需要保留原因和字段定位的原始异常。
     * @return 异常提供的排障信息；没有异常时为空。
     */
    public static String failureMessage(Throwable exception) {
        if (exception == null) return null;
        return HttpDiagnostics.safeFailureMessage(StrUtil.blankToDefault(
                exception.getMessage(), exception.getClass().getSimpleName()), null);
    }

    /**
     * 按 UTF-8 恢复客户端返回的响应文本。
     * @param body 外部接口返回的响应正文。
     * @return 解码字符编码对应的文本。
     */
    private static String decodeUtf8(String body) {
        return body.startsWith("\uFEFF") ? body.substring(1) : body;
    }
}
