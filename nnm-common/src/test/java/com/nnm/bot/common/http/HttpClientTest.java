package com.nnm.bot.common.http;

import com.nnm.bot.common.http.HttpClient.ByteResult;
import com.nnm.bot.common.http.HttpClient.DownloadResult;
import com.nnm.bot.common.http.HttpClient.HttpResult;
import com.nnm.bot.common.http.HttpClient.TextResult;
import java.io.OutputStream;
import java.util.stream.Stream;
import org.slf4j.MDC.MDCCloseable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.config.http.HttpClientProperties;
import com.nnm.bot.common.config.http.RestClientConfig;
import java.util.Map;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.service.annotation.GetExchange;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.http.HttpConnectTimeoutException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpClientTest {
    @TempDir
    Path directory;

    private HttpServer server;
    private String baseUrl;
    private HttpClient client;
    private NnmRestClientFactory factory;
    private final HttpClientProperties properties = new HttpClientProperties();
    private final AtomicInteger requests = new AtomicInteger();

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        properties.setMaxResponseBodyBytes(8);
        properties.setMaxDownloadBytes(16);
        properties.setRetryBackoff(Duration.ZERO);
        properties.setRetryMaxBackoff(Duration.ZERO);
        properties.setMetricsEnabled(false);
        RestClientConfig config = new RestClientConfig();
        ReflectionTestUtils.setField(config, "properties", properties);
        ClientHttpRequestFactory requestFactory = config.nnmClientHttpRequestFactory();
        client = new HttpClient();
        ReflectionTestUtils.setField(client, "restClient", config.restClient(requestFactory));
        ReflectionTestUtils.setField(client, "properties", properties);
        ReflectionTestUtils.setField(client, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(client, "meterRegistryProvider",
                new DefaultListableBeanFactory().getBeanProvider(MeterRegistry.class));
        factory = new NnmRestClientFactory();
        ReflectionTestUtils.setField(factory, "requestFactory", requestFactory);
        ReflectionTestUtils.setField(factory, "properties", properties);
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void stopsChunkedResponseAndDoesNotRetrySizeFailure() {
        respond("/large", 200, 64, true);
        HttpResult result = client.get(baseUrl + "/large");
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isRetryable()).isFalse();
        assertThat(result.error()).isInstanceOf(ExternalResponseTooLargeException.class);
        assertThat(requests).hasValue(1);
    }

    @Test
    void typedResponseKeepsSizeFailureNonRetryable() {
        respond("/large", 200, 64, true);
        assertThatThrownBy(() -> client.get(baseUrl + "/large", String.class))
                .isInstanceOf(ExternalResponseTooLargeException.class);
        assertThat(requests).hasValue(1);
    }

    @Test
    void propagatesCurrentRequestContextWithoutOverwritingExplicitHeader() {
        AtomicReference<HttpHeaders> receivedHeaders = new AtomicReference<>();
        server.createContext("/headers", exchange -> {
            HttpHeaders received = new HttpHeaders();
            exchange.getRequestHeaders().forEach(received::addAll);
            receivedHeaders.set(received);
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", "explicit");
        headers.add("X-Multi", "one");
        headers.add("X-Multi", "two");
        try (MDCCloseable request = MDC.putCloseable("requestId", "context");
             MDCCloseable trace = MDC.putCloseable("traceparent", "trace")) {
            assertThat(client.get(baseUrl + "/headers", headers).isSuccess()).isTrue();
        }
        assertThat(receivedHeaders.get().getFirst("X-Request-Id")).isEqualTo("explicit");
        assertThat(receivedHeaders.get().getFirst("traceparent")).isEqualTo("trace");
        assertThat(receivedHeaders.get().get("X-Multi")).containsExactly("one", "two");
    }

    @Test
    void recordsEveryAttemptIncludingRetries() {
        properties.setMetricsEnabled(true);
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        try {
            DefaultListableBeanFactory beans = new DefaultListableBeanFactory();
            beans.registerSingleton("meterRegistry", registry);
            ReflectionTestUtils.setField(client, "meterRegistryProvider",
                    beans.getBeanProvider(MeterRegistry.class));
            respond("/retry", 503, 0, true);
            client.get(baseUrl + "/retry");
            assertThat(registry.get("nnm.http.client.requests").tag("status", "503")
                    .timer().count()).isEqualTo(3);
            assertThat(requests).hasValue(3);
            respond("/write", 200, 0, true);
            client.post(baseUrl + "/write", "data", HttpHeaders.EMPTY);
            client.putBytes(baseUrl + "/write", new byte[1], HttpHeaders.EMPTY);
            assertThat(registry.get("nnm.http.client.requests").tag("method", "POST")
                    .timer().count()).isEqualTo(1);
            assertThat(registry.get("nnm.http.client.requests").tag("method", "PUT")
                    .timer().count()).isEqualTo(1);
        } finally {
            registry.close();
        }
    }

    @Test
    void rejectsKnownLengthBeforeConvertingBytes() {
        respond("/large", 200, 64, false);
        ByteResult result = client.getBytes(baseUrl + "/large", HttpHeaders.EMPTY);
        assertThat(result.error()).isInstanceOf(ExternalResponseTooLargeException.class);
        assertThat(result.isRetryable()).isFalse();
        assertThat(requests).hasValue(1);
    }

    @Test
    void businessDownloadLimitAppliesToChunkedBodiesAndPropagatesHttpErrors() {
        respond("/limited", 200, 7, true);
        assertThatThrownBy(() -> client.getLimitedBytes(baseUrl + "/limited", Map.of(), 4))
                .isInstanceOf(ExternalResponseTooLargeException.class);
        respond("/exact", 200, 4, true);
        assertThat(client.getLimitedBytes(baseUrl + "/exact", Map.of(), 4)).hasSize(4);
        respond("/missing", 404, 4, true);
        assertThatThrownBy(() -> client.getLimitedBytes(baseUrl + "/missing", Map.of(), 4))
                .isInstanceOf(ExternalHttpException.class);
    }

    @Test
    void boundsErrorBodiesAndDeclarativeResponses() {
        respond("/large", 503, 64, true);
        assertThat(client.get(baseUrl + "/large").isRetryable()).isFalse();
        LargeApi api = factory.createClient(URI.create(baseUrl), LargeApi.class);
        assertThatThrownBy(api::large).isInstanceOf(ExternalResponseTooLargeException.class);
        assertThat(requests).hasValue(2);
    }

    @Test
    void allowsExactlyTheConfiguredLimit() {
        respond("/small", 200, 8, true);
        assertThat(client.get(baseUrl + "/small").body()).hasSize(8);
        assertThat(requests).hasValue(1);
    }

    @Test
    void keepsExistingFileAndRemovesTemporaryFileOnOversizedDownload() throws IOException {
        Path target = directory.resolve("data.bin");
        Files.writeString(target, "original");
        respond("/large", 200, 64, true);
        DownloadResult result = client.downloadTo(baseUrl + "/large", HttpHeaders.EMPTY, target);
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isRetryable()).isFalse();
        assertThat(Files.readString(target)).isEqualTo("original");
        assertThat(requests).hasValue(1);
        try (Stream<Path> files = Files.list(directory)) {
            assertThat(files.toList()).containsExactly(target);
        }
    }

    @Test
    void streamsDownloadUsingSeparateLimit() throws IOException {
        respond("/file", 200, 16, false);
        Path target = directory.resolve("data.bin");
        assertThat(client.downloadTo(baseUrl + "/file", HttpHeaders.EMPTY, target).isSuccess()).isTrue();
        assertThat(Files.size(target)).isEqualTo(16);
    }

    @Test
    void narrowsStreamingLimitWithoutOverwritingExistingFile() throws IOException {
        respond("/file", 200, 12, true);
        Path target = directory.resolve("bounded.bin");
        Files.writeString(target, "previous");
        DownloadResult result = client.downloadTo(baseUrl + "/file", Map.of(), target, 8);
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.error()).isInstanceOf(ExternalResponseTooLargeException.class);
        assertThat(result.isRetryable()).isFalse();
        assertThat(Files.readString(target)).isEqualTo("previous");
        assertThat(requests).hasValue(1);
    }

    @Test
    void businessDownloadLimitCannotExceedGlobalLimit() {
        respond("/file", 200, 20, true);
        DownloadResult result = client.downloadTo(baseUrl + "/file", Map.of(), directory.resolve("bounded.bin"), 32);
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.error()).isInstanceOf(ExternalResponseTooLargeException.class);
        assertThat(requests).hasValue(1);
    }

    @Test
    void streamsTextWithoutApplyingInMemoryResponseLimit() throws IOException {
        byte[] body = "{\"entries\":[\"中文\",\"complete\"]}".repeat(3)
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);
        properties.setMaxDownloadBytes(body.length);
        server.createContext("/large-text", exchange -> {
            requests.incrementAndGet();
            exchange.getResponseHeaders().set(HttpHeaders.CONTENT_TYPE,
                    "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(body);
            } finally {
                exchange.close();
            }
        });

        TextResult result = client.downloadText(
                baseUrl + "/large-text", HttpHeaders.EMPTY);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.bytes()).isEqualTo(body.length);
        assertThat(result.body()).isEqualTo(new String(body, java.nio.charset.StandardCharsets.UTF_8));
        assertThat(requests).hasValue(1);
    }

    @Test
    void doesNotRetryBeforeServerRetryAfter() {
        server.createContext("/busy", exchange -> {
            requests.incrementAndGet();
            exchange.getResponseHeaders().add("Retry-After", "60");
            exchange.sendResponseHeaders(429, -1);
            exchange.close();
        });
        HttpResult result = client.get(baseUrl + "/busy");
        assertThat(result.statusCode()).isEqualTo(429);
        assertThat(result.headers().getFirst(HttpHeaders.RETRY_AFTER)).isEqualTo("60");
        assertThat(requests).hasValue(1);
    }

    @Test
    void resultsRetainIndependentMultiValueHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Test", "first");
        headers.add("X-Test", "second");
        HttpResult result = new HttpClient.HttpResult(200, "", headers, null);
        headers.add("X-Test", "third");
        assertThat(result.headers().get("X-Test")).containsExactly("first", "second");
    }

    @Test
    void removesCredentialsQueryAndFragmentFromLoggedUrls() {
        assertThat(HttpClient.sanitizeUrl("https://user:secret@localhost/path?token=secret#part"))
                .isEqualTo("https://localhost/path");
    }

    @Test
    void failureLogDetailsExposeRootCauseWithoutLeakingQueryParameters() {
        HttpConnectTimeoutException root = new HttpConnectTimeoutException("HTTP connect timed out");
        IllegalStateException wrapper = new IllegalStateException("request failed", root);

        assertThat(HttpClient.mostSpecificCause(wrapper)).isSameAs(root);
        assertThat(HttpClient.safeFailureMessage(
                "GET https://user:password@service.test/data?token=secret failed\nnow",
                "https://service.test/data?token=secret"))
                .isEqualTo("GET https://service.test/data failed now");
    }

    private void respond(String path, int status, int size, boolean chunked) {
        server.createContext(path, exchange -> {
            requests.incrementAndGet();
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(status, chunked ? 0 : size);
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(new byte[size]);
            } finally {
                exchange.close();
            }
        });
    }

    interface LargeApi {
        @GetExchange("/large")
        ResponseEntity<String> large();
    }
}
