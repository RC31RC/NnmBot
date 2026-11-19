package com.nnm.bot.common.storage;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

/** 对象存储连接、超时与并发预算。 */
@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.storage")
public class ObjectStorageProperties {

    /** MinIO 服务源地址，不允许附带路径、查询参数或凭据。 */
    private String endpoint = "http://127.0.0.1:9000";
    /** 对象存储访问凭据。 */
    private String accessKey = "local-dev-access-key";
    /** 对象存储访问密钥。 */
    private String secretKey = "local-dev-secret-key";
    /** 目标 bucket；应用不会自动创建。 */
    private String bucket = "nnmbot";
    /** 签名使用的区域标识。 */
    private String region = "us-east-1";
    /** 建连超时。 */
    private Duration connectTimeout = Duration.ofSeconds(5);
    /** 单次读取超时。 */
    private Duration readTimeout = Duration.ofSeconds(60);
    /** 单次写入超时。 */
    private Duration writeTimeout = Duration.ofSeconds(60);
    /** 单次 HTTP 调用总超时。 */
    private Duration callTimeout = Duration.ofMinutes(2);
    /** 对象存储并发请求上限，受部署资源预算约束。 */
    private int maxConcurrentRequests = 2;
    /** 过期对象清理的最大尝试次数。 */
    private int cleanupMaxAttempts = 2;
    /** 清理重试间隔。 */
    private Duration cleanupRetryDelay = Duration.ofMillis(250);

    /** 校验 endpoint、bucket、超时及并发预算。
     *
     * @throws IllegalArgumentException 配置不符合对象存储 SDK 约束
     */
    @PostConstruct
    public void validate() {
        Assert.notBlank(endpoint, "nnm.storage.endpoint is required");
        URI uri = URI.create(endpoint);
        Assert.isTrue(
            ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) && StrUtil.isNotBlank(uri.getHost())
                && uri.getUserInfo() == null && uri.getQuery() == null && uri.getFragment() == null
                && (StrUtil.isEmpty(uri.getPath()) || "/".equals(uri.getPath())),
            "nnm.storage.endpoint must be an HTTP(S) origin");
        Assert.notBlank(accessKey, "nnm.storage.access-key is required");
        Assert.notBlank(secretKey, "nnm.storage.secret-key is required");
        Assert.isTrue(bucket != null && bucket.matches("[a-z0-9][a-z0-9.-]{1,61}[a-z0-9]") && !bucket.contains(".."),
            "nnm.storage.bucket is invalid");
        Assert.notBlank(region, "nnm.storage.region is required");
        validateTimeout(connectTimeout, "connect-timeout");
        validateTimeout(readTimeout, "read-timeout");
        validateTimeout(writeTimeout, "write-timeout");
        validateTimeout(callTimeout, "call-timeout");
        Assert.isTrue(maxConcurrentRequests > 0 && maxConcurrentRequests <= 8,
            "nnm.storage.max-concurrent-requests must be between 1 and 8");
        Assert.isTrue(cleanupMaxAttempts >= 1 && cleanupMaxAttempts <= 3,
            "nnm.storage.cleanup-max-attempts must be between 1 and 3");
        validateTimeout(cleanupRetryDelay, "cleanup-retry-delay");
    }

    private void validateTimeout(Duration timeout, String name) {
        Assert.isTrue(timeout != null && timeout.toMillis() > 0 && timeout.toMillis() <= Integer.MAX_VALUE,
            "nnm.storage.{} must be positive and fit SDK milliseconds", name);
    }
}
