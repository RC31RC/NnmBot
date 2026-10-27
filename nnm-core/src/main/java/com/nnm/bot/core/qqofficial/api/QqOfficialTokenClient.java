package com.nnm.bot.core.qqofficial.api;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.qqofficial.config.QqOfficialConnection;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class QqOfficialTokenClient {
    private final ReentrantLock tokenLock = new ReentrantLock();

    private static final long DEFAULT_EXPIRES_IN_SECONDS = 7200;
    private static final long REFRESH_AHEAD_SECONDS = 300;

    private final HttpClient httpClient;
    private final QqOfficialConnection connection;
    private String accessToken;
    private Instant expiresAt = Instant.MIN;

    public QqOfficialTokenClient(HttpClient httpClient,
                                 QqOfficialConnection connection) {
        this.httpClient = httpClient;
        this.connection = connection;
    }

    public String getAccessToken() {
        tokenLock.lock();
        try {
            if (ObjectUtil.isNotNull(accessToken)
                    && Instant.now().isBefore(expiresAt.minusSeconds(REFRESH_AHEAD_SECONDS))) {
                return accessToken;
            }

            JsonNode json = httpClient.postJson(
                    connection.tokenUrl(),
                    Map.of(
                            "appId", connection.appId(),
                            "clientSecret", connection.clientSecret()
                    ),
                    Map.of(),
                    JsonNode.class);
            String token = text(json, "access_token");
            if (ObjectUtil.isNull(token)) {
                throw new BusinessException(
                        BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                        "QQ 官方机器人 Token 响应缺少 access_token");
            }
            long expiresIn = json.path("expires_in").asLong(DEFAULT_EXPIRES_IN_SECONDS);
            accessToken = token;
            expiresAt = Instant.now().plusSeconds(Math.max(60, expiresIn));
            return token;
        } finally {
            tokenLock.unlock();
        }
    }

    public void clear() {
        tokenLock.lock();
        try {
            accessToken = null;
            expiresAt = Instant.MIN;
        } finally {
            tokenLock.unlock();
        }
    }

    static String text(JsonNode node, String field) {
        JsonNode value = ObjectUtil.isNull(node) ? null : node.get(field);
        return ObjectUtil.isNull(value) || value.isNull() || StrUtil.isBlank(value.asText())
                ? null
                : value.asText();
    }
}
