package com.nnm.bot.common.http;

import com.nnm.bot.common.config.http.HttpClientProperties;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import static org.assertj.core.api.Assertions.assertThat;

class NnmRestClientFactoryTest {

    @Test
    void keepsErrorResponseAndQueryWhenExchangeUsesConfiguredRootUri() {
        AtomicReference<URI> requestUri = new AtomicReference<>();
        NnmRestClientFactory factory = factory(requestUri, 503, "upstream unavailable");

        TestHttpExchange exchange = factory.createClient(
                URI.create("https://service.test/api/arbys?days=30"),
                TestHttpExchange.class);

        ResponseEntity<String> response = exchange.fetch();

        assertThat(requestUri).hasValue(URI.create("https://service.test/api/arbys?days=30"));
        assertThat(response.getStatusCode().value()).isEqualTo(503);
        assertThat(response.getBody()).isEqualTo("upstream unavailable");
    }

    @Test
    void bindsRequestParametersThroughTheDeclarativeProxy() {
        AtomicReference<URI> requestUri = new AtomicReference<>();
        NnmRestClientFactory factory = factory(requestUri, 200, "ok");

        TestHttpExchange exchange = factory.createClient(
                URI.create("https://service.test/api"), TestHttpExchange.class);

        exchange.fetchWithDays(30);

        assertThat(requestUri).hasValue(URI.create("https://service.test/api?days=30"));
    }

    private static NnmRestClientFactory factory(
            AtomicReference<URI> requestUri, int statusCode, String body) {
        NnmRestClientFactory factory = new NnmRestClientFactory();
        ClientHttpRequestFactory requestFactory = (uri, method) -> {
            requestUri.set(uri);
            MockClientHttpRequest request = new MockClientHttpRequest(method, uri);
            request.setResponse(new MockClientHttpResponse(
                    body.getBytes(StandardCharsets.UTF_8), statusCode));
            return request;
        };
        ReflectionTestUtils.setField(factory, "requestFactory", requestFactory);
        ReflectionTestUtils.setField(factory, "properties", new HttpClientProperties());
        return factory;
    }

    @HttpExchange
    interface TestHttpExchange {

        @GetExchange
        ResponseEntity<String> fetch();

        @GetExchange
        ResponseEntity<String> fetchWithDays(
                @RequestParam("days")
                int days);
    }
}
