package com.nnm.bot.core.qqofficial.gateway;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import java.util.function.BooleanSupplier;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.plugin.execution.CommandDispatcher;
import com.nnm.bot.core.qqofficial.api.QqOfficialApiClient;
import com.nnm.bot.core.qqofficial.api.QqOfficialTokenClient;
import com.nnm.bot.core.qqofficial.config.QqOfficialConnection;
import com.nnm.bot.core.qqofficial.message.QqOfficialMessageContextFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.WebSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 管理所有 QQ 官方机器人实例的 Gateway 连接及其配套 API 客户端。
 *
 * <p>该类负责连接、鉴权、心跳、恢复和重连；只有消息类 DISPATCH 事件会被转换为
 * 核心消息上下文并交给命令执行器。</p>
 */
@Slf4j
@Component
public class QqOfficialGatewayClient implements SmartLifecycle {

    @Resource
    private HttpClient httpClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ObjectProvider<CommandDispatcher> commandDispatcherProvider;
    @Resource(name = "applicationTaskExecutor")
    private AsyncTaskExecutor taskExecutor;
    @Resource(name = "webSocketConnectExecutor")
    private AsyncTaskExecutor connectionExecutor;
    @Resource(name = "scheduledExecutor")
    private ThreadPoolTaskScheduler scheduler;
    @Resource
    private BotChannelRegistry channelRegistry;
    @Resource
    private ObjectProvider<ClusterBotRuntime> clusterProvider;
    private final ConcurrentHashMap<String, OfficialConnection> connections =
            new ConcurrentHashMap<>();
    private volatile boolean running;

    public QqOfficialApiClient apiClient(String botAccountId) {
        return this.connections.values().stream()
                .filter(connection -> connection.instance.botAccountId().equals(botAccountId))
                .findFirst()
                .map(connection -> connection.apiClient)
                .orElseThrow(() -> new BusinessException(
                        BotChannelErrorCode.CHANNEL_NOT_REGISTERED,
                        "未注册 QQ 官方机器人渠道: " + botAccountId));
    }

    @Override
    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.connections.values().forEach(OfficialConnection::start);
    }

    @Override
    public void stop() {
        stop(() -> { });
    }

    @Override
    public void stop(Runnable callback) {
        this.running = false;
        this.connections.values().forEach(OfficialConnection::stop);
        callback.run();
    }

    public synchronized void startInstance(BotChannelInstanceRecord record) {
        if (record.platform() != BotPlatform.QQ_OFFICIAL) {
            throw new BusinessException(
                    BotChannelErrorCode.PLATFORM_MISMATCH,
                    "QQ 官方客户端不能启动协议: " + record.platform());
        }
        this.validate(record);
        OfficialConnection replacement = new OfficialConnection(record);
        OfficialConnection previous = this.connections.put(record.instanceId(), replacement);
        if (ObjectUtil.isNotNull(previous)) {
            previous.stop();
        }
        this.running = true;
        replacement.start();
    }

    public synchronized void stopInstance(String instanceId) {
        OfficialConnection connection = this.connections.remove(instanceId);
        if (ObjectUtil.isNotNull(connection)) {
            connection.stop();
        }
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    private final class OfficialConnection implements WebSocket.Listener {

        private static final int DISPATCH = 0;
        private static final int RECONNECT = 7;
        private static final int INVALID_SESSION = 9;
        private static final int HELLO = 10;

        private final BotChannelInstanceRecord instance;
        private final QqOfficialConnection connection;
        private final QqOfficialTokenClient tokenClient;
        private final QqOfficialApiClient apiClient;
        private final java.net.http.HttpClient webSocketClient;
        private final BotChannelKey channelKey;
        private final BooleanSupplier ownership;
        private final AtomicBoolean connecting = new AtomicBoolean();
        private final AtomicBoolean active = new AtomicBoolean();
        private final StringBuilder incoming = new StringBuilder();

        private volatile WebSocket webSocket;
        private volatile ScheduledFuture<?> heartbeat;
        private volatile ScheduledFuture<?> reconnect;
        private volatile String sessionId;
        private volatile Integer sequence;

        private OfficialConnection(BotChannelInstanceRecord instance) {
            this.instance = instance;
            this.channelKey = new BotChannelKey(BotPlatform.QQ_OFFICIAL, instance.botAccountId());
            ClusterBotRuntime cluster = clusterProvider == null ? null : clusterProvider.getIfAvailable();
            BooleanSupplier lease = cluster == null ? () -> true : cluster.guard(channelKey);
            this.ownership = () -> active.get() && lease.getAsBoolean();
            this.connection = new QqOfficialConnection(
                    instance.appId(),
                    instance.clientSecret(),
                    instance.tokenUrl(),
                    instance.apiBaseUrl(),
                    instance.gatewayUrl());
            this.tokenClient = new QqOfficialTokenClient(httpClient, this.connection);
            this.apiClient = new QqOfficialApiClient(
                    httpClient, objectMapper, tokenClient, this.connection, ownership);
            // 协议调度交给 JDK 管理，不能占用业务并发许可后再向同一执行器提交业务。
            this.webSocketClient = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(instance.gatewayConnectTimeout())
                    .build();
        }

        private void start() {
            if (!active.compareAndSet(false, true)) {
                return;
            }
            connectionExecutor.execute(this::connect);
        }

        private void stop() {
            active.set(false);
            cancel(reconnect);
            cancel(heartbeat);
            WebSocket socket = webSocket;
            webSocket = null;
            if (ObjectUtil.isNotNull(socket)) {
                socket.abort();
            }
            channelRegistry.disconnected(channelKey);
        }

        @Override
        public void onOpen(WebSocket socket) {
            if (!running || !ownership.getAsBoolean()) {
                connecting.set(false);
                socket.abort();
                return;
            }
            webSocket = socket;
            connecting.set(false);
            socket.request(1);
            channelRegistry.connected(channelKey);
            log.info("QQ official Gateway connected, instanceId={}, botAccountId={}",
                    instance.instanceId(), instance.botAccountId());
        }

        @Override
        public CompletionStage<?> onText(WebSocket socket, CharSequence data, boolean last) {
            // JDK WebSocket 可能分片回调；只有收到最后一片后才能解析完整 JSON。
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
            if (active.get()) {
                channelRegistry.disconnected(channelKey);
            }
            if (running && active.get()) {
                scheduleReconnect();
            }
            log.warn("QQ official Gateway closed, instanceId={}: {} {}",
                    instance.instanceId(), statusCode, reason);
            return null;
        }

        @Override
        public void onError(WebSocket socket, Throwable error) {
            if (!active.get()) {
                return;
            }
            channelRegistry.failed(channelKey, error);
            log.warn("QQ official Gateway error, instanceId={}: {}",
                    instance.instanceId(), error);
            if (running && active.get()) {
                socket.abort();
                scheduleReconnect();
            }
        }

        private void connect() {
            if (!running || !ownership.getAsBoolean() || !connecting.compareAndSet(false, true)) {
                return;
            }
            channelRegistry.connecting(channelKey);
            try {
                tokenClient.getAccessToken();
                webSocketClient.newWebSocketBuilder()
                        .buildAsync(URI.create(apiClient.gatewayUrl()), this)
                        .whenComplete((socket, error) -> {
                            if (ObjectUtil.isNotNull(error)) {
                                connecting.set(false);
                                if (!active.get()) {
                                    return;
                                }
                                channelRegistry.failed(channelKey, error);
                                log.warn("QQ official Gateway connection failed, instanceId={}",
                                        instance.instanceId(), error);
                                scheduleReconnect();
                            }
                        });
            } catch (Exception exception) {
                connecting.set(false);
                if (!active.get()) {
                    return;
                }
                channelRegistry.failed(channelKey, exception);
                log.warn("QQ official Gateway setup failed, instanceId={}",
                        instance.instanceId(), exception);
                scheduleReconnect();
            }
        }

        private void handlePayload(WebSocket socket, String payload) {
            if (!ownership.getAsBoolean()) {
                socket.abort();
                return;
            }
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
                channelRegistry.failed(channelKey, exception);
                log.warn("QQ official Gateway payload handling failed, instanceId={}",
                        instance.instanceId(), exception);
            }
        }

        private void handleHello(WebSocket socket, JsonNode data) {
            long interval = Math.max(1000L,
                    data.path("heartbeat_interval").asLong(45000L));
            String token = tokenClient.getAccessToken();
            JsonNode payload = StrUtil.isNotBlank(sessionId) && ObjectUtil.isNotNull(sequence)
                    ? QqOfficialGatewayPayloads.resume(
                            objectMapper, token, sessionId, sequence)
                    : QqOfficialGatewayPayloads.identify(
                            objectMapper, token, instance.intents());
            if (!ownership.getAsBoolean()) {
                socket.abort();
                return;
            }
            socket.sendText(payload.toString(), true);
            cancel(heartbeat);
            heartbeat = scheduler.scheduleAtFixedRate(
                    () -> sendHeartbeat(socket), Duration.ofMillis(interval));
        }

        private void sendHeartbeat(WebSocket socket) {
            if (!ownership.getAsBoolean()) {
                socket.abort();
                return;
            }
            if (!socket.isOutputClosed() && !socket.isInputClosed()) {
                socket.sendText(QqOfficialGatewayPayloads
                        .heartbeat(objectMapper, sequence).toString(), true);
            }
        }

        /**
         * 只把 QQ 消息事件送入命令执行器；连接生命周期事件在 Gateway 层内部消费。
         * 命令在业务虚拟线程运行；并发达到上限时提交入口背压，不创建无界消息积压。
         */
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
                            () -> {
                                if (ownership.getAsBoolean()) {
                                    commandDispatcherProvider.getObject().dispatch(context);
                                }
                            }));
        }

        private void scheduleReconnect() {
            if (!running || !active.get()) {
                return;
            }
            cancel(reconnect);
            reconnect = scheduler.schedule(() -> connectionExecutor.execute(this::connect),
                    Instant.now().plus(instance.reconnectDelay()));
        }
    }

    private static void cancel(ScheduledFuture<?> future) {
        if (ObjectUtil.isNotNull(future)) {
            future.cancel(false);
        }
    }

    private void validate(BotChannelInstanceRecord record) {
        if (ObjectUtil.isNull(record)) {
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_CONFIG_INVALID,
                    "QQ 官方机器人实例记录不能为空");
        }
        this.requireText(record.instanceId(), "QQ 官方机器人实例标识");
        this.requireText(record.botAccountId(), "QQ 官方机器人 App ID");
        this.requireText(record.appId(), "QQ 官方机器人 App ID");
        this.requireText(record.clientSecret(), "QQ 官方机器人 Client Secret");
        this.requireText(record.tokenUrl(), "QQ 官方机器人 Token 地址");
        this.requireText(record.apiBaseUrl(), "QQ 官方机器人 API 地址");
        if (record.intents() < 0) {
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_CONFIG_INVALID,
                    "QQ 官方机器人 intents 不能为负数");
        }
        if (ObjectUtil.isNull(record.gatewayConnectTimeout())
                || record.gatewayConnectTimeout().isZero()
                || record.gatewayConnectTimeout().isNegative()) {
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_CONFIG_INVALID,
                    "QQ 官方机器人 Gateway 连接超时必须为正数");
        }
        if (ObjectUtil.isNull(record.reconnectDelay()) || record.reconnectDelay().isNegative()) {
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_CONFIG_INVALID,
                    "QQ 官方机器人重连延迟不能为负数");
        }
    }

    private void requireText(String value, String field) {
        if (StrUtil.isBlank(value)) {
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_CONFIG_INVALID,
                    field + "不能为空");
        }
    }
}
