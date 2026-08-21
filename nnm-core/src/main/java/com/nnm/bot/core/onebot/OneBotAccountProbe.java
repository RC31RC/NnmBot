package com.nnm.bot.core.onebot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nnm.bot.core.onebot.config.OneBotProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    private final OneBotProperties properties;
    private final ObjectMapper objectMapper;
    private final ThreadPoolTaskExecutor taskExecutor;

    public OneBotAccountProbe(
            OneBotProperties properties,
            ObjectMapper objectMapper,
            @Qualifier("applicationTaskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.taskExecutor = taskExecutor;
    }

    public String probe(String connectionUrl, String accessToken) {
        URI uri = websocketUri(connectionUrl);
        String echo = "nnm-account-probe-" + UUID.randomUUID();
        CompletableFuture<String> result = new CompletableFuture<>();
        AtomicReference<WebSocketSession> sessionRef = new AtomicReference<>();

        StandardWebSocketClient client = new StandardWebSocketClient();
        client.setTaskExecutor(taskExecutor);
        WebSocketHttpHeaders headers = headers(accessToken);
        CompletableFuture<WebSocketSession> connection = client.execute(
                new ProbeHandler(echo, result, sessionRef), headers, uri);
        connection.whenComplete((session, error) -> {
            if (session != null) {
                sessionRef.set(session);
            }
            if (error != null) {
                result.completeExceptionally(error);
            }
        });

        try {
            return result.get(properties.getProbeTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException exception) {
            connection.cancel(true);
            throw new OneBotAccountProbeException(
                    "连接 OneBot 成功后未在限定时间内返回登录账号", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new OneBotAccountProbeException("OneBot 账号探测被中断", exception);
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause() == null ? exception : exception.getCause();
            throw new OneBotAccountProbeException(
                    "无法从 OneBot WebSocket 获取登录账号: " + safeMessage(cause), cause);
        } finally {
            close(sessionRef.get());
        }
    }

    String accountId(String payload, String expectedEcho) throws IOException {
        JsonNode response = objectMapper.readTree(payload);
        if (!expectedEcho.equals(response.path("echo").asText())) {
            return null;
        }
        if (!"ok".equalsIgnoreCase(response.path("status").asText())) {
            throw new OneBotAccountProbeException(
                    "OneBot get_login_info 调用失败，retcode=" + response.path("retcode").asText());
        }
        String accountId = response.path("data").path("user_id").asText().trim();
        try {
            if (Long.parseLong(accountId) <= 0L) {
                throw new NumberFormatException("account id must be positive");
            }
        } catch (NumberFormatException exception) {
            throw new OneBotAccountProbeException(
                    "OneBot get_login_info 响应中缺少有效的 user_id", exception);
        }
        return accountId;
    }

    private final class ProbeHandler extends TextWebSocketHandler {

        private final String echo;
        private final CompletableFuture<String> result;
        private final AtomicReference<WebSocketSession> sessionRef;

        private ProbeHandler(String echo,
                             CompletableFuture<String> result,
                             AtomicReference<WebSocketSession> sessionRef) {
            this.echo = echo;
            this.result = result;
            this.sessionRef = sessionRef;
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            sessionRef.set(session);
            ObjectNode request = objectMapper.createObjectNode()
                    .put("action", "get_login_info")
                    .put("echo", echo);
            request.set("params", objectMapper.createObjectNode());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(request)));
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) {
            try {
                String accountId = accountId(message.getPayload(), echo);
                if (accountId != null) {
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
                result.completeExceptionally(new IllegalStateException(
                        "OneBot WebSocket 已关闭: " + status));
            }
        }
    }

    private static URI websocketUri(String connectionUrl) {
        if (connectionUrl == null || connectionUrl.isBlank()) {
            throw new IllegalArgumentException("OneBot WebSocket 地址不能为空");
        }
        URI uri = URI.create(connectionUrl.trim());
        if (!"ws".equalsIgnoreCase(uri.getScheme())
                && !"wss".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("OneBot WebSocket 地址必须使用 ws 或 wss 协议");
        }
        if (uri.getHost() == null) {
            throw new IllegalArgumentException("OneBot WebSocket 地址缺少有效主机名");
        }
        return uri;
    }

    private static WebSocketHttpHeaders headers(String accessToken) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        if (accessToken != null && !accessToken.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION,
                    accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken);
        }
        return headers;
    }

    private static void close(WebSocketSession session) {
        if (session == null || !session.isOpen()) {
            return;
        }
        try {
            session.close(CloseStatus.NORMAL);
        } catch (IOException ignored) {
            // Probe is already complete; closing failures do not change its result.
        }
    }

    private static String safeMessage(Throwable error) {
        return error.getMessage() == null || error.getMessage().isBlank()
                ? error.getClass().getSimpleName()
                : error.getMessage();
    }
}
