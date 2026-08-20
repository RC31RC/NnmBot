package com.nnm.bot.core.onebot;

import com.nnm.bot.core.onebot.config.OneBotProperties;
import com.nnm.bot.core.channel.BotChannelKey;
import com.nnm.bot.core.channel.BotChannelRegistry;
import com.nnm.bot.core.message.BotPlatform;
import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/** 管理多个 OneBot 正向 WebSocket 连接。 */
@Slf4j
@Component
public class OneBotWebSocketClient implements SmartLifecycle {

    private final OneBotProperties properties;
    private final MultiOneBotWebSocketClientHandler handler;
    private final ThreadPoolTaskScheduler scheduler;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final BotChannelRegistry channelRegistry;

    private final List<ManagedConnection> connections = new ArrayList<>();
    private volatile boolean running;

    public OneBotWebSocketClient(
            OneBotProperties properties,
            MultiOneBotWebSocketClientHandler handler,
            @Qualifier("scheduledExecutor") ThreadPoolTaskScheduler scheduler,
            @Qualifier("applicationTaskExecutor") ThreadPoolTaskExecutor taskExecutor,
            BotChannelRegistry channelRegistry) {
        this.properties = properties;
        this.handler = handler;
        this.scheduler = scheduler;
        this.taskExecutor = taskExecutor;
        this.channelRegistry = channelRegistry;
    }

    @PostConstruct
    void initialize() {
        if (!properties.isEnabled()) {
            return;
        }
        for (OneBotProperties.Instance instance : properties.getInstances()) {
            if (instance.isEnabled()) {
                connections.add(new ManagedConnection(instance));
            }
        }
    }

    @Override
    public void start() {
        if (!properties.isEnabled() || running) {
            return;
        }
        running = true;
        connections.forEach(ManagedConnection::start);
    }

    @Override
    public void stop() {
        stop(() -> { });
    }

    @Override
    public void stop(Runnable callback) {
        running = false;
        connections.forEach(ManagedConnection::stop);
        callback.run();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return properties.isEnabled();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 100;
    }

    @PreDestroy
    void destroy() {
        stop();
    }

    private final class ManagedConnection {

        private final OneBotProperties.Instance instance;
        private final StandardWebSocketClient client;
        private final WebSocketHttpHeaders headers;
        private final URI uri;
        private final AtomicBoolean connected = new AtomicBoolean();
        private final AtomicBoolean connecting = new AtomicBoolean();
        private final BotChannelKey channelKey;
        private volatile CompletableFuture<WebSocketSession> connectionFuture;
        private volatile WebSocketSession session;
        private ScheduledFuture<?> reconnectTask;

        private ManagedConnection(OneBotProperties.Instance instance) {
            this.instance = instance;
            this.channelKey = new BotChannelKey(
                    BotPlatform.ONEBOT, instance.getBotAccountId());
            this.client = new StandardWebSocketClient();
            this.client.setTaskExecutor(taskExecutor);
            this.headers = new WebSocketHttpHeaders();
            this.uri = URI.create(instance.getConnection().getUrl());
            String token = instance.getConnection().getAccessToken();
            if (token != null && !token.isBlank()) {
                headers.set(HttpHeaders.AUTHORIZATION,
                        token.startsWith("Bearer ") ? token : "Bearer " + token);
            }
        }

        private void start() {
            connect();
            reconnectTask = scheduler.scheduleWithFixedDelay(
                    this::connect, instance.getRuntime().getReconnectInterval());
        }

        private void connect() {
            if (!running || connected.get() || !connecting.compareAndSet(false, true)) {
                return;
            }
            channelRegistry.connecting(channelKey);
            connectionFuture = client.execute(new ConnectionHandler(), headers, uri);
            connectionFuture.whenComplete((connectedSession, error) -> {
                connecting.set(false);
                if (error != null) {
                    if (!running) {
                        channelRegistry.disconnected(channelKey);
                        return;
                    }
                    channelRegistry.failed(channelKey, error);
                    log.warn("OneBot WebSocket connection failed, instanceId={}: {}",
                            instance.getId(), error.getMessage());
                    return;
                }
                if (!connected.get()) {
                    return;
                }
                session = connectedSession;
                log.info("OneBot WebSocket connected, instanceId={}, botAccountId={}",
                        instance.getId(), instance.getBotAccountId());
            });
        }

        private void stop() {
            if (reconnectTask != null) {
                reconnectTask.cancel(false);
            }
            CompletableFuture<WebSocketSession> pendingConnection = connectionFuture;
            if (pendingConnection != null && !pendingConnection.isDone()) {
                pendingConnection.cancel(true);
            }
            WebSocketSession connectedSession = session;
            session = null;
            if (connectedSession != null && connectedSession.isOpen()) {
                try {
                    connectedSession.close(CloseStatus.NORMAL);
                } catch (IOException exception) {
                    log.debug("OneBot WebSocket close failed, instanceId={}: {}",
                            instance.getId(), exception.getMessage());
                }
            }
            connected.set(false);
            connecting.set(false);
            channelRegistry.disconnected(channelKey);
        }

        private final class ConnectionHandler extends WebSocketHandlerDecorator {

            private ConnectionHandler() {
                super(handler);
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                Object advertisedAccount = session.getAttributes().get("x-self-id");
                if (advertisedAccount != null
                        && !instance.getBotAccountId().equals(advertisedAccount.toString())) {
                    rejectMismatchedAccount(session, advertisedAccount.toString());
                    return;
                }
                session.getAttributes().put("x-self-id", instance.getBotAccountId());
                super.afterConnectionEstablished(session);
                ManagedConnection.this.session = session;
                connected.set(true);
                channelRegistry.connected(channelKey);
            }

            @Override
            public void handleMessage(WebSocketSession session,
                                      WebSocketMessage<?> message) throws Exception {
                String actualAccount = messageAccountId(message);
                if (actualAccount != null
                        && !instance.getBotAccountId().equals(actualAccount)) {
                    rejectMismatchedAccount(session, actualAccount);
                    return;
                }
                super.handleMessage(session, message);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session,
                                              CloseStatus closeStatus) throws Exception {
                connected.set(false);
                if (ManagedConnection.this.session == session) {
                    ManagedConnection.this.session = null;
                }
                channelRegistry.disconnected(channelKey);
                super.afterConnectionClosed(session, closeStatus);
            }

            @Override
            public void handleTransportError(WebSocketSession session,
                                             Throwable exception) throws Exception {
                connected.set(false);
                channelRegistry.failed(channelKey, exception);
                super.handleTransportError(session, exception);
            }

            private String messageAccountId(WebSocketMessage<?> message) {
                if (!(message.getPayload() instanceof String payload)) {
                    return null;
                }
                try {
                    Object value = JsonObjectWrapper.parseObject(payload).get("self_id");
                    return value == null ? null : value.toString();
                } catch (RuntimeException ignored) {
                    return null;
                }
            }

            private void rejectMismatchedAccount(WebSocketSession session,
                                                 String actualAccount) throws IOException {
                IllegalStateException error = new IllegalStateException(
                        "Configured botAccountId=" + instance.getBotAccountId()
                                + " but OneBot connection reported " + actualAccount);
                channelRegistry.failed(channelKey, error);
                log.warn("OneBot account mismatch, instanceId={}: {}",
                        instance.getId(), error.getMessage());
                session.close(CloseStatus.POLICY_VIOLATION);
            }
        }
    }
}
