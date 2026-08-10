package com.nnm.bot.core.qqofficial.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.time.Duration;

@ConfigurationProperties(prefix = "nnm.bot.qq-official")
public class QqOfficialProperties {

    private boolean enabled;
    private String appId = "";
    private String clientSecret = "";
    private String tokenUrl = "https://bots.qq.com/app/getAppAccessToken";
    private String apiBaseUrl = "https://api.sgroup.qq.com";
    private String gatewayUrl = "";
    private long intents = (1L << 25) | (1L << 26);
    private Duration gatewayConnectTimeout = Duration.ofSeconds(10);
    private Duration reconnectDelay = Duration.ofSeconds(5);

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public long getIntents() {
        return intents;
    }

    public void setIntents(long intents) {
        this.intents = intents;
    }

    public Duration getReconnectDelay() {
        return reconnectDelay;
    }

    public void setReconnectDelay(Duration reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }

    public Duration getGatewayConnectTimeout() {
        return gatewayConnectTimeout;
    }

    public void setGatewayConnectTimeout(Duration gatewayConnectTimeout) {
        this.gatewayConnectTimeout = gatewayConnectTimeout;
    }

    public void validate() {
        if (!enabled) {
            return;
        }
        requireText(appId, "nnm.bot.qq-official.app-id");
        requireText(clientSecret, "nnm.bot.qq-official.client-secret");
        requireText(tokenUrl, "nnm.bot.qq-official.token-url");
        requireText(apiBaseUrl, "nnm.bot.qq-official.api-base-url");
        if (intents < 0) {
            throw new IllegalArgumentException("nnm.bot.qq-official.intents must not be negative");
        }
        if (gatewayConnectTimeout == null || gatewayConnectTimeout.isNegative()
                || gatewayConnectTimeout.isZero()) {
            throw new IllegalArgumentException(
                    "nnm.bot.qq-official.gateway-connect-timeout must be positive");
        }
        if (reconnectDelay == null || reconnectDelay.isNegative()) {
            throw new IllegalArgumentException(
                    "nnm.bot.qq-official.reconnect-delay must not be negative");
        }
    }

    private static void requireText(String value, String name) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(name + " must not be blank when enabled");
        }
    }
}
