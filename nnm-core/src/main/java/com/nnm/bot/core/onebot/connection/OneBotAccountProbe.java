package com.nnm.bot.core.onebot.connection;

import cn.hutool.core.text.CharSequenceUtil;

import cn.hutool.core.util.ObjUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.core.onebot.constant.OneBotErrorCode;
import com.nnm.bot.core.onebot.constant.OneBotProtocolConstants;
import com.nnm.bot.core.onebot.constant.OneBotProtocolConstants.ResponseStatus;
import com.nnm.bot.core.onebot.connection.dto.OneBotLoginProbeDTO;
import com.nnm.bot.core.onebot.connection.dto.OneBotLoginProbeRequestDTO;
import com.nnm.bot.core.onebot.config.OneBotProperties;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/** 使用 OneBot 11 的 get_login_info 动作探测正向 WebSocket 当前登录账号。 */
@Component
public class OneBotAccountProbe {

    @Resource
    private OneBotProperties properties;
    @Resource
    private ObjectMapper objectMapper;
    @Resource(name = "webSocketConnectExecutor")
    private AsyncTaskExecutor taskExecutor;

    public String probe(String connectionUrl, String accessToken) {
        URI uri = websocketUri(connectionUrl);
        String echo = OneBotProtocolConstants.ECHO_PREFIX + UUID.randomUUID();
        CompletableFuture<String> result = new CompletableFuture<>();
        AtomicReference<WebSocketSession> sessionRef = new AtomicReference<>();

        StandardWebSocketClient client = new StandardWebSocketClient();
        client.setTaskExecutor(this.taskExecutor);
        WebSocketHttpHeaders headers = headers(accessToken);
        CompletableFuture<WebSocketSession> connection =
            client.execute(new ProbeHandler(echo, result, sessionRef), headers, uri);
        connection.whenComplete((session, error) -> {
            if (ObjUtil.isNotNull(session)) {
                sessionRef.set(session);
            }
            if (ObjUtil.isNotNull(error)) {
                result.completeExceptionally(error);
            }
        });

        try {
            return result.get(this.properties.getProbeTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException exception) {
            connection.cancel(true);
            throw new OneBotAccountProbeException("连接 OneBot 成功后未在限定时间内返回登录账号", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new OneBotAccountProbeException("OneBot 账号探测被中断", exception);
        } catch (ExecutionException exception) {
            Throwable cause = ObjUtil.isNull(exception.getCause()) ? exception : exception.getCause();
            throw new OneBotAccountProbeException("无法从 OneBot WebSocket 获取登录账号: " + safeMessage(cause), cause);
        } finally {
            close(sessionRef.get());
        }
    }

    String accountId(String payload, String expectedEcho) throws IOException {
        OneBotLoginProbeDTO response = this.objectMapper.readValue(payload, OneBotLoginProbeDTO.class);
        if (!expectedEcho.equals(response.echo())) {
            return null;
        }
        if (!ResponseStatus.fromCode(response.status()).isSuccess()) {
            throw new OneBotAccountProbeException(
                "OneBot " + OneBotProtocolConstants.ACTION_GET_LOGIN_INFO + " 调用失败，retcode=" + response.retcode());
        }
        String accountId = response.data() == null || response.data().userId() == null ? ""
            : CharSequenceUtil.trim(response.data().userId());
        try {
            if (Long.parseLong(accountId) <= 0L) {
                throw new NumberFormatException("account id must be positive");
            }
        } catch (NumberFormatException exception) {
            throw new OneBotAccountProbeException("OneBot get_login_info 响应中缺少有效的 user_id", exception);
        }
        return accountId;
    }

    private final class ProbeHandler extends TextWebSocketHandler {

        private final String echo;
        private final CompletableFuture<String> result;
        private final AtomicReference<WebSocketSession> sessionRef;

        private ProbeHandler(String echo, CompletableFuture<String> result,
            AtomicReference<WebSocketSession> sessionRef) {
            this.echo = echo;
            this.result = result;
            this.sessionRef = sessionRef;
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            sessionRef.set(session);
            OneBotLoginProbeRequestDTO request = new OneBotLoginProbeRequestDTO(
                OneBotProtocolConstants.ACTION_GET_LOGIN_INFO, new OneBotLoginProbeRequestDTO.EmptyParamsDTO(), echo);
            session.sendMessage(new TextMessage(OneBotAccountProbe.this.objectMapper.writeValueAsString(request)));
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) {
            try {
                String accountId = accountId(message.getPayload(), echo);
                if (ObjUtil.isNotNull(accountId)) {
                    result.complete(accountId);
                }
            } catch (Exception exception) {
                result.completeExceptionally(exception);
            }
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) {
            result.completeExceptionally(exception);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            if (!result.isDone()) {
                result.completeExceptionally(new OneBotAccountProbeException("OneBot WebSocket 已关闭: " + status));
            }
        }
    }

    private static URI websocketUri(String connectionUrl) {
        if (CharSequenceUtil.isBlank(connectionUrl)) {
            throw new BusinessException(OneBotErrorCode.PROBE_URL_REQUIRED);
        }
        URI uri;
        try {
            uri = URI.create(CharSequenceUtil.trim(connectionUrl));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(OneBotErrorCode.PROBE_URL_INVALID, exception);
        }
        if (!OneBotProtocolConstants.WS_SCHEME.equalsIgnoreCase(uri.getScheme())
            && !OneBotProtocolConstants.WSS_SCHEME.equalsIgnoreCase(uri.getScheme())) {
            throw new BusinessException(OneBotErrorCode.PROBE_URL_SCHEME_INVALID);
        }
        if (ObjUtil.isNull(uri.getHost())) {
            throw new BusinessException(OneBotErrorCode.PROBE_URL_HOST_INVALID);
        }
        return uri;
    }

    private static WebSocketHttpHeaders headers(String accessToken) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        if (CharSequenceUtil.isNotBlank(accessToken)) {
            headers.set(HttpHeaders.AUTHORIZATION, accessToken.startsWith(OneBotProtocolConstants.BEARER_PREFIX)
                ? accessToken : OneBotProtocolConstants.BEARER_PREFIX + accessToken);
        }
        return headers;
    }

    private static void close(WebSocketSession session) {
        if (ObjUtil.isNull(session) || !session.isOpen()) {
            return;
        }
        try {
            session.close(CloseStatus.NORMAL);
        } catch (IOException ignored) {
            // 探测已经完成，连接关闭失败不改变探测结果。
        }
    }

    private static String safeMessage(Throwable error) {
        return CharSequenceUtil.isBlank(error.getMessage()) ? error.getClass().getSimpleName() : error.getMessage();
    }
}
