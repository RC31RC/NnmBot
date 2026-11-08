package com.nnm.bot.core.qqofficial.api;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.qqofficial.config.QqOfficialConnection;
import com.nnm.bot.core.qqofficial.api.dto.QqOfficialTokenDTO;
import com.nnm.bot.core.qqofficial.api.dto.QqOfficialTokenRequestDTO;
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

    public QqOfficialTokenClient(HttpClient httpClient, QqOfficialConnection connection) {
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

            QqOfficialTokenDTO response = httpClient.postJson(connection.tokenUrl(),
                new QqOfficialTokenRequestDTO(connection.appId(), connection.clientSecret()), Map.of(),
                QqOfficialTokenDTO.class);
            String token = response == null ? null : response.accessToken();
            if (StrUtil.isBlank(token)) {
                throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                    "QQ 官方机器人 Token 响应缺少 access_token");
            }
            long expiresIn = response.expiresIn() == null ? DEFAULT_EXPIRES_IN_SECONDS : response.expiresIn();
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

}
