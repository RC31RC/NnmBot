package com.nnm.bot.common.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.retry.RetryBackoff;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 统一同步 HTTP 客户端。
 *
 * <p>幂等 GET 对网络异常、超时、5xx 和 429 自动重试；POST 默认不重试，
 * 避免非幂等请求产生重复副作用。</p>
 */
@Slf4j
@Component
public class HttpClient {

    @Resource
    private RestClient restClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private HttpClientProperties properties;

    public HttpResult get(String url) {
        return get(url, Map.of());
    }

    /**
     * 兼容需要自行判断状态码的调用方。请求失败不会抛出外部调用异常。
     */
    public HttpResult get(String url, Map<String, String> headers) {
        String requestUrl = requireUrl(url);
        Map<String, String> requestHeaders = normalizedHeaders(headers);
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        HttpResult last = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            last = doGetResult(requestUrl, requestHeaders);
            if (!last.isRetryable() || attempt == maxAttempts) {
                return last;
            }
            logRetry("GET", requestUrl, attempt, maxAttempts, last.statusCode());
            if (!sleepBackoff(attempt)) {
                return last;
            }
        }
        return last;
    }

    public <T> T get(String url, Class<T> responseType) {
        return get(url, Map.of(), responseType);
    }

    /**
     * 发起类型化 GET 请求。最终失败时抛出明确的外部 HTTP、超时或传输异常。
     */
    public <T> T get(String url,
                     Map<String, String> headers,
                     Class<T> responseType) {
        return getWithRetry(url, headers, responseType);
    }

    /**
     * 发起支持泛型返回值的 GET 请求。
     */
    public <T> T get(String url,
                     Map<String, String> headers,
                     ParameterizedTypeReference<T> responseType) {
        return getWithRetry(url, headers, responseType.getType());
    }

    private <T> T getWithRetry(String url,
                               Map<String, String> headers,
                               Type responseType) {
        String requestUrl = requireUrl(url);
        Map<String, String> requestHeaders = normalizedHeaders(headers);
        Objects.requireNonNull(responseType, "responseType must not be null");
        int maxAttempts = Math.max(1, properties.getMaxAttempts());

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return doGet(requestUrl, requestHeaders, responseType);
            } catch (RuntimeException exception) {
                RuntimeException normalized = normalize("GET", requestUrl, exception);
                if (!isRetryable(normalized) || attempt == maxAttempts) {
                    throw normalized;
                }
                logRetry("GET", requestUrl, attempt, maxAttempts, statusCode(normalized));
                if (!sleepBackoff(attempt)) {
                    throw normalized;
                }
            }
        }
        throw new IllegalStateException("Unreachable HTTP retry state");
    }

    /**
     * 发起 JSON POST 请求。该方法不自动重试。
     */
    public <T> T postJson(String url,
                          Object body,
                          Map<String, String> headers,
                          Class<T> responseType) {
        return postJson(url, body, headers, responseType, responseType);
    }

    /**
     * 发起支持泛型返回值的 JSON POST 请求。该方法不自动重试。
     */
    public <T> T postJson(String url,
                          Object body,
                          Map<String, String> headers,
                          ParameterizedTypeReference<T> responseType) {
        return postJson(url, body, headers, responseType.getType(), responseType);
    }

    private <T> T postJson(String url,
                           Object body,
                           Map<String, String> headers,
                           Type responseType,
                           Object responseTypeReference) {
        String requestUrl = requireUrl(url);
        Map<String, String> requestHeaders = normalizedHeaders(headers);
        Objects.requireNonNull(responseTypeReference, "responseType must not be null");
        String requestBody = serialize("POST", requestUrl, body == null ? Map.of() : body);
        logRequest("POST", requestUrl, body);
        long startedAt = System.nanoTime();
        try {
            ResponseEntity<String> response = restClient.post()
                    .uri(requestUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> requestHeaders.forEach(httpHeaders::set))
                    .body(requestBody)
                    .retrieve()
                    .toEntity(String.class);
            logResponse("POST", requestUrl, response, startedAt);
            return deserialize("POST", requestUrl, response.getBody(), responseType);
        } catch (RuntimeException exception) {
            throw normalizeAndLog("POST", requestUrl, exception, startedAt);
        }
    }

    /**
     * 兼容原有字符串 POST 调用。请求失败不会抛出外部调用异常。
     */
    public HttpResult post(String url, String body, Map<String, String> headers) {
        String requestUrl = requireUrl(url);
        Map<String, String> requestHeaders = normalizedHeaders(headers);
        logRequest("POST", requestUrl, body);
        long startedAt = System.nanoTime();
        try {
            ResponseEntity<String> response = restClient.post()
                    .uri(requestUrl)
                    .headers(httpHeaders -> requestHeaders.forEach(httpHeaders::set))
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);
            logResponse("POST", requestUrl, response, startedAt);
            return new HttpResult(response.getStatusCode().value(), response.getBody(), null);
        } catch (RestClientResponseException exception) {
            logFailure("POST", requestUrl, exception.getStatusCode().value(), exception, startedAt);
            return new HttpResult(exception.getStatusCode().value(),
                    exception.getResponseBodyAsString(), exception);
        } catch (RuntimeException exception) {
            logFailure("POST", requestUrl, -1, exception, startedAt);
            return new HttpResult(-1, null, exception);
        }
    }

    private HttpResult doGetResult(String url, Map<String, String> headers) {
        logRequest("GET", url, null);
        long startedAt = System.nanoTime();
        try {
            ResponseEntity<String> response = restClient.get()
                    .uri(url)
                    .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                    .retrieve()
                    .toEntity(String.class);
            logResponse("GET", url, response, startedAt);
            return new HttpResult(response.getStatusCode().value(), response.getBody(), null);
        } catch (RestClientResponseException exception) {
            logFailure("GET", url, exception.getStatusCode().value(), exception, startedAt);
            return new HttpResult(exception.getStatusCode().value(),
                    exception.getResponseBodyAsString(), exception);
        } catch (RuntimeException exception) {
            logFailure("GET", url, -1, exception, startedAt);
            return new HttpResult(-1, null, exception);
        }
    }

    private <T> T doGet(String url,
                        Map<String, String> headers,
                        Type responseType) {
        logRequest("GET", url, null);
        long startedAt = System.nanoTime();
        try {
            ResponseEntity<String> response = restClient.get()
                    .uri(url)
                    .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                    .retrieve()
                    .toEntity(String.class);
            logResponse("GET", url, response, startedAt);
            return deserialize("GET", url, response.getBody(), responseType);
        } catch (RuntimeException exception) {
            throw normalizeAndLog("GET", url, exception, startedAt);
        }
    }

    private RuntimeException normalizeAndLog(String method,
                                             String url,
                                             RuntimeException exception,
                                             long startedAt) {
        RuntimeException normalized = normalize(method, url, exception);
        logFailure(method, url, statusCode(normalized), normalized, startedAt);
        return normalized;
    }

    private RuntimeException normalize(String method,
                                       String url,
                                       RuntimeException exception) {
        if (exception instanceof ExternalHttpException
                || exception instanceof ExternalTimeoutException
                || exception instanceof ExternalTransportException
                || exception instanceof ExternalPayloadException) {
            return exception;
        }
        String safeUrl = sanitizeUrl(url);
        if (exception instanceof RestClientResponseException responseException) {
            return new ExternalHttpException(
                    method,
                    safeUrl,
                    responseException.getStatusCode().value(),
                    truncate(responseException.getResponseBodyAsString()),
                    exception);
        }
        if (containsCause(exception, HttpTimeoutException.class)
                || containsCause(exception, SocketTimeoutException.class)) {
            return new ExternalTimeoutException(method, safeUrl, exception);
        }
        return new ExternalTransportException(method, safeUrl, exception);
    }

    private String serialize(String method, String url, Object body) {
        if (body instanceof String stringBody) {
            return stringBody;
        }
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException exception) {
            throw new ExternalPayloadException(
                    method, sanitizeUrl(url), "serialization", exception);
        }
    }

    private <T> T deserialize(String method,
                              String url,
                              String body,
                              Type responseType) {
        if (responseType == Void.class || responseType == void.class
                || body == null || body.isBlank()) {
            return null;
        }
        if (responseType == String.class) {
            @SuppressWarnings("unchecked")
            T result = (T) body;
            return result;
        }
        try {
            return objectMapper.readValue(body, objectMapper.getTypeFactory()
                    .constructType(responseType));
        } catch (JsonProcessingException exception) {
            throw new ExternalPayloadException(
                    method, sanitizeUrl(url), "deserialization", exception);
        }
    }

    private void logRequest(String method, String url, Object payload) {
        log.debug("[{}] >>> url={}", method, sanitizeUrl(url));
        if (properties.isPayloadLoggingEnabled() && payload != null && log.isDebugEnabled()) {
            log.debug("[{}] >>> payload={}", method, truncate(String.valueOf(payload)));
        }
    }

    private void logResponse(String method,
                             String url,
                             ResponseEntity<?> response,
                             long startedAt) {
        long elapsedNanos = elapsedNanos(startedAt);
        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos);
        if (!properties.getSlowRequestThreshold().isZero()
                && elapsedNanos >= properties.getSlowRequestThreshold().toNanos()) {
            log.warn("[{}] <<< url={}, status={}, elapsedMs={}",
                    method, sanitizeUrl(url), response.getStatusCode().value(), elapsedMillis);
        } else {
            log.debug("[{}] <<< url={}, status={}, elapsedMs={}",
                    method, sanitizeUrl(url), response.getStatusCode().value(), elapsedMillis);
        }
        if (properties.isPayloadLoggingEnabled()
                && response.getBody() != null
                && log.isDebugEnabled()) {
            log.debug("[{}] <<< payload={}", method, truncate(String.valueOf(response.getBody())));
        }
    }

    private void logFailure(String method,
                            String url,
                            int statusCode,
                            RuntimeException exception,
                            long startedAt) {
        log.warn("[{}] <<< url={}, status={}, elapsedMs={}, error={}",
                method,
                sanitizeUrl(url),
                statusCode,
                TimeUnit.NANOSECONDS.toMillis(elapsedNanos(startedAt)),
                exception.getClass().getSimpleName());
        log.debug("[{}] external HTTP failure details", method, exception);
    }

    private void logRetry(String method,
                          String url,
                          int attempt,
                          int maxAttempts,
                          int statusCode) {
        log.warn("[{}] url={} attempt={}/{} failed(status={}), retrying",
                method, sanitizeUrl(url), attempt, maxAttempts, statusCode);
    }

    private boolean sleepBackoff(int attempt) {
        try {
            Thread.sleep(RetryBackoff.exponential(
                    properties.getRetryBackoff(),
                    properties.getRetryMaxBackoff(),
                    attempt
            ).toMillis());
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    static String sanitizeUrl(String url) {
        if (url == null) {
            return "";
        }
        int queryIndex = url.indexOf('?');
        int fragmentIndex = url.indexOf('#');
        int endIndex = url.length();
        if (queryIndex >= 0) {
            endIndex = Math.min(endIndex, queryIndex);
        }
        if (fragmentIndex >= 0) {
            endIndex = Math.min(endIndex, fragmentIndex);
        }
        return url.substring(0, endIndex);
    }

    private String truncate(String value) {
        if (value == null) {
            return "";
        }
        int maxLength = properties.getMaxPayloadLogLength();
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength)
                + "...(truncated, total=" + value.length() + ")";
    }

    private static String requireUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("url must not be blank");
        }
        return url;
    }

    private static Map<String, String> normalizedHeaders(Map<String, String> headers) {
        return headers == null ? Map.of() : headers;
    }

    private static boolean isRetryable(RuntimeException exception) {
        return exception instanceof ExternalTimeoutException
                || exception instanceof ExternalTransportException
                || exception instanceof ExternalHttpException httpException
                && httpException.isRetryable();
    }

    private static int statusCode(RuntimeException exception) {
        return exception instanceof ExternalHttpException httpException
                ? httpException.getStatusCode()
                : -1;
    }

    private static boolean containsCause(Throwable throwable,
                                         Class<? extends Throwable> causeType) {
        Throwable current = throwable;
        while (current != null) {
            if (causeType.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static long elapsedNanos(long startedAt) {
        return Math.max(0L, System.nanoTime() - startedAt);
    }

    /**
     * {@code statusCode == -1} 表示没有收到 HTTP 响应。
     */
    public record HttpResult(int statusCode, String body, Exception error) {

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean isTooManyRequests() {
            return statusCode == 429;
        }

        public boolean isRetryable() {
            return statusCode == -1 || statusCode == 429
                    || statusCode >= 500 && statusCode < 600;
        }

        public boolean is2xx() {
            return HttpStatusCode.valueOf(statusCode == -1 ? 599 : statusCode)
                    .is2xxSuccessful();
        }
    }
}
