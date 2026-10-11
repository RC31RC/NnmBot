package com.nnm.bot.common.http;

import com.nnm.bot.common.config.http.HttpClientProperties;
import java.net.URI;
import java.util.Objects;

import jakarta.annotation.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * 创建基于统一 HTTP 传输配置的声明式 HTTP 客户端。
 *
 * <p>代理只负责 URI、请求头和参数绑定，重试、日志和结果语义仍由调用方的
 * {@link HttpClient} 统一管理。</p>
 */
@Component
public class NnmRestClientFactory {

    @Resource
    private ClientHttpRequestFactory requestFactory;
    @Resource
    private HttpClientProperties properties;

    /**
     * 创建一个使用指定根地址的 {@code @HttpExchange} 代理。
     * 错误响应被保留为 {@code ResponseEntity}，由上层统一转换为 {@link HttpClient.HttpResult}。
     */
    public <T> T createClient(URI baseUrl, Class<T> serviceType) {
        Objects.requireNonNull(baseUrl, "baseUrl must not be null");
        Objects.requireNonNull(serviceType, "serviceType must not be null");
        validateBaseUrl(baseUrl);

        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .requestInterceptor(new ResponseSizeLimitInterceptor(properties))
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            // 声明式结果型客户端必须保留 4xx/5xx 状态，不能提前抛异常。
                        })
                .build();
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(serviceType);
    }

    private static void validateBaseUrl(URI baseUrl) {
        if (!baseUrl.isAbsolute() || baseUrl.getHost() == null
                || (!"http".equalsIgnoreCase(baseUrl.getScheme())
                && !"https".equalsIgnoreCase(baseUrl.getScheme()))) {
            throw new IllegalArgumentException(
                    "baseUrl must be an absolute HTTP URL: " + baseUrl);
        }
    }
}
