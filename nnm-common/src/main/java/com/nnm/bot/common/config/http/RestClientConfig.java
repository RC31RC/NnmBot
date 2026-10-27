package com.nnm.bot.common.config.http;

import java.net.http.HttpClient.Builder;
import com.nnm.bot.common.http.ResponseSizeLimitInterceptor;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * 全局 {@link RestClient} 配置。
 * <p>
 * 底层基于 Java 21 {@link HttpClient} + {@link JdkClientHttpRequestFactory}，连接/读取超时可配。
 * 默认执行标准 SSL 证书校验。确需排查本地代理证书问题时，可临时启用
 * {@code nnm.http.trust-all-certs}。
 * </p>
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(HttpClientProperties.class)
public class RestClientConfig {

    @Resource
    private HttpClientProperties properties;

    @Bean
    @ConditionalOnMissingBean(ClientHttpRequestFactory.class)
    public ClientHttpRequestFactory nnmClientHttpRequestFactory() {
        properties.validate();
        Builder httpClientBuilder = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(properties.getConnectTimeout())
                .followRedirects(HttpClient.Redirect.NORMAL);

        if (properties.isTrustAllCerts()) {
            httpClientBuilder.sslContext(createTrustAllSslContext());
        }

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClientBuilder.build());
        requestFactory.setReadTimeout(properties.getReadTimeout());
        return requestFactory;
    }

    @Bean
    @ConditionalOnMissingBean(RestClient.class)
    public RestClient restClient(ClientHttpRequestFactory requestFactory) {
        properties.validate();

        return RestClient.builder()
                .requestFactory(requestFactory)
                .requestInterceptor(new ResponseSizeLimitInterceptor(properties))
                .build();
    }

    /**
     * 创建仅供临时诊断使用的信任所有证书 SSLContext。
     */
    private SSLContext createTrustAllSslContext() {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            log.error("创建信任所有证书的 SSLContext 失败，将使用默认 SSL 校验", e);
            throw new IllegalStateException("Failed to create trust-all SSL context", e);
        }
    }
}
