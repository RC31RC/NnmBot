package com.nnm.bot.common.config.http;

import cn.hutool.core.util.ObjectUtil;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * HTTP 客户端配置，可通过 {@code nnm.http.*} 在 application.yml 中覆盖。
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
    /** 超过该耗时的请求记录为慢请求 */
    private Duration slowRequestThreshold = Duration.ofSeconds(3);
    /** 是否记录请求和响应内容，默认关闭以避免泄漏凭据或业务数据 */
    private boolean payloadLoggingEnabled = false;
    /** 请求和响应内容的最大日志长度 */
    private int maxPayloadLogLength = 2048;
    /** 文本和字节响应允许读取的最大字节数 */
    private long maxResponseBodyBytes = 16 * 1024 * 1024L;
    /** 文件下载允许写入的最大字节数 */
    private long maxDownloadBytes = 512 * 1024 * 1024L;
    /** 透传请求 ID 的 Header 名称 */
    private String requestIdHeader = "X-Request-Id";
    /** 从 MDC 读取请求 ID 的键名 */
    private String requestIdMdcKey = "requestId";
    /** 透传链路追踪 Header 的名称；没有对应上下文值时不会添加 */
    private String traceHeader = "traceparent";
    /** 从 MDC 读取链路追踪值的键名 */
    private String traceMdcKey = "traceparent";
    /** 是否发布 HTTP 客户端 Micrometer 指标 */
    private boolean metricsEnabled = true;
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

    public Duration getSlowRequestThreshold() {
        return slowRequestThreshold;
    }

    public void setSlowRequestThreshold(Duration slowRequestThreshold) {
        this.slowRequestThreshold = slowRequestThreshold;
    }

    public boolean isPayloadLoggingEnabled() {
        return payloadLoggingEnabled;
    }

    public void setPayloadLoggingEnabled(boolean payloadLoggingEnabled) {
        this.payloadLoggingEnabled = payloadLoggingEnabled;
    }

    public int getMaxPayloadLogLength() {
        return maxPayloadLogLength;
    }

    public void setMaxPayloadLogLength(int maxPayloadLogLength) {
        this.maxPayloadLogLength = maxPayloadLogLength;
    }

    public long getMaxResponseBodyBytes() {
        return maxResponseBodyBytes;
    }

    public void setMaxResponseBodyBytes(long maxResponseBodyBytes) {
        this.maxResponseBodyBytes = maxResponseBodyBytes;
    }

    public long getMaxDownloadBytes() {
        return maxDownloadBytes;
    }

    public void setMaxDownloadBytes(long maxDownloadBytes) {
        this.maxDownloadBytes = maxDownloadBytes;
    }

    public String getRequestIdHeader() {
        return requestIdHeader;
    }

    public void setRequestIdHeader(String requestIdHeader) {
        this.requestIdHeader = requestIdHeader;
    }

    public String getRequestIdMdcKey() {
        return requestIdMdcKey;
    }

    public void setRequestIdMdcKey(String requestIdMdcKey) {
        this.requestIdMdcKey = requestIdMdcKey;
    }

    public String getTraceHeader() {
        return traceHeader;
    }

    public void setTraceHeader(String traceHeader) {
        this.traceHeader = traceHeader;
    }

    public String getTraceMdcKey() {
        return traceMdcKey;
    }

    public void setTraceMdcKey(String traceMdcKey) {
        this.traceMdcKey = traceMdcKey;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public void setMetricsEnabled(boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
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
        requireNotNegative(slowRequestThreshold, "nnm.http.slow-request-threshold");
        if (maxPayloadLogLength < 128 || maxPayloadLogLength > 65536) {
            throw new IllegalArgumentException(
                    "nnm.http.max-payload-log-length must be between 128 and 65536");
        }
        if (maxResponseBodyBytes < 1 || maxResponseBodyBytes > 1024 * 1024 * 1024L) {
            throw new IllegalArgumentException(
                    "nnm.http.max-response-body-bytes must be between 1 and 1073741824");
        }
        if (maxDownloadBytes < 1 || maxDownloadBytes > 4 * 1024 * 1024 * 1024L) {
            throw new IllegalArgumentException(
                    "nnm.http.max-download-bytes must be between 1 and 4294967296");
        }
        requireText(requestIdHeader, "nnm.http.request-id-header");
        requireText(requestIdMdcKey, "nnm.http.request-id-mdc-key");
        requireText(traceHeader, "nnm.http.trace-header");
        requireText(traceMdcKey, "nnm.http.trace-mdc-key");
    }

    private void requirePositive(Duration value, String name) {
        if (ObjectUtil.isNull(value) || value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }

    private void requireNotNegative(Duration value, String name) {
        if (ObjectUtil.isNull(value) || value.isNegative()) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
    }

    private void requireText(String value, String name) {
        if (ObjectUtil.isNull(value) || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
