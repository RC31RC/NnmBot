package com.nnm.bot.core.qqofficial.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.plugin.PluginDispatcher;
import com.nnm.bot.core.qqofficial.api.QqOfficialApiClient;
import com.nnm.bot.core.qqofficial.api.QqOfficialTokenClient;
import com.nnm.bot.core.qqofficial.config.QqOfficialProperties;
import com.nnm.bot.core.qqofficial.message.QqOfficialMessageContextFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class QqOfficialGatewayClient implements WebSocket.Listener, SmartLifecycle {

    private static final int DISPATCH = 0;
    private static final int RECONNECT = 7;
    private static final int INVALID_SESSION = 9;
    private static final int HELLO = 10;

    @Resource
    private QqOfficialProperties properties;
    @Resource
    private QqOfficialApiClient apiClient;
    @Resource
    private QqOfficialTokenClient tokenClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private PluginDispatcher pluginDispatcher;
    @Resource(name = "applicationTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Resource(name = "scheduledExecutor")
    private ThreadPoolTaskScheduler scheduler;
    private final AtomicBoolean running = new AtomicBoolean();
    private final AtomicBoolean connecting = new AtomicBoolean();
    private final StringBuilder incoming = new StringBuilder();
    private HttpClient webSocketClient;

    private volatile WebSocket webSocket;
    private volatile ScheduledFuture<?> heartbeat;
    private volatile ScheduledFuture<?> reconnect;
    private volatile String sessionId;
    private volatile Integer sequence;

    @PostConstruct
    void initialize() {
        this.webSocketClient = HttpClient.newBuilder()
                .executor(taskExecutor)
                .connectTimeout(properties.getGatewayConnectTimeout())
                .build();
    }

    @Override
    public void start() {
        if (!properties.isEnabled() || !running.compareAndSet(false, true)) {
            return;
        }
        taskExecutor.execute(this::connect);
    }

    @Override
    public void stop() {
        stop(() -> {
        });
    }

    @Override
    public void stop(Runnable callback) {
        running.set(false);
        cancel(reconnect);
        cancel(heartbeat);
        WebSocket socket = webSocket;
        webSocket = null;
        if (socket != null) {
            socket.sendClose(WebSocket.NORMAL_CLOSURE, "shutdown");
        }
        callback.run();
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public boolean isAutoStartup() {
        return properties.isEnabled();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onOpen(WebSocket socket) {
        if (!running.get()) {
            connecting.set(false);
            socket.sendClose(WebSocket.NORMAL_CLOSURE, "disabled");
            return;
        }
        webSocket = socket;
        connecting.set(false);
        socket.request(1);
        log.info("QQ official Gateway connected");
    }

    @Override
    public java.util.concurrent.CompletionStage<?> onText(WebSocket socket,
                                                           CharSequence data,
                                                           boolean last) {
        synchronized (incoming) {
            incoming.append(data);
            if (last) {
                String payload = incoming.toString();
                incoming.setLength(0);
                handlePayload(socket, payload);
            }
        }
        socket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket socket, int statusCode, String reason) {
        connecting.set(false);
        cancel(heartbeat);
        if (webSocket == socket) {
            webSocket = null;
        }
        if (running.get()) {
            scheduleReconnect();
        }
        log.warn("QQ official Gateway closed: {} {}", statusCode, reason);
        return null;
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        log.warn("QQ official Gateway error: {}", error.getMessage());
        if (running.get()) {
            socket.abort();
            scheduleReconnect();
        }
    }

    private void connect() {
        if (!running.get() || !connecting.compareAndSet(false, true)) {
            return;
        }
        try {
            tokenClient.getAccessToken();
            String url = apiClient.gatewayUrl();
            webSocketClient.newWebSocketBuilder()
                    .buildAsync(URI.create(url), this)
                    .whenComplete((socket, error) -> {
                        if (error != null) {
                            connecting.set(false);
                            log.warn("QQ official Gateway connection failed: {}", error.getMessage());
                            scheduleReconnect();
                        }
                    });
            log.debug("QQ official Gateway connection requested");
        } catch (Exception e) {
            connecting.set(false);
            log.warn("QQ official Gateway setup failed: {}", e.getMessage());
            scheduleReconnect();
        }
    }

    private void handlePayload(WebSocket socket, String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root.hasNonNull("s")) {
                sequence = root.get("s").asInt();
            }
            int op = root.path("op").asInt(-1);
            JsonNode data = root.path("d");
            switch (op) {
                case HELLO -> handleHello(socket, data);
                case DISPATCH -> handleDispatch(root.path("t").asText(""), data);
                case RECONNECT -> {
                    socket.abort();
                    scheduleReconnect();
                }
                case INVALID_SESSION -> {
                    if (!data.asBoolean(false)) {
                        sessionId = null;
                        sequence = null;
                        tokenClient.clear();
                    }
                    socket.abort();
                    scheduleReconnect();
                }
                default -> {
                }
            }
        } catch (Exception e) {
            log.warn("QQ official Gateway payload handling failed: {}", e.getMessage());
        }
    }

    private void handleHello(WebSocket socket, JsonNode data) {
        long interval = Math.max(1000L, data.path("heartbeat_interval").asLong(45000L));
        String token = tokenClient.getAccessToken();
        JsonNode payload = sessionId != null && !sessionId.isBlank() && sequence != null
                ? QqOfficialGatewayPayloads.resume(objectMapper, token, sessionId, sequence)
                : QqOfficialGatewayPayloads.identify(
                        objectMapper, token, properties.getIntents());
        socket.sendText(payload.toString(), true);
        cancel(heartbeat);
        heartbeat = scheduler.scheduleAtFixedRate(
                () -> sendHeartbeat(socket), Duration.ofMillis(interval));
    }

    private void sendHeartbeat(WebSocket socket) {
        if (socket.isOutputClosed() || socket.isInputClosed()) {
            return;
        }
        socket.sendText(
                QqOfficialGatewayPayloads.heartbeat(objectMapper, sequence).toString(), true);
    }

    private void handleDispatch(String eventType, JsonNode data) {
        if ("READY".equals(eventType)) {
            sessionId = data.path("session_id").asText(null);
            return;
        }
        if ("RESUMED".equals(eventType)) {
            return;
        }
        QqOfficialMessageContextFactory.from(properties.getAppId(), eventType, data)
                .ifPresent(context ->
                        taskExecutor.execute(() -> pluginDispatcher.dispatch(context)));
    }

    private void scheduleReconnect() {
        if (!running.get()) {
            return;
        }
        cancel(reconnect);
        reconnect = scheduler.schedule(this::connect,
                Instant.now().plus(normalizedReconnectDelay()));
    }

    private Duration normalizedReconnectDelay() {
        return properties.getReconnectDelay() == null
                ? Duration.ofSeconds(5)
                : properties.getReconnectDelay();
    }

    private static void cancel(ScheduledFuture<?> future) {
        if (future != null) {
            future.cancel(false);
        }
    }
}
