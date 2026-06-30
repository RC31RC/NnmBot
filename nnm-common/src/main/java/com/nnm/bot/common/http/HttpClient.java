package com.nnm.bot.common.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

/**
 * HTTP 调用封装组件，基于 Spring {@link RestClient}。
 * <p>
 * 容错策略（按需求）：连接/读取超时（见 {@link RestClientConfig}）+ 幂等 GET 自动重试，不做熔断。
 * 重试覆盖网络异常、读超时、5xx、429；POST 不自动重试以避免非幂等副作用。
 * </p>
 */
@Slf4j
@Component
public class HttpClient {

    private final RestClient restClient;
    private final HttpClientProperties properties;

    public HttpClient(RestClient restClient, HttpClientProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public HttpResult get(String url) {
        return get(url, Map.of());
    }

    /**
     * 发起 GET 请求，对可重试故障（网络异常、超时、5xx、429）自动重试。
     */
    public HttpResult get(String url, Map<String, String> headers) {
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        HttpResult last = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            last = doGet(url, headers);
            if (!last.isRetryable() || attempt == maxAttempts) {
                return last;
            }
            log.warn("GET {} 第 {}/{} 次失败(status={})，准备重试", url, attempt, maxAttempts, last.statusCode());
            if (!sleepBackoff(attempt)) {
                return last;
            }
        }
        return last;
    }

    private HttpResult doGet(String url, Map<String, String> headers) {
        try {
            String body = restClient.get()
                    .uri(url)
                    .headers(h -> headers.forEach(h::add))
                    .retrieve()
                    .body(String.class);
            return new HttpResult(200, body, null);
        } catch (RestClientResponseException e) {
            // 拿到了响应但状态码非 2xx
            return new HttpResult(e.getStatusCode().value(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            // 网络异常 / 超时
            log.warn("GET {} 失败: {}", url, e.getMessage());
            return new HttpResult(-1, null, e);
        }
    }

    /**
     * 发起 POST 请求。不自动重试（请求体可能非幂等，重试需由调用方按业务语义决定）。
     */
    public HttpResult post(String url, String body, Map<String, String> headers) {
        try {
            String responseBody = restClient.post()
                    .uri(url)
                    .headers(h -> headers.forEach(h::add))
                    .body(body)
                    .retrieve()
                    .body(String.class);
            return new HttpResult(200, responseBody, null);
        } catch (RestClientResponseException e) {
            return new HttpResult(e.getStatusCode().value(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.warn("POST {} 失败: {}", url, e.getMessage());
            return new HttpResult(-1, null, e);
        }
    }

    /**
     * 退避等待。线程被中断时返回 false，提示调用方停止重试。
     */
    private boolean sleepBackoff(int attempt) {
        try {
            Thread.sleep(properties.getRetryBackoff().toMillis() * attempt);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * HTTP 响应封装。{@code statusCode == -1} 表示网络异常/超时（无 HTTP 响应）。
     */
    public record HttpResult(int statusCode, String body, Exception error) {

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        /** 是否触发对端限流（429 Too Many Requests） */
        public boolean isTooManyRequests() {
            return statusCode == 429;
        }

        /** 是否为可重试故障：网络异常、读超时（statusCode=-1）、服务端 5xx、限流 429 */
        public boolean isRetryable() {
            return statusCode == -1 || statusCode == 429 || (statusCode >= 500 && statusCode < 600);
        }

        /** 兼容 Spring 的状态码判断 */
        public boolean is2xx() {
            return HttpStatusCode.valueOf(statusCode == -1 ? 599 : statusCode).is2xxSuccessful();
        }
    }
}
