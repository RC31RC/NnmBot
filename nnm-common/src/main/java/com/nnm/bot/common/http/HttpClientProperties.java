package com.nnm.bot.common.http;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * HTTP 客户端配置。可通过 {@code nnm.http.*} 在 application.yml 中覆盖。
 * <p>
 * 默认值针对「国内访问海外 Warframe API」场景调高，给慢速跨境链路留足时间。
 * </p>
 */
@ConfigurationProperties(prefix = "nnm.http")
public class HttpClientProperties {

    /** 连接超时 */
    private Duration connectTimeout = Duration.ofSeconds(10);
    /** 读取超时 */
    private Duration readTimeout = Duration.ofSeconds(30);
    /** GET 请求最大尝试次数（含首次） */
    private int maxAttempts = 3;
    /** 重试基础退避间隔 */
    private Duration retryBackoff = Duration.ofMillis(500);
    /** 是否信任所有 SSL 证书（应对国内访问海外 CDN/API 时证书链不完整） */
    private boolean trustAllCerts = true;

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Duration getRetryBackoff() {
        return retryBackoff;
    }

    public void setRetryBackoff(Duration retryBackoff) {
        this.retryBackoff = retryBackoff;
    }

    public boolean isTrustAllCerts() {
        return trustAllCerts;
    }

    public void setTrustAllCerts(boolean trustAllCerts) {
        this.trustAllCerts = trustAllCerts;
    }
}
