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
    /** 单次重试退避上限 */
    private Duration retryMaxBackoff = Duration.ofSeconds(5);
    /**
     * 是否跳过 SSL 证书校验。仅用于定位本地网络或代理证书问题，
     * 正式环境必须保持关闭。
     */
    private boolean trustAllCerts = false;

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

    public Duration getRetryMaxBackoff() {
        return retryMaxBackoff;
    }

    public void setRetryMaxBackoff(Duration retryMaxBackoff) {
        this.retryMaxBackoff = retryMaxBackoff;
    }

    public boolean isTrustAllCerts() {
        return trustAllCerts;
    }

    public void setTrustAllCerts(boolean trustAllCerts) {
        this.trustAllCerts = trustAllCerts;
    }

    void validate() {
        requirePositive(connectTimeout, "nnm.http.connect-timeout");
        requirePositive(readTimeout, "nnm.http.read-timeout");
        if (maxAttempts < 1 || maxAttempts > 10) {
            throw new IllegalArgumentException("nnm.http.max-attempts must be between 1 and 10");
        }
        requireNotNegative(retryBackoff, "nnm.http.retry-backoff");
        requireNotNegative(retryMaxBackoff, "nnm.http.retry-max-backoff");
        if (retryMaxBackoff.compareTo(retryBackoff) < 0) {
            throw new IllegalArgumentException(
                    "nnm.http.retry-max-backoff must not be shorter than retry-backoff");
        }
    }

    private void requirePositive(Duration value, String name) {
        if (value == null || value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }

    private void requireNotNegative(Duration value, String name) {
        if (value == null || value.isNegative()) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
    }
}
