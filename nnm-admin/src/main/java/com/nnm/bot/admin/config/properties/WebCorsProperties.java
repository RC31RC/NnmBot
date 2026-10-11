package com.nnm.bot.admin.config.properties;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理端跨域配置。
 *
 * <p>来源必须显式配置，避免使用 {@code *} 放大管理接口的暴露范围。</p>
 */
@ConfigurationProperties(prefix = "nnm.web.cors")
public class WebCorsProperties {

    private List<String> allowedOrigins = new ArrayList<>(List.of(
            "http://localhost:3006",
            "http://127.0.0.1:3006"
    ));

    private List<String> allowedOriginPatterns = new ArrayList<>();

    private List<String> allowedMethods = new ArrayList<>(List.of(
            "GET",
            "POST",
            "PUT",
            "PATCH",
            "DELETE",
            "OPTIONS"
    ));

    private List<String> allowedHeaders = new ArrayList<>(List.of("*"));

    private boolean allowCredentials;

    private long maxAge = 3600;

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = normalize(allowedOrigins);
    }

    public List<String> getAllowedOriginPatterns() {
        return allowedOriginPatterns;
    }

    public void setAllowedOriginPatterns(List<String> allowedOriginPatterns) {
        this.allowedOriginPatterns = normalize(allowedOriginPatterns);
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = normalize(allowedMethods);
    }

    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = normalize(allowedHeaders);
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    private List<String> normalize(List<String> values) {
        if (CollUtil.isEmpty(values)) {
            return new ArrayList<>();
        }
        return values.stream()
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .toList();
    }
}
