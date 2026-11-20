package com.nnm.bot.core.qqofficial.api;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.core.qqofficial.constant.QqOfficialErrorCode;
import com.nnm.bot.core.qqofficial.config.QqOfficialConnection;
import com.nnm.bot.core.qqofficial.api.dto.QqOfficialTokenDTO;
import com.nnm.bot.core.qqofficial.api.dto.QqOfficialTokenRequestDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/** 串行刷新并缓存 QQ 官方应用访问令牌。 */
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

    /**
     * 返回有效令牌；进入提前刷新窗口后同步请求新令牌。
     *
     * @return 当前访问令牌
     */
    public String getAccessToken() {
        tokenLock.lock();
        try {
            if (ObjUtil.isNotNull(accessToken)
                && Instant.now().isBefore(expiresAt.minusSeconds(REFRESH_AHEAD_SECONDS))) {
                return accessToken;
            }

            QqOfficialTokenDTO response = httpClient.postJson(connection.tokenUrl(),
                new QqOfficialTokenRequestDTO(connection.appId(), connection.clientSecret()), Map.of(),
                QqOfficialTokenDTO.class);
            String token = response == null ? null : response.accessToken();
            if (CharSequenceUtil.isBlank(token)) {
                throw new BusinessException(QqOfficialErrorCode.TOKEN_MISSING);
            }
            long expiresIn = response.expiresIn() == null ? DEFAULT_EXPIRES_IN_SECONDS : response.expiresIn();
            accessToken = token;
            expiresAt = Instant.now().plusSeconds(Math.max(60, expiresIn));
            return token;
        } finally {
            tokenLock.unlock();
        }
    }

    /** 清除令牌缓存，使下一次调用重新认证。 */
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
