package com.nnm.bot.common.http;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.config.http.HttpClientProperties;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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

    /** 下载需要保留原始字节的响应，例如官方 PublicExport 的 LZMA 和 JSON 文件。 */
    public ByteResult getBytes(String url, Map<String, String> headers) {
        String requestUrl = requireUrl(url);
        Map<String, String> requestHeaders = normalizedHeaders(headers);
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        ByteResult last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            last = doGetBytesResult(requestUrl, requestHeaders);
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

    /**
     * 流式下载到文件。响应先写入目标文件同目录的临时文件，完整成功后再原子替换目标。
     * 这样既避免大响应一次性进入堆内存，也不会把半截文件暴露给后续解析逻辑。
     */
    public DownloadResult downloadTo(
            String url, Map<String, String> headers, Path target) {
        String requestUrl = requireUrl(url);
        Map<String, String> requestHeaders = normalizedHeaders(headers);
        Objects.requireNonNull(target, "target must not be null");
        Path normalizedTarget = target.toAbsolutePath().normalize();
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        DownloadResult last = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            last = doDownload(requestUrl, requestHeaders, normalizedTarget);
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
        String requestBody = serialize("POST", requestUrl, ObjectUtil.isNull(body) ? Map.of() : body);
        logRequest("POST", requestUrl, body);
        return executeTypedRequest(
                "POST",
                requestUrl,
                responseType,
                () -> restClient.post()
                        .uri(requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders -> requestHeaders.forEach(httpHeaders::set))
                        .body(requestBody)
                        .retrieve()
                        .toEntity(String.class));
    }

    /**
     * 兼容原有字符串 POST 调用。请求失败不会抛出外部调用异常。
     */
    public HttpResult post(String url, String body, Map<String, String> headers) {
        String requestUrl = requireUrl(url);
        Map<String, String> requestHeaders = normalizedHeaders(headers);
        logRequest("POST", requestUrl, body);
        return executeTextResult(
                "POST",
                requestUrl,
                () -> restClient.post()
                        .uri(requestUrl)
                        .headers(httpHeaders -> requestHeaders.forEach(httpHeaders::set))
                        .body(body)
                        .retrieve()
                        .toEntity(String.class));
    }

    /**
     * 发送原始字节 PUT。预签名上传不自动重试，避免网络结果不明确时重复写入同一分片。
     */
    public HttpResult putBytes(String url, byte[] body, Map<String, String> headers) {
        String requestUrl = requireUrl(url);
        Map<String, String> requestHeaders = normalizedHeaders(headers);
        Objects.requireNonNull(body, "body must not be null");
        logRequest("PUT", requestUrl, null);
        return executeTextResult(
                "PUT",
                requestUrl,
                () -> restClient.put()
                        .uri(requestUrl)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .headers(httpHeaders -> requestHeaders.forEach(httpHeaders::set))
                        .body(body)
                        .retrieve()
                        .toEntity(String.class));
    }

    private HttpResult doGetResult(String url, Map<String, String> headers) {
        logRequest("GET", url, null);
        return executeTextResult(
                "GET",
                url,
                () -> restClient.get()
                        .uri(url)
                        .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                        .retrieve()
                        .toEntity(String.class));
    }

    /**
     * 结果型调用在此统一把 HTTP 状态和传输异常转换为值对象，调用方自行决定业务语义。
     */
    private HttpResult executeTextResult(
            String method,
            String url,
            Supplier<ResponseEntity<String>> request) {
        long startedAt = System.nanoTime();
        try {
            ResponseEntity<String> response = request.get();
            logResponse(method, url, response, startedAt);
            return new HttpResult(response.getStatusCode().value(), response.getBody(), null);
        } catch (RestClientResponseException exception) {
            logFailure(method, url, exception.getStatusCode().value(), exception, startedAt);
            return new HttpResult(exception.getStatusCode().value(),
                    exception.getResponseBodyAsString(), exception);
        } catch (RuntimeException exception) {
            logFailure(method, url, -1, exception, startedAt);
            return new HttpResult(-1, null, exception);
        }
    }

    private ByteResult doGetBytesResult(String url, Map<String, String> headers) {
        logRequest("GET", url, null);
        long startedAt = System.nanoTime();
        try {
            ResponseEntity<byte[]> response = restClient.get()
                    .uri(url)
                    .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                    .retrieve()
                    .toEntity(byte[].class);
            logResponse("GET", url, response, startedAt);
            return new ByteResult(response.getStatusCode().value(), response.getBody(), null);
        } catch (RestClientResponseException exception) {
            logFailure("GET", url, exception.getStatusCode().value(), exception, startedAt);
            return new ByteResult(exception.getStatusCode().value(), null, exception);
        } catch (RuntimeException exception) {
            logFailure("GET", url, -1, exception, startedAt);
            return new ByteResult(-1, null, exception);
        }
    }

    private DownloadResult doDownload(
            String url, Map<String, String> headers, Path target) {
        logRequest("GET", url, null);
        long startedAt = System.nanoTime();
        Path temporary = null;
        try {
            Files.createDirectories(target.getParent());
            temporary = Files.createTempFile(
                    target.getParent(), "." + target.getFileName() + ".", ".download");
            Path temporaryFile = temporary;
            DownloadResult result = restClient.get()
                    .uri(url)
                    .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                    .exchange((request, response) -> {
                        int statusCode = response.getStatusCode().value();
                        if (statusCode < 200 || statusCode >= 300) {
                            return new DownloadResult(statusCode, 0, null);
                        }
                        long bytes = 0;
                        try (InputStream input = response.getBody();
                             OutputStream output = Files.newOutputStream(
                                     temporaryFile,
                                     StandardOpenOption.TRUNCATE_EXISTING,
                                     StandardOpenOption.WRITE)) {
                            byte[] buffer = new byte[32 * 1024];
                            int read;
                            while ((read = input.read(buffer)) >= 0) {
                                if (read == 0) {
                                    continue;
                                }
                                output.write(buffer, 0, read);
                                bytes += read;
                            }
                        }
                        return new DownloadResult(statusCode, bytes, null);
                    });
            if (result.isSuccess()) {
                try (FileChannel channel = FileChannel.open(
                        temporary,
                        StandardOpenOption.WRITE)) {
                    channel.force(true);
                }
                try {
                    Files.move(temporary, target,
                            StandardCopyOption.ATOMIC_MOVE,
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (java.nio.file.AtomicMoveNotSupportedException exception) {
                    Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
                }
                temporary = null;
            }
            log.debug("[GET] <<< url={}, status={}, bytes={}, elapsedMs={}",
                    sanitizeUrl(url),
                    result.statusCode(),
                    result.bytes(),
                    TimeUnit.NANOSECONDS.toMillis(elapsedNanos(startedAt)));
            return result;
        } catch (IOException | RuntimeException exception) {
            DownloadResult result = new DownloadResult(-1, 0, exception);
            RuntimeException failure = exception instanceof RuntimeException runtime
                    ? runtime
                    : new IllegalStateException("Streaming download failed", exception);
            logFailure("GET", url, -1, failure, startedAt);
            return result;
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException cleanupFailure) {
                    log.debug("Could not remove temporary download {}", temporary, cleanupFailure);
                }
            }
        }
    }

    private <T> T doGet(String url,
                        Map<String, String> headers,
                        Type responseType) {
        logRequest("GET", url, null);
        return executeTypedRequest(
                "GET",
                url,
                responseType,
                () -> restClient.get()
                        .uri(url)
                        .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                        .retrieve()
                        .toEntity(String.class));
    }

    /**
     * 类型化调用在此统一记录耗时并转换外部 HTTP、超时、传输和载荷异常。
     */
    private <T> T executeTypedRequest(
            String method,
            String url,
            Type responseType,
            Supplier<ResponseEntity<String>> request) {
        long startedAt = System.nanoTime();
        try {
            ResponseEntity<String> response = request.get();
            logResponse(method, url, response, startedAt);
            return deserialize(method, url, response.getBody(), responseType);
        } catch (RuntimeException exception) {
            throw normalizeAndLog(method, url, exception, startedAt);
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
                || StrUtil.isBlank(body)) {
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
        if (properties.isPayloadLoggingEnabled() && ObjectUtil.isNotNull(payload) && log.isDebugEnabled()) {
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
                && ObjectUtil.isNotNull(response.getBody())
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
        if (ObjectUtil.isNull(url)) {
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
        if (ObjectUtil.isNull(value)) {
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
        if (StrUtil.isBlank(url)) {
            throw new IllegalArgumentException("url must not be blank");
        }
        return url;
    }

    private static Map<String, String> normalizedHeaders(Map<String, String> headers) {
        return MapUtil.isEmpty(headers) ? Map.of() : headers;
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
        while (ObjectUtil.isNotNull(current)) {
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
     *
     * @param statusCode HTTP 状态码；未收到响应时为 -1
     * @param body 响应正文
     * @param error 请求过程中捕获的异常
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

    public record ByteResult(int statusCode, byte[] body, Exception error) {

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean isRetryable() {
            return statusCode == -1 || statusCode == 429
                    || statusCode >= 500 && statusCode < 600;
        }
    }

    public record DownloadResult(int statusCode, long bytes, Exception error) {

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean isRetryable() {
            return statusCode == -1 || statusCode == 429
                    || statusCode >= 500 && statusCode < 600;
        }
    }
}
