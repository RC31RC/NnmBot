package com.nnm.bot.common.http;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.config.http.HttpClientProperties;
import com.nnm.bot.common.retry.RetryBackoff;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.http.HttpTimeoutException;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.ArrayList;
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
    @Resource
    private ObjectProvider<MeterRegistry> meterRegistryProvider;

    public HttpResult get(String url) {
        return get(url, HttpHeaders.EMPTY);
    }

    /**
     * 兼容旧的单值 Header 调用方。重复 Header 场景请使用 {@link HttpHeaders} 重载。
     */
    public HttpResult get(String url, Map<String, String> headers) {
        return get(url, toHttpHeaders(headers));
    }

    /**
     * 发送结果型 GET 请求，调用方自行判断状态码。
     */
    public HttpResult get(String url, HttpHeaders headers) {
        String requestUrl = requireUrl(url);
        HttpHeaders requestHeaders = requestHeaders(headers);
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        HttpResult last = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            last = doGetResult(requestUrl, requestHeaders);
            if (!last.isRetryable() || attempt == maxAttempts) {
                return last;
            }
            logRetry("GET", requestUrl, attempt, maxAttempts, last.statusCode());
            if (!sleepBackoff(attempt, last.headers())) {
                return last;
            }
        }
        return last;
    }

    /**
     * 执行由 {@code @HttpExchange} 代理构造的 GET 请求。
     * 请求仍复用结果型 GET 的重试、退避、日志和异常转换策略。
     */
    public HttpResult executeGet(
            String requestUrl, Supplier<ResponseEntity<String>> request) {
        String url = requireUrl(requestUrl);
        Objects.requireNonNull(request, "request must not be null");
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        HttpResult last = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            logRequest("GET", url, null);
            last = executeTextResult("GET", url, request);
            if (!last.isRetryable() || attempt == maxAttempts) {
                return last;
            }
            logRetry("GET", url, attempt, maxAttempts, last.statusCode());
            if (!sleepBackoff(attempt, last.headers())) {
                return last;
            }
        }
        return last;
    }

    /** 下载需要保留原始字节的响应，例如官方 PublicExport 的 LZMA 和 JSON 文件。 */
    public ByteResult getBytes(String url, Map<String, String> headers) {
        return getBytes(url, toHttpHeaders(headers));
    }

    /** 单次受限下载；在读取过程中执行业务字节上限，HTTP/传输/超限异常直接传播。 */
    public byte[] getLimitedBytes(String url, Map<String, String> headers, long maxBytes) {
        if (maxBytes < 1 || maxBytes >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("maxBytes must be positive and less than Integer.MAX_VALUE");
        }
        String requestUrl = requireUrl(url);
        int limit = Math.toIntExact(Math.min(maxBytes, properties.getMaxResponseBodyBytes()));
        logRequest("GET", requestUrl, null);
        long startedAt = System.nanoTime();
        return restClient.get().uri(requestUrl)
                .headers(target -> copyHeaders(requestHeaders(toHttpHeaders(headers)), target))
                .exchange((request, response) -> {
                    if (response.getHeaders().getContentLength() > limit) {
                        throw new ExternalResponseTooLargeException("GET", sanitizeUrl(requestUrl), limit, null);
                    }
                    byte[] body = response.getBody().readNBytes(limit + 1);
                    if (body.length > limit) {
                        throw new ExternalResponseTooLargeException("GET", sanitizeUrl(requestUrl), limit, null);
                    }
                    int status = response.getStatusCode().value();
                    recordMetric("GET", requestUrl, status, startedAt);
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        throw new ExternalHttpException("GET", sanitizeUrl(requestUrl), status,
                                new String(body, StandardCharsets.UTF_8), response.getHeaders(), null);
                    }
                    return body;
                });
    }

    /**
     * 下载需要保留原始字节的响应。
     */
    public ByteResult getBytes(String url, HttpHeaders headers) {
        String requestUrl = requireUrl(url);
        HttpHeaders requestHeaders = requestHeaders(headers);
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        ByteResult last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            last = doGetBytesResult(requestUrl, requestHeaders);
            if (!last.isRetryable() || attempt == maxAttempts) {
                return last;
            }
            logRetry("GET", requestUrl, attempt, maxAttempts, last.statusCode());
            if (!sleepBackoff(attempt, last.headers())) {
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
        return downloadTo(url, toHttpHeaders(headers), target);
    }

    /**
     * 流式下载到文件，并在响应成功后原子替换目标文件。
     */
    public DownloadResult downloadTo(
            String url, HttpHeaders headers, Path target) {
        return downloadTo(url, headers, target, properties.getMaxDownloadBytes());
    }

    /** 成品下载可使用 manifest 字节数收紧上限；不能突破全局下载预算。 */
    public DownloadResult downloadTo(
            String url, Map<String, String> headers, Path target, long maxBytes) {
        return downloadTo(url, toHttpHeaders(headers), target, maxBytes);
    }

    private DownloadResult downloadTo(
            String url, HttpHeaders headers, Path target, long maxBytes) {
        if (maxBytes < 1) throw new IllegalArgumentException("maxBytes must be positive");
        long downloadLimit = Math.min(maxBytes, properties.getMaxDownloadBytes());
        String requestUrl = requireUrl(url);
        HttpHeaders requestHeaders = requestHeaders(headers);
        Objects.requireNonNull(target, "target must not be null");
        Path normalizedTarget = target.toAbsolutePath().normalize();
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        DownloadResult last = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            last = doDownload(requestUrl, requestHeaders, normalizedTarget, downloadLimit);
            if (!last.isRetryable() || attempt == maxAttempts) {
                return last;
            }
            logRetry("GET", requestUrl, attempt, maxAttempts, last.statusCode());
            if (!sleepBackoff(attempt, last.headers())) {
                return last;
            }
        }
        return last;
    }

    /**
     * 流式下载 UTF-8 文本。先完整写入临时文件，再一次性读取文本，避免 HTTP 客户端
     * 在响应阶段把大 JSON 直接组装为 byte[]，从而暴露截断响应或过高堆内存峰值。
     */
    public TextResult downloadText(String url, Map<String, String> headers) {
        return downloadText(url, toHttpHeaders(headers));
    }

    /**
     * 流式下载 UTF-8 文本。
     */
    public TextResult downloadText(String url, HttpHeaders headers) {
        Path temporary = null;
        try {
            temporary = Files.createTempFile("nnmbot-http-", ".text");
            DownloadResult result = downloadTo(url, headers, temporary);
            if (!result.isSuccess()) {
                return new TextResult(
                        result.statusCode(), null, result.headers(), result.bytes(), result.error());
            }
            String body = Files.readString(temporary, StandardCharsets.UTF_8);
            return new TextResult(
                    result.statusCode(), body, result.headers(), result.bytes(), null);
        } catch (IOException | RuntimeException exception) {
            return new TextResult(-1, null, HttpHeaders.EMPTY, 0, exception);
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException cleanupFailure) {
                    log.debug("Could not remove temporary text download {}", temporary,
                            cleanupFailure);
                }
            }
        }
    }

    public <T> T get(String url, Class<T> responseType) {
        return get(url, HttpHeaders.EMPTY, responseType);
    }

    /**
     * 发起类型化 GET 请求。最终失败时抛出明确的外部 HTTP、超时或传输异常。
     */
    public <T> T get(String url,
                     Map<String, String> headers,
                     Class<T> responseType) {
        return get(url, toHttpHeaders(headers), responseType);
    }

    /**
     * 发起类型化 GET 请求。
     */
    public <T> T get(String url,
                     HttpHeaders headers,
                     Class<T> responseType) {
        return getWithRetry(url, headers, responseType);
    }

    /**
     * 发起支持泛型返回值的 GET 请求。
     */
    public <T> T get(String url,
                     Map<String, String> headers,
                     ParameterizedTypeReference<T> responseType) {
        return get(url, toHttpHeaders(headers), responseType);
    }

    /**
     * 发起支持泛型返回值的 GET 请求。
     */
    public <T> T get(String url,
                     HttpHeaders headers,
                     ParameterizedTypeReference<T> responseType) {
        return getWithRetry(url, headers, responseType.getType());
    }

    private <T> T getWithRetry(String url,
                               HttpHeaders headers,
                               Type responseType) {
        String requestUrl = requireUrl(url);
        HttpHeaders requestHeaders = requestHeaders(headers);
        Objects.requireNonNull(responseType, "responseType must not be null");
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                T result = doGet(requestUrl, requestHeaders, responseType);
                return result;
            } catch (RuntimeException exception) {
                RuntimeException normalized = normalize("GET", requestUrl, exception);
                if (!isRetryable(normalized) || attempt == maxAttempts) {
                    throw normalized;
                }
                logRetry("GET", requestUrl, attempt, maxAttempts, statusCode(normalized));
                if (!sleepBackoff(attempt, retryHeaders(normalized))) {
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
        return postJson(url, body, toHttpHeaders(headers), responseType);
    }

    /**
     * 发起 JSON POST 请求。POST 不自动重试，避免非幂等请求产生重复副作用。
     */
    public <T> T postJson(String url,
                          Object body,
                          HttpHeaders headers,
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
        return postJson(url, body, toHttpHeaders(headers), responseType);
    }

    /**
     * 发起支持泛型返回值的 JSON POST 请求。
     */
    public <T> T postJson(String url,
                          Object body,
                          HttpHeaders headers,
                          ParameterizedTypeReference<T> responseType) {
        return postJson(url, body, headers, responseType.getType(), responseType);
    }

    private <T> T postJson(String url,
                           Object body,
                           HttpHeaders headers,
                           Type responseType,
                           Object responseTypeReference) {
        String requestUrl = requireUrl(url);
        HttpHeaders requestHeaders = requestHeaders(headers);
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
                        .headers(httpHeaders -> copyHeaders(requestHeaders, httpHeaders))
                        .body(requestBody)
                        .retrieve()
                        .toEntity(String.class));
    }

    /**
     * 兼容原有字符串 POST 调用。请求失败不会抛出外部调用异常。
     */
    public HttpResult post(String url, String body, Map<String, String> headers) {
        return post(url, body, toHttpHeaders(headers));
    }

    /**
     * 发送字符串 POST 请求。请求失败不会抛出外部调用异常。
     */
    public HttpResult post(String url, String body, HttpHeaders headers) {
        String requestUrl = requireUrl(url);
        HttpHeaders requestHeaders = requestHeaders(headers);
        logRequest("POST", requestUrl, body);
        return executeTextResult(
                "POST",
                requestUrl,
                () -> restClient.post()
                        .uri(requestUrl)
                        .headers(httpHeaders -> copyHeaders(requestHeaders, httpHeaders))
                        .body(body)
                        .retrieve()
                        .toEntity(String.class));
    }

    /**
     * 发送原始字节 PUT。预签名上传不自动重试，避免网络结果不明确时重复写入同一分片。
     */
    public HttpResult putBytes(String url, byte[] body, Map<String, String> headers) {
        return putBytes(url, body, toHttpHeaders(headers));
    }

    /**
     * 发送原始字节 PUT。
     */
    public HttpResult putBytes(String url, byte[] body, HttpHeaders headers) {
        String requestUrl = requireUrl(url);
        HttpHeaders requestHeaders = requestHeaders(headers);
        Objects.requireNonNull(body, "body must not be null");
        logRequest("PUT", requestUrl, null);
        return executeTextResult(
                "PUT",
                requestUrl,
                () -> restClient.put()
                        .uri(requestUrl)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .headers(httpHeaders -> copyHeaders(requestHeaders, httpHeaders))
                        .body(body)
                        .retrieve()
                        .toEntity(String.class));
    }

    private HttpResult doGetResult(String url, HttpHeaders headers) {
        logRequest("GET", url, null);
        return executeTextResult(
                "GET",
                url,
                () -> restClient.get()
                        .uri(url)
                        .headers(httpHeaders -> copyHeaders(headers, httpHeaders))
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
            ensureResponseBodySize(method, url, response.getBody());
            logResponse(method, url, response, startedAt);
            recordMetric(method, url, response.getStatusCode().value(), startedAt);
            return new HttpResult(response.getStatusCode().value(), response.getBody(),
                    response.getHeaders(), null);
        } catch (RestClientResponseException exception) {
            ensureResponseBodySize(method, url, exception.getResponseBodyAsByteArray());
            logFailure(method, url, exception.getStatusCode().value(), exception, startedAt);
            recordMetric(method, url, exception.getStatusCode().value(), startedAt);
            return new HttpResult(exception.getStatusCode().value(),
                    exception.getResponseBodyAsString(), exception.getResponseHeaders(), exception);
        } catch (RuntimeException exception) {
            logFailure(method, url, -1, exception, startedAt);
            recordMetric(method, url, -1, startedAt);
            return new HttpResult(-1, null, exception);
        }
    }

    private ByteResult doGetBytesResult(String url, HttpHeaders headers) {
        logRequest("GET", url, null);
        long startedAt = System.nanoTime();
        try {
            ResponseEntity<byte[]> response = restClient.get()
                    .uri(url)
                    .headers(httpHeaders -> copyHeaders(headers, httpHeaders))
                    .retrieve()
                    .toEntity(byte[].class);
            ensureResponseBodySize("GET", url, response.getBody());
            logResponse("GET", url, response, startedAt);
            recordMetric("GET", url, response.getStatusCode().value(), startedAt);
            return new ByteResult(response.getStatusCode().value(), response.getBody(),
                    response.getHeaders(), null);
        } catch (RestClientResponseException exception) {
            ensureResponseBodySize("GET", url, exception.getResponseBodyAsByteArray());
            logFailure("GET", url, exception.getStatusCode().value(), exception, startedAt);
            recordMetric("GET", url, exception.getStatusCode().value(), startedAt);
            return new ByteResult(exception.getStatusCode().value(), null,
                    exception.getResponseHeaders(), exception);
        } catch (RuntimeException exception) {
            logFailure("GET", url, -1, exception, startedAt);
            recordMetric("GET", url, -1, startedAt);
            return new ByteResult(-1, null, exception);
        }
    }

    private DownloadResult doDownload(
            String url, HttpHeaders headers, Path target, long downloadLimit) {
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
                    .attribute(ResponseSizeLimitInterceptor.STREAMING_DOWNLOAD, true)
                    .headers(httpHeaders -> copyHeaders(headers, httpHeaders))
                    .exchange((request, response) -> {
                        int statusCode = response.getStatusCode().value();
                        HttpHeaders responseHeaders = copyHeaders(response.getHeaders());
                        if (statusCode < 200 || statusCode >= 300) {
                            drainAndCheck(response, url, properties.getMaxResponseBodyBytes());
                            return new DownloadResult(statusCode, 0, responseHeaders, null);
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
                                if (bytes > downloadLimit - read) {
                                    throw new ExternalResponseTooLargeException(
                                            "GET", sanitizeUrl(url),
                                            downloadLimit, null);
                                }
                                output.write(buffer, 0, read);
                                bytes += read;
                            }
                        }
                        return new DownloadResult(statusCode, bytes, responseHeaders, null);
                    });
            if (result.isSuccess()) {
                try (FileChannel channel = FileChannel.open(
                        temporary,
                        StandardOpenOption.WRITE)) {
                    channel.force(true);
                }
                // 不支持原子替换时直接失败，不能降级为可能损坏原文件的覆盖写入。
                Files.move(temporary, target,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
                temporary = null;
            }
            log.debug("[GET] <<< url={}, status={}, bytes={}, elapsedMs={}",
                    sanitizeUrl(url),
                    result.statusCode(),
                    result.bytes(),
                    TimeUnit.NANOSECONDS.toMillis(elapsedNanos(startedAt)));
            recordMetric("GET", url, result.statusCode(), startedAt);
            return result;
        } catch (IOException | RuntimeException exception) {
            DownloadResult result = new DownloadResult(-1, 0, HttpHeaders.EMPTY, exception);
            RuntimeException failure = exception instanceof RuntimeException runtime
                    ? runtime
                    : new IllegalStateException("Streaming download failed", exception);
            logFailure("GET", url, -1, failure, startedAt);
            recordMetric("GET", url, -1, startedAt);
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
                        HttpHeaders headers,
                        Type responseType) {
        logRequest("GET", url, null);
        return executeTypedRequest(
                "GET",
                url,
                responseType,
                () -> restClient.get()
                        .uri(url)
                        .headers(httpHeaders -> copyHeaders(headers, httpHeaders))
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
            ensureResponseBodySize(method, url, response.getBody());
            logResponse(method, url, response, startedAt);
            T result = deserialize(method, url, response.getBody(), responseType);
            recordMetric(method, url, response.getStatusCode().value(), startedAt);
            return result;
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
        recordMetric(method, url, statusCode(normalized), startedAt);
        return normalized;
    }

    private RuntimeException normalize(String method,
                                       String url,
                                       RuntimeException exception) {
        // 消息转换器可能包装读取异常，超限不能被误判为可重试的网络故障。
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ExternalResponseTooLargeException tooLarge) {
                return tooLarge;
            }
        }
        if (exception instanceof ExternalHttpException
                || exception instanceof ExternalTimeoutException
                || exception instanceof ExternalTransportException
                || exception instanceof ExternalPayloadException
                || exception instanceof ExternalResponseTooLargeException) {
            return exception;
        }
        String safeUrl = sanitizeUrl(url);
        if (exception instanceof RestClientResponseException responseException) {
            return new ExternalHttpException(
                    method,
                    safeUrl,
                    responseException.getStatusCode().value(),
                    truncate(responseException.getResponseBodyAsString()),
                    responseException.getResponseHeaders(),
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
        if (statusCode == 404) {
            log.debug("[{}] <<< url={}, status=404, elapsedMs={}",
                    method,
                    sanitizeUrl(url),
                    TimeUnit.NANOSECONDS.toMillis(elapsedNanos(startedAt)));
            return;
        }
        Throwable cause = mostSpecificCause(exception);
        log.warn("[{}] <<< url={}, status={}, elapsedMs={}, error={}, cause={}, message={}",
                method,
                sanitizeUrl(url),
                statusCode,
                TimeUnit.NANOSECONDS.toMillis(elapsedNanos(startedAt)),
                exception.getClass().getSimpleName(),
                cause.getClass().getSimpleName(),
                safeFailureMessage(cause.getMessage(), url));
        log.debug("[{}] external HTTP failure details", method, exception);
    }

    /**
     * 找到最底层异常，WARN 日志可以直接区分连接超时、读取超时、TLS 和 DNS 等问题。
     */
    static Throwable mostSpecificCause(Throwable throwable) {
        Throwable current = Objects.requireNonNull(throwable, "throwable must not be null");
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    /**
     * 异常消息可能包含完整请求地址，因此写入日志前统一移除查询参数、片段和换行。
     */
    static String safeFailureMessage(String message, String requestUrl) {
        return HttpDiagnostics.safeFailureMessage(message, requestUrl);
    }

    private void logRetry(String method,
                          String url,
                          int attempt,
                          int maxAttempts,
                          int statusCode) {
        log.warn("[{}] url={} attempt={}/{} failed(status={}), retrying",
                method, sanitizeUrl(url), attempt, maxAttempts, statusCode);
    }

    private boolean sleepBackoff(int attempt, HttpHeaders responseHeaders) {
        try {
            Duration exponential = RetryBackoff.exponential(
                    properties.getRetryBackoff(),
                    properties.getRetryMaxBackoff(),
                    attempt
            );
            Duration retryAfter = retryAfter(responseHeaders);
            if (retryAfter != null && retryAfter.compareTo(properties.getRetryMaxBackoff()) > 0) {
                // 上游要求的等待超出本次同步调用预算时停止重试，不能提前再请求。
                return false;
            }
            Duration wait = retryAfter == null || retryAfter.compareTo(exponential) < 0
                    ? exponential
                    : retryAfter;
            Thread.sleep(wait.toMillis());
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    static String sanitizeUrl(String url) {
        return HttpDiagnostics.safeUrl(url);
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

    private HttpHeaders requestHeaders(HttpHeaders headers) {
        HttpHeaders result = new HttpHeaders();
        copyHeaders(headers, result);
        if (result.getFirst(properties.getRequestIdHeader()) == null) {
            String requestId = MDC.get(properties.getRequestIdMdcKey());
            if (StrUtil.isNotBlank(requestId)) {
                result.set(properties.getRequestIdHeader(), requestId);
            }
        }
        if (result.getFirst(properties.getTraceHeader()) == null) {
            String trace = MDC.get(properties.getTraceMdcKey());
            if (StrUtil.isNotBlank(trace)) {
                result.set(properties.getTraceHeader(), trace);
            }
        }
        return result;
    }

    private static HttpHeaders toHttpHeaders(Map<String, String> headers) {
        HttpHeaders result = new HttpHeaders();
        if (headers != null) {
            headers.forEach((name, value) -> {
                if (name != null && value != null) {
                    result.add(name, value);
                }
            });
        }
        return result;
    }

    private static HttpHeaders copyHeaders(HttpHeaders source) {
        HttpHeaders result = new HttpHeaders();
        copyHeaders(source, result);
        return immutableHeaders(result);
    }

    private static void copyHeaders(HttpHeaders source, HttpHeaders target) {
        if (source != null && !source.isEmpty()) {
            source.forEach((name, values) -> target.put(name, new ArrayList<>(values)));
        }
    }

    private static HttpHeaders immutableHeaders(HttpHeaders headers) {
        if (headers == null || headers.isEmpty()) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders copy = new HttpHeaders();
        copyHeaders(headers, copy);
        return HttpHeaders.readOnlyHttpHeaders(copy);
    }

    private void ensureResponseBodySize(String method, String url, String body) {
        if (body == null) {
            return;
        }
        ensureResponseBodySize(method, url, body.getBytes(StandardCharsets.UTF_8));
    }

    private void ensureResponseBodySize(String method, String url, byte[] body) {
        if (body != null && body.length > properties.getMaxResponseBodyBytes()) {
            throw new ExternalResponseTooLargeException(
                    method, sanitizeUrl(url), properties.getMaxResponseBodyBytes(), null);
        }
    }

    private void drainAndCheck(ClientHttpResponse response, String url, long maxBytes)
            throws IOException {
        long contentLength = response.getHeaders().getContentLength();
        if (contentLength > maxBytes) {
            throw new ExternalResponseTooLargeException(
                    "GET", sanitizeUrl(url), maxBytes, null);
        }
        long bytes = 0;
        byte[] buffer = new byte[8192];
        int read;
        try (InputStream input = response.getBody()) {
            while ((read = input.read(buffer)) >= 0) {
                if (read == 0) {
                    continue;
                }
                bytes += read;
                if (bytes > maxBytes) {
                    throw new ExternalResponseTooLargeException(
                            "GET", sanitizeUrl(url), maxBytes, null);
                }
            }
        }
    }

    private static HttpHeaders retryHeaders(RuntimeException exception) {
        if (exception instanceof ExternalHttpException httpException) {
            return httpException.getResponseHeaders();
        }
        return HttpHeaders.EMPTY;
    }

    private Duration retryAfter(HttpHeaders headers) {
        if (headers == null) {
            return null;
        }
        String value = headers.getFirst(HttpHeaders.RETRY_AFTER);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        try {
            long seconds = Long.parseLong(value.trim());
            if (seconds < 0) {
                return null;
            }
            return Duration.ofSeconds(seconds);
        } catch (NumberFormatException ignored) {
            try {
                Instant retryAt = ZonedDateTime.parse(
                        value, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
                Duration delay = Duration.between(Instant.now(), retryAt);
                return delay.isNegative() ? Duration.ZERO : delay;
            } catch (DateTimeParseException ignoredDate) {
                return null;
            }
        }
    }

    /** 每次实际请求记一条指标，包含失败尝试，不包含重试间隔。 */
    private void recordMetric(String method, String url, int statusCode, long startedAt) {
        if (!properties.isMetricsEnabled()) {
            return;
        }
        meterRegistryProvider.ifAvailable(registry -> {
            String host;
            try {
                host = URI.create(url).getHost();
            } catch (IllegalArgumentException exception) {
                host = null;
            }
            String outcome = statusCode >= 200 && statusCode < 300
                    ? "success"
                    : statusCode < 0 ? "transport_error" : "http_error";
            Timer.builder("nnm.http.client.requests")
                    .tag("method", method)
                    .tag("host", host == null ? "unknown" : host)
                    .tag("status", String.valueOf(statusCode))
                    .tag("outcome", outcome)
                    .register(registry)
                    .record(Duration.ofNanos(elapsedNanos(startedAt)));
        });
    }

    private static boolean isRetryable(RuntimeException exception) {
        return exception instanceof ExternalTimeoutException
                || exception instanceof ExternalTransportException
                || exception instanceof ExternalHttpException httpException
                && httpException.isRetryable();
    }

    private static boolean isRetryableResult(int status, Exception error) {
        if (error != null && containsCause(error, ExternalResponseTooLargeException.class)) {
            return false;
        }
        if (status == 429 || status >= 500 && status < 600) {
            return true;
        }
        // 本地磁盘错误、参数错误和响应超限都不会因重发请求而恢复。
        return status == -1 && (error instanceof ResourceAccessException
                || error instanceof ExternalTimeoutException
                || error instanceof ExternalTransportException);
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
    public record HttpResult(
            int statusCode,
            String body,
            HttpHeaders headers,
            Exception error) {

        public HttpResult(int statusCode, String body, Exception error) {
            this(statusCode, body, HttpHeaders.EMPTY, error);
        }

        public HttpResult {
            headers = immutableHeaders(headers);
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean isTooManyRequests() {
            return statusCode == 429;
        }

        public boolean isRetryable() {
            return isRetryableResult(statusCode, error);
        }

        public boolean is2xx() {
            return HttpStatusCode.valueOf(statusCode == -1 ? 599 : statusCode)
                    .is2xxSuccessful();
        }
    }

    public record ByteResult(
            int statusCode,
            byte[] body,
            HttpHeaders headers,
            Exception error) {

        public ByteResult(int statusCode, byte[] body, Exception error) {
            this(statusCode, body, HttpHeaders.EMPTY, error);
        }

        public ByteResult {
            headers = immutableHeaders(headers);
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean isRetryable() {
            return isRetryableResult(statusCode, error);
        }
    }

    public record TextResult(
            int statusCode,
            String body,
            HttpHeaders headers,
            long bytes,
            Exception error) {

        public TextResult(int statusCode, String body, Exception error) {
            this(statusCode, body, HttpHeaders.EMPTY, 0, error);
        }

        public TextResult {
            headers = immutableHeaders(headers);
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean isRetryable() {
            return isRetryableResult(statusCode, error);
        }
    }

    public record DownloadResult(
            int statusCode,
            long bytes,
            HttpHeaders headers,
            Exception error) {

        public DownloadResult(int statusCode, long bytes, Exception error) {
            this(statusCode, bytes, HttpHeaders.EMPTY, error);
        }

        public DownloadResult {
            headers = immutableHeaders(headers);
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean isRetryable() {
            return isRetryableResult(statusCode, error);
        }
    }
}
