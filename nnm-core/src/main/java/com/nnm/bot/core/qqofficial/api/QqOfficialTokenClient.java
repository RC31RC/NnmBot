package com.nnm.bot.core.qqofficial.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.core.qqofficial.config.QqOfficialProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
public class QqOfficialTokenClient {

    private static final long DEFAULT_EXPIRES_IN_SECONDS = 7200;
    private static final long REFRESH_AHEAD_SECONDS = 300;

    @Resource
    private HttpClient httpClient;
    @Resource
    private QqOfficialProperties properties;
    private String accessToken;
    private Instant expiresAt = Instant.MIN;

    public synchronized String getAccessToken() {
        if (accessToken != null
                && Instant.now().isBefore(expiresAt.minusSeconds(REFRESH_AHEAD_SECONDS))) {
            return accessToken;
        }

        JsonNode json = httpClient.postJson(
                properties.getTokenUrl(),
                Map.of(
                        "appId", properties.getAppId(),
                        "clientSecret", properties.getClientSecret()
                ),
                Map.of(),
                JsonNode.class);
        String token = text(json, "access_token");
        if (token == null) {
            throw new IllegalStateException("QQ official token response has no access_token");
        }
        long expiresIn = json.path("expires_in").asLong(DEFAULT_EXPIRES_IN_SECONDS);
        accessToken = token;
        expiresAt = Instant.now().plusSeconds(Math.max(60, expiresIn));
        return token;
    }

    public synchronized void clear() {
        accessToken = null;
        expiresAt = Instant.MIN;
    }

    static String text(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        return value == null || value.isNull() || value.asText().isBlank() ? null : value.asText();
    }
}
