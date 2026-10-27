package com.nnm.bot.common.storage;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.storage")
public class ObjectStorageProperties {

    private String endpoint = "http://127.0.0.1:9000";
    private String accessKey = "local-dev-access-key";
    private String secretKey = "local-dev-secret-key";
    private String bucket = "nnmbot";
    private String region = "us-east-1";
    private Duration connectTimeout = Duration.ofSeconds(5);
    private Duration readTimeout = Duration.ofSeconds(60);
    private Duration writeTimeout = Duration.ofSeconds(60);
    private Duration callTimeout = Duration.ofMinutes(2);
    private int maxConcurrentRequests = 2;

    @PostConstruct
    public void validate() {
        Assert.notBlank(endpoint, "nnm.storage.endpoint is required");
        URI uri = URI.create(endpoint);
        Assert.isTrue(("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))
                        && StrUtil.isNotBlank(uri.getHost()) && uri.getUserInfo() == null
                        && uri.getQuery() == null && uri.getFragment() == null
                        && (StrUtil.isEmpty(uri.getPath()) || "/".equals(uri.getPath())),
                "nnm.storage.endpoint must be an HTTP(S) origin");
        Assert.notBlank(accessKey, "nnm.storage.access-key is required");
        Assert.notBlank(secretKey, "nnm.storage.secret-key is required");
        Assert.isTrue(bucket != null && bucket.matches("[a-z0-9][a-z0-9.-]{1,61}[a-z0-9]")
                        && !bucket.contains(".."), "nnm.storage.bucket is invalid");
        Assert.notBlank(region, "nnm.storage.region is required");
        validateTimeout(connectTimeout, "connect-timeout");
        validateTimeout(readTimeout, "read-timeout");
        validateTimeout(writeTimeout, "write-timeout");
        validateTimeout(callTimeout, "call-timeout");
        Assert.isTrue(maxConcurrentRequests > 0 && maxConcurrentRequests <= 8,
                "nnm.storage.max-concurrent-requests must be between 1 and 8");
    }

    private void validateTimeout(Duration timeout, String name) {
        Assert.isTrue(timeout != null && timeout.toMillis() > 0
                        && timeout.toMillis() <= Integer.MAX_VALUE,
                "nnm.storage.{} must be positive and fit SDK milliseconds", name);
    }
}
