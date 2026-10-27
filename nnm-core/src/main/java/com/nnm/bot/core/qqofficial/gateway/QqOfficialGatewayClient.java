package com.nnm.bot.core.qqofficial.gateway;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandIngressResult;
import java.util.Optional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.channel.port.BotChannelConnector;
import com.nnm.bot.core.channel.port.BotGroupMemberEventSink;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.plugin.execution.CommandIngressService;
import com.nnm.bot.core.qqofficial.api.QqOfficialApiClient;
import com.nnm.bot.core.qqofficial.api.QqOfficialTokenClient;
import com.nnm.bot.core.qqofficial.config.QqOfficialConnection;
import com.nnm.bot.core.qqofficial.discovery.QqOfficialObservedScopeStore;
import com.nnm.bot.core.qqofficial.message.QqOfficialMessageContextFactory;
import jakarta.annotation.Resource;
import jakarta.annotation.PreDestroy;
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
import java.util.function.BooleanSupplier;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 管理所有 QQ 官方机器人实例的 Gateway 连接及其配套 API 客户端。
 *
 * <p>该类负责连接、鉴权、心跳、恢复和重连；只有消息类 DISPATCH 事件会被转换为
 * 核心消息上下文并交给命令执行器。</p>
 */
@Slf4j
@Component
public class QqOfficialGatewayClient implements SmartLifecycle, BotChannelConnector {
    private final ReentrantLock lifecycleLock = new ReentrantLock();

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Resource
    private HttpClient httpClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ObjectProvider<CommandIngressService> commandIngressProvider;
    @Resource(name = "qqGatewayEventExecutor")
    private AsyncTaskExecutor eventExecutor;
    @Resource(name = "webSocketConnectExecutor")
    private AsyncTaskExecutor connectionExecutor;
    @Resource(name = "scheduledExecutor")
    private ThreadPoolTaskScheduler scheduler;
    @Resource
    private BotChannelRegistry channelRegistry;
    @Resource
    private ObjectProvider<ClusterBotRuntime> clusterProvider;
    @Resource
    private ObjectProvider<QqOfficialObservedScopeStore> observedScopeStoreProvider;
    @Resource
    private ObjectProvider<BotGroupMemberEventSink> memberEventSinkProvider;
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

    @PreDestroy
    void destroy() {
        stop();
        connections.values().forEach(connection -> connection.webSocketClient.shutdownNow());
        connections.clear();
    }

    public void startInstance(BotChannelInstanceRecord record) {
        lifecycleLock.lock();
        try {
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
                previous.webSocketClient.shutdownNow();
            }
            this.running = true;
            replacement.start();
        } finally {
            lifecycleLock.unlock();
        }
    }

    public void stopInstance(String instanceId) {
        lifecycleLock.lock();
        try {
            OfficialConnection connection = this.connections.get(instanceId);
            if (ObjectUtil.isNotNull(connection)) {
                connection.stop();
                connection.webSocketClient.shutdownNow();
                this.connections.remove(instanceId, connection);
            }
        } finally {
            lifecycleLock.unlock();
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
        private static final int HEARTBEAT_ACK = 11;

        // 连接状态和分片缓冲分别互斥；同时加锁时始终先连接锁、再分片锁。
        private final ReentrantLock connectionLock = new ReentrantLock();
        private final ReentrantLock incomingLock = new ReentrantLock();

        private final BotChannelInstanceRecord instance;
        private final QqOfficialConnection connection;
        private final QqOfficialTokenClient tokenClient;
        private final QqOfficialApiClient apiClient;
        private final java.net.http.HttpClient webSocketClient;
        private final BotChannelKey channelKey;
        private final BooleanSupplier ownership;
        private final AtomicBoolean connecting = new AtomicBoolean();
        private final AtomicBoolean active = new AtomicBoolean();
        private final AtomicBoolean awaitingHeartbeatAck = new AtomicBoolean();
        private final StringBuilder incoming = new StringBuilder();

        private volatile WebSocket webSocket;
        private volatile ScheduledFuture<?> heartbeat;
        private volatile ScheduledFuture<?> reconnect;
        private volatile ScheduledFuture<?> readyTimeout;
        private volatile String sessionId;
        private volatile Integer sequence;
        private boolean ready;

        private OfficialConnection(BotChannelInstanceRecord instance) {
            this.instance = instance;
            this.channelKey = new BotChannelKey(BotPlatform.QQ_OFFICIAL, instance.botAccountId());
            ClusterBotRuntime cluster = clusterProvider.getObject();
            BooleanSupplier lease = cluster.guard(channelKey);
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
            connectionLock.lock();
            try {
                active.set(false);
                cancel(reconnect);
                cancel(heartbeat);
                cancel(readyTimeout);
                ready = false;
                WebSocket socket = webSocket;
                webSocket = null;
                if (ObjectUtil.isNotNull(socket)) {
                    socket.abort();
                }
                incomingLock.lock();
                try {
                    incoming.setLength(0);
                } finally {
                    incomingLock.unlock();
                }
                channelRegistry.disconnected(channelKey);
            } finally {
                connectionLock.unlock();
            }
        }

        @Override
        public void onOpen(WebSocket socket) {
            connectionLock.lock();
            try {
                if (!running || !ownership.getAsBoolean() || webSocket != null) {
                    connecting.set(false);
                    socket.abort();
                    return;
                }
                webSocket = socket;
                ready = false;
                connecting.set(false);
                socket.request(1);
                readyTimeout = scheduler.schedule(() -> checkReadyTimeout(socket),
                        Instant.now().plus(instance.gatewayConnectTimeout()));
            } finally {
                connectionLock.unlock();
            }
        }

        private void checkReadyTimeout(WebSocket socket) {
            connectionLock.lock();
            try {
                if (ready || !accepts(socket)) return;
                failAndReconnect(socket, new IllegalStateException("Gateway READY timeout"));
            } finally {
                connectionLock.unlock();
            }
        }

        @Override
        public CompletionStage<?> onText(WebSocket socket, CharSequence data, boolean last) {
            // 不在分片锁内查询租约或停止连接，保持连接锁 -> 分片锁的顺序。
            if (!accepts(socket)) return null;
            // JDK WebSocket 可能分片回调；只有收到最后一片后才能解析完整 JSON。
            String payload = null;
            incomingLock.lock();
            try {
                if (socket != webSocket) {
                    socket.abort();
                    return null;
                }
                incoming.append(data);
                if (last) {
                    payload = incoming.toString();
                    incoming.setLength(0);
                }
            } finally {
                incomingLock.unlock();
            }
            // 不在分片锁内处理协议；重连和停止按连接锁 -> 分片锁的单一顺序清理。
            if (payload != null) {
                handlePayload(socket, payload);
            }
            if (socket == webSocket) socket.request(1);
            return null;
        }

        private boolean accepts(WebSocket socket) {
            if (socket != webSocket) {
                log.debug("Ignoring stale QQ official Gateway socket, instanceId={}", instance.instanceId());
                socket.abort();
                return false;
            }
            if (ownership.getAsBoolean()) return true;
            connectionLock.lock();
            try {
                if (socket == webSocket) {
                    // abort() 不保证触发 onClose，必须在此清理心跳和本地连接状态。
                    log.warn("QQ official Gateway ownership lost; closing socket, instanceId={}. "
                                    + "A JVM pause or expired node/connection lease fences this connection; "
                                    + "cluster reconciliation must acquire a new connection.", instance.instanceId());
                    stop();
                } else {
                    socket.abort();
                }
            } finally {
                connectionLock.unlock();
            }
            return false;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket socket, int statusCode, String reason) {
            connectionLock.lock();
            try {
                if (socket != webSocket) return null;
                connecting.set(false);
                cancel(heartbeat);
                cancel(readyTimeout);
                ready = false;
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
            } finally {
                connectionLock.unlock();
            }
        }

        @Override
        public void onError(WebSocket socket, Throwable error) {
            connectionLock.lock();
            try {
                if (!active.get() || socket != webSocket) {
                    return;
                }
                channelRegistry.failed(channelKey, error);
                log.warn("QQ official Gateway error, instanceId={}: {}",
                        instance.instanceId(), error);
                if (running && active.get()) {
                    socket.abort();
                    scheduleReconnect(socket);
                }
            } finally {
                connectionLock.unlock();
            }
        }

        private void connect() {
            if (!running || !ownership.getAsBoolean() || webSocket != null
                    || !connecting.compareAndSet(false, true)) {
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
            if (!accepts(socket)) return;
            boolean protocolHandled = false;
            try {
                JsonNode root = objectMapper.readTree(payload);
                int op = root.path("op").asInt(-1);
                JsonNode data = root.path("d");
                String eventType = root.path("t").asText("");
                connectionLock.lock();
                try {
                    if (!accepts(socket)) return;
                    if (root.hasNonNull("s")) {
                        sequence = root.get("s").asInt();
                    }
                    switch (op) {
                        case HELLO -> handleHello(socket, data);
                        case DISPATCH -> {
                            if ("READY".equals(eventType)) {
                                sessionId = data.path("session_id").asText(null);
                            }
                            if ("READY".equals(eventType) || "RESUMED".equals(eventType)) {
                                ready = true;
                                cancel(readyTimeout);
                                channelRegistry.connected(channelKey);
                            }
                        }
                        case HEARTBEAT_ACK -> awaitingHeartbeatAck.set(false);
                        case RECONNECT -> scheduleReconnect();
                        case INVALID_SESSION -> {
                            if (!data.asBoolean(false)) {
                                sessionId = null;
                                sequence = null;
                                tokenClient.clear();
                            }
                            scheduleReconnect();
                        }
                        default -> { }
                    }
                } finally {
                    connectionLock.unlock();
                }
                protocolHandled = true;
                if (op == DISPATCH && !"READY".equals(eventType) && !"RESUMED".equals(eventType)) {
                    handleDispatch(socket, eventType, data);
                }
            } catch (Exception exception) {
                connectionLock.lock();
                try {
                    if (!accepts(socket)) return;
                    // 业务/辅助事件拒绝不代表连接协议失败，不能污染心跳与在线状态。
                    if (!protocolHandled) channelRegistry.failed(channelKey, exception);
                } finally {
                    connectionLock.unlock();
                }
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
            if (!accepts(socket)) return;
            cancel(heartbeat);
            awaitingHeartbeatAck.set(false);
            // JDK WebSocket does not allow concurrent outstanding text sends.
            socket.sendText(payload.toString(), true).whenComplete((ignored, failure) -> {
                connectionLock.lock();
                try {
                    if (!accepts(socket)) return;
                    if (failure != null) {
                        failAndReconnect(socket, failure);
                        return;
                    }
                    heartbeat = scheduler.scheduleAtFixedRate(
                            () -> sendHeartbeat(socket), Duration.ofMillis(interval));
                } finally {
                    connectionLock.unlock();
                }
            });
        }

        private void sendHeartbeat(WebSocket socket) {
            connectionLock.lock();
            try {
                if (!accepts(socket)) return;
                if (!awaitingHeartbeatAck.compareAndSet(false, true)) {
                    channelRegistry.failed(channelKey, new IllegalStateException("Gateway heartbeat ACK timeout"));
                    scheduleReconnect();
                    return;
                }
                if (!socket.isOutputClosed() && !socket.isInputClosed()) {
                    socket.sendText(QqOfficialGatewayPayloads
                            .heartbeat(objectMapper, sequence).toString(), true)
                            .whenComplete((ignored, failure) -> {
                                if (failure != null) failAndReconnect(socket, failure);
                            });
                }
            } finally {
                connectionLock.unlock();
            }
        }

        private void failAndReconnect(WebSocket socket, Throwable failure) {
            connectionLock.lock();
            try {
                if (!accepts(socket)) return;
                channelRegistry.failed(channelKey, failure);
                scheduleReconnect(socket);
            } finally {
                connectionLock.unlock();
            }
        }

        /**
         * 同步内存匹配后非等待接纳；辅助事件独立有界，不能让业务容量阻塞协议回调。
         */
        private void handleDispatch(WebSocket socket, String eventType, JsonNode data) {
            String messageId = data.path("id").asText("");
            long receivedAt = System.currentTimeMillis();
            if (eventType.contains("MESSAGE_CREATE")) {
                log.info("QQ official message received instance={} type={} messageId={}",
                        instance.instanceId(), eventType, messageId);
            } else {
                log.debug("QQ official event received instance={} type={} messageId={}",
                        instance.instanceId(), eventType, messageId);
            }
            Optional<BotMessageContext> context = QqOfficialMessageContextFactory.from(instance.botAccountId(), eventType, data);
            context.ifPresent(value -> {
                CommandIngressResult result = commandIngressProvider.getObject().accept(value.withReceivedAt(receivedAt),
                        () -> socket == webSocket && ownership.getAsBoolean());
                log.debug("QQ official command admission instance={} type={} messageId={} result={}",
                        instance.instanceId(), eventType, messageId, result);
            });
            // 先完成命令接纳，辅助事件拒绝/失败不能改变已接管结果；不在协议线程做外部 I/O。
            if (isObservedEvent(eventType)) {
                ObjectNode observation = objectMapper.createObjectNode();
                for (String field : new String[] {"group_openid", "member_openid", "member_name", "nickname", "member_role", "role"}) {
                    String value = data.path(field).asText("");
                    if (value.length() > 4096) throw new IllegalArgumentException("QQ observation field is too large: " + field);
                    observation.put(field, value);
                }
                eventExecutor.execute(() -> {
                    if (socket != webSocket || !ownership.getAsBoolean()) return;
                    observeDispatch(eventType, observation);
                    dispatchMemberEvent(eventType, observation);
                });
            }
        }

        private boolean isObservedEvent(String eventType) {
            return switch (eventType) {
                case "GROUP_ADD_ROBOT", "GROUP_DEL_ROBOT", "GROUP_MESSAGE_CREATE",
                        "GROUP_AT_MESSAGE_CREATE", "GROUP_MEMBER_ADD", "GROUP_MEMBER_REMOVE" -> true;
                default -> false;
            };
        }

        private void observeDispatch(String eventType, JsonNode data) {
            if (observedScopeStoreProvider == null) {
                return;
            }
            QqOfficialObservedScopeStore store =
                    observedScopeStoreProvider.getIfAvailable();
            if (store == null) {
                return;
            }
            String groupOpenid = text(data, "group_openid");
            switch (eventType) {
                case "GROUP_ADD_ROBOT", "GROUP_MESSAGE_CREATE", "GROUP_AT_MESSAGE_CREATE" ->
                        store.observeGroup(instance.botAccountId(), groupOpenid);
                case "GROUP_DEL_ROBOT" ->
                        store.deactivateGroup(instance.botAccountId(), groupOpenid);
                case "GROUP_MEMBER_ADD" ->
                        store.observeMember(
                                instance.botAccountId(),
                                groupOpenid,
                                text(data, "member_openid"),
                                text(data, "member_name", "nickname"),
                                text(data, "member_role", "role"));
                case "GROUP_MEMBER_REMOVE" ->
                        store.removeMember(
                                instance.botAccountId(),
                                groupOpenid,
                                text(data, "member_openid"));
                default -> {
                }
            }
        }

        private String text(JsonNode node, String... fields) {
            for (String field : fields) {
                String value = node.path(field).asText("");
                if (StrUtil.isNotBlank(value)) {
                    return value;
                }
            }
            return null;
        }

        private void dispatchMemberEvent(String eventType, JsonNode data) {
            if (memberEventSinkProvider == null
                    || !("GROUP_MEMBER_ADD".equals(eventType)
                    || "GROUP_MEMBER_REMOVE".equals(eventType))) {
                return;
            }
            String groupOpenid = text(data, "group_openid");
            String memberOpenid = text(data, "member_openid");
            if (groupOpenid == null || memberOpenid == null) {
                return;
            }
            BotGroupMemberEventSink sink = memberEventSinkProvider.getIfAvailable();
            if (sink == null) return;
            if ("GROUP_MEMBER_ADD".equals(eventType)) {
                sink.handleMemberJoined(BotPlatform.QQ_OFFICIAL, instance.botAccountId(),
                        groupOpenid, new BotDiscoveredMember(memberOpenid, null, null));
            } else {
                sink.handleMemberLeft(BotPlatform.QQ_OFFICIAL, instance.botAccountId(),
                        groupOpenid, memberOpenid);
            }
        }

        private void scheduleReconnect(WebSocket expected) {
            connectionLock.lock();
            try {
                if (expected == webSocket) {
                    scheduleReconnect();
                }
            } finally {
                connectionLock.unlock();
            }
        }

        private void scheduleReconnect() {
            connectionLock.lock();
            try {
                if (!running || !ownership.getAsBoolean()) {
                    return;
                }
                WebSocket socket = webSocket;
                webSocket = null;
                ready = false;
                cancel(heartbeat);
                cancel(readyTimeout);
                if (socket != null) socket.abort();
                channelRegistry.disconnected(channelKey);
                incomingLock.lock();
                try {
                    incoming.setLength(0);
                } finally {
                    incomingLock.unlock();
                }
                cancel(reconnect);
                reconnect = scheduler.schedule(() -> connectionExecutor.execute(this::connect),
                        Instant.now().plus(instance.reconnectDelay()));
            } finally {
                connectionLock.unlock();
            }
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
