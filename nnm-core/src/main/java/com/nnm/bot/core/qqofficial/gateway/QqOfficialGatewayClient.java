package com.nnm.bot.core.qqofficial.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.core.channel.BotChannelKey;
import com.nnm.bot.core.channel.BotChannelRegistry;
import com.nnm.bot.core.message.BotPlatform;
import com.nnm.bot.core.plugin.PluginDispatcher;
import com.nnm.bot.core.qqofficial.api.QqOfficialApiClient;
import com.nnm.bot.core.qqofficial.api.QqOfficialTokenClient;
import com.nnm.bot.core.qqofficial.config.QqOfficialProperties;
import com.nnm.bot.core.qqofficial.message.QqOfficialMessageContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.WebSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/** 管理所有 QQ 官方机器人实例的 Gateway 与 API 客户端。 */
@Slf4j
@Component
public class QqOfficialGatewayClient implements SmartLifecycle {

    private final QqOfficialProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ObjectProvider<PluginDispatcher> pluginDispatcherProvider;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final ThreadPoolTaskScheduler scheduler;
    private final BotChannelRegistry channelRegistry;
    private final Map<String, OfficialConnection> connections;
    private volatile boolean running;

    public QqOfficialGatewayClient(
            QqOfficialProperties properties,
            HttpClient httpClient,
            ObjectMapper objectMapper,
            ObjectProvider<PluginDispatcher> pluginDispatcherProvider,
            @Qualifier("applicationTaskExecutor") ThreadPoolTaskExecutor taskExecutor,
            @Qualifier("scheduledExecutor") ThreadPoolTaskScheduler scheduler,
            BotChannelRegistry channelRegistry) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.pluginDispatcherProvider = pluginDispatcherProvider;
        this.taskExecutor = taskExecutor;
        this.scheduler = scheduler;
        this.channelRegistry = channelRegistry;
        this.connections = createConnections();
    }

    public QqOfficialApiClient apiClient(String botAccountId) {
        OfficialConnection connection = connections.get(botAccountId);
        if (connection == null) {
            throw new IllegalStateException(
                    "No QQ official channel registered for botAccountId=" + botAccountId);
        }
        return connection.apiClient;
    }

    @Override
    public void start() {
        if (!properties.isEnabled() || running) {
            return;
        }
        running = true;
        connections.values().forEach(OfficialConnection::start);
    }

    @Override
    public void stop() {
        stop(() -> { });
    }

    @Override
    public void stop(Runnable callback) {
        running = false;
        connections.values().forEach(OfficialConnection::stop);
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
        return Integer.MAX_VALUE;
    }

    private Map<String, OfficialConnection> createConnections() {
        if (!properties.isEnabled()) {
            return Map.of();
        }
        Map<String, OfficialConnection> result = new LinkedHashMap<>();
        for (QqOfficialProperties.Instance instance : properties.getInstances()) {
            if (!instance.isEnabled()) {
                continue;
            }
            OfficialConnection connection = new OfficialConnection(instance);
            OfficialConnection previous = result.put(instance.botAccountId(), connection);
            if (previous != null) {
                throw new IllegalStateException(
                        "Duplicate QQ official botAccountId: " + instance.botAccountId());
            }
        }
        return Map.copyOf(result);
    }

    private final class OfficialConnection implements WebSocket.Listener {

        private static final int DISPATCH = 0;
        private static final int RECONNECT = 7;
        private static final int INVALID_SESSION = 9;
        private static final int HELLO = 10;

        private final QqOfficialProperties.Instance instance;
        private final QqOfficialTokenClient tokenClient;
        private final QqOfficialApiClient apiClient;
        private final java.net.http.HttpClient webSocketClient;
        private final BotChannelKey channelKey;
        private final AtomicBoolean connecting = new AtomicBoolean();
        private final StringBuilder incoming = new StringBuilder();

        private volatile WebSocket webSocket;
        private volatile ScheduledFuture<?> heartbeat;
        private volatile ScheduledFuture<?> reconnect;
        private volatile String sessionId;
        private volatile Integer sequence;

        private OfficialConnection(QqOfficialProperties.Instance instance) {
            this.instance = instance;
            this.tokenClient = new QqOfficialTokenClient(httpClient, instance.getConnection());
            this.apiClient = new QqOfficialApiClient(
                    httpClient, objectMapper, tokenClient, instance.getConnection());
            this.webSocketClient = java.net.http.HttpClient.newBuilder()
                    .executor(taskExecutor)
                    .connectTimeout(instance.getRuntime().getGatewayConnectTimeout())
                    .build();
            this.channelKey = new BotChannelKey(
                    BotPlatform.QQ_OFFICIAL, instance.botAccountId());
        }

        private void start() {
            taskExecutor.execute(this::connect);
        }

        private void stop() {
            cancel(reconnect);
            cancel(heartbeat);
            WebSocket socket = webSocket;
            webSocket = null;
            if (socket != null) {
                socket.sendClose(WebSocket.NORMAL_CLOSURE, "shutdown");
            }
            channelRegistry.disconnected(channelKey);
        }

        @Override
        public void onOpen(WebSocket socket) {
            if (!running) {
                connecting.set(false);
                socket.sendClose(WebSocket.NORMAL_CLOSURE, "disabled");
                return;
            }
            webSocket = socket;
            connecting.set(false);
            socket.request(1);
            channelRegistry.connected(channelKey);
            log.info("QQ official Gateway connected, instanceId={}, botAccountId={}",
                    instance.getId(), instance.botAccountId());
        }

        @Override
        public CompletionStage<?> onText(WebSocket socket, CharSequence data, boolean last) {
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
            channelRegistry.disconnected(channelKey);
            if (running) {
                scheduleReconnect();
            }
            log.warn("QQ official Gateway closed, instanceId={}: {} {}",
                    instance.getId(), statusCode, reason);
            return null;
        }

        @Override
        public void onError(WebSocket socket, Throwable error) {
            channelRegistry.failed(channelKey, error);
            log.warn("QQ official Gateway error, instanceId={}: {}",
                    instance.getId(), error.getMessage());
            if (running) {
                socket.abort();
                scheduleReconnect();
            }
        }

        private void connect() {
            if (!running || !connecting.compareAndSet(false, true)) {
                return;
            }
            channelRegistry.connecting(channelKey);
            try {
                tokenClient.getAccessToken();
                webSocketClient.newWebSocketBuilder()
                        .buildAsync(URI.create(apiClient.gatewayUrl()), this)
                        .whenComplete((socket, error) -> {
                            if (error != null) {
                                connecting.set(false);
                                channelRegistry.failed(channelKey, error);
                                log.warn("QQ official Gateway connection failed, instanceId={}: {}",
                                        instance.getId(), error.getMessage());
                                scheduleReconnect();
                            }
                        });
            } catch (Exception exception) {
                connecting.set(false);
                channelRegistry.failed(channelKey, exception);
                log.warn("QQ official Gateway setup failed, instanceId={}: {}",
                        instance.getId(), exception.getMessage());
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
                    default -> { }
                }
            } catch (Exception exception) {
                log.warn("QQ official Gateway payload handling failed, instanceId={}: {}",
                        instance.getId(), exception.getMessage());
            }
        }

        private void handleHello(WebSocket socket, JsonNode data) {
            long interval = Math.max(1000L,
                    data.path("heartbeat_interval").asLong(45000L));
            String token = tokenClient.getAccessToken();
            JsonNode payload = sessionId != null && !sessionId.isBlank() && sequence != null
                    ? QqOfficialGatewayPayloads.resume(
                            objectMapper, token, sessionId, sequence)
                    : QqOfficialGatewayPayloads.identify(
                            objectMapper, token, instance.getRuntime().getIntents());
            socket.sendText(payload.toString(), true);
            cancel(heartbeat);
            heartbeat = scheduler.scheduleAtFixedRate(
                    () -> sendHeartbeat(socket), Duration.ofMillis(interval));
        }

        private void sendHeartbeat(WebSocket socket) {
            if (!socket.isOutputClosed() && !socket.isInputClosed()) {
                socket.sendText(QqOfficialGatewayPayloads
                        .heartbeat(objectMapper, sequence).toString(), true);
            }
        }

        private void handleDispatch(String eventType, JsonNode data) {
            if ("READY".equals(eventType)) {
                sessionId = data.path("session_id").asText(null);
                return;
            }
            if ("RESUMED".equals(eventType)) {
                return;
            }
            QqOfficialMessageContextFactory
                    .from(instance.botAccountId(), eventType, data)
                    .ifPresent(context -> taskExecutor.execute(
                            () -> pluginDispatcherProvider.getObject().dispatch(context)));
        }

        private void scheduleReconnect() {
            if (!running) {
                return;
            }
            cancel(reconnect);
            reconnect = scheduler.schedule(this::connect,
                    Instant.now().plus(instance.getRuntime().getReconnectDelay()));
        }
    }

    private static void cancel(ScheduledFuture<?> future) {
        if (future != null) {
            future.cancel(false);
        }
    }
}
