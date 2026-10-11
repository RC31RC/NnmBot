package com.nnm.bot.core.onebot.connection;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import org.springframework.beans.factory.ObjectProvider;
import java.util.function.BooleanSupplier;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.onebot.config.OneBotProperties;
import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.common.utils.JsonUtils;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.http.HttpHeaders;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 管理数据库配置的 OneBot 正向 WebSocket 连接。
 *
 * <p>同一连接地址可以上报多个已配置账号；连接生命周期按实例标识管理，
 * 实际消息发送和状态登记仍按平台账号路由。</p>
 */
@Slf4j
@Component
public class OneBotWebSocketClient implements SmartLifecycle {

    @Resource
    private MultiOneBotWebSocketClientHandler handler;
    @Resource(name = "scheduledExecutor")
    private ThreadPoolTaskScheduler scheduler;
    @Resource(name = "webSocketConnectExecutor")
    private AsyncTaskExecutor taskExecutor;
    @Resource
    private BotChannelRegistry channelRegistry;
    @Resource
    private OneBotProperties properties;
    @Resource
    private ObjectProvider<ClusterBotRuntime> clusterProvider;

    private final ConcurrentHashMap<String, ManagedConnection> connections =
            new ConcurrentHashMap<>();
    private volatile boolean running;

    @Override
    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.connections.values().forEach(ManagedConnection::start);
    }

    @Override
    public void stop() {
        stop(() -> { });
    }

    @Override
    public void stop(Runnable callback) {
        this.running = false;
        this.connections.values().forEach(ManagedConnection::stop);
        callback.run();
    }

    /**
     * 启动或原子替换一个实例连接，旧连接会在新配置登记后停止。
     */
    public synchronized void startInstance(BotChannelInstanceRecord record) {
        if (record.platform() != BotPlatform.ONEBOT) {
            throw new BusinessException(
                    BotChannelErrorCode.PLATFORM_MISMATCH,
                    "OneBot 客户端不能启动协议: " + record.platform());
        }
        this.validate(record);
        ManagedConnection replacement = new ManagedConnection(List.of(record));
        ManagedConnection previous = this.connections.put(record.instanceId(), replacement);
        if (ObjectUtil.isNotNull(previous)) {
            previous.stop();
        }
        this.running = true;
        replacement.start();
    }

    public synchronized void stopInstance(String instanceId) {
        ManagedConnection connection = this.connections.remove(instanceId);
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
        return Integer.MAX_VALUE - 100;
    }

    @PreDestroy
    void destroy() {
        stop();
    }

    private final class ManagedConnection {

        private final List<BotChannelInstanceRecord> instances;
        private final StandardWebSocketClient client;
        private final WebSocketHttpHeaders headers;
        private final URI uri;
        private final AtomicBoolean connected = new AtomicBoolean();
        private final AtomicBoolean connecting = new AtomicBoolean();
        private final Set<Long> configuredAccounts;
        private final Set<Long> knownAccounts = ConcurrentHashMap.newKeySet();
        private final BotChannelInstanceRecord primaryInstance;
        private final AtomicBoolean active = new AtomicBoolean();
        private final BooleanSupplier ownership;
        private volatile CompletableFuture<WebSocketSession> connectionFuture;
        private volatile WebSocketSession session;
        private ScheduledFuture<?> reconnectTask;

        private ManagedConnection(List<BotChannelInstanceRecord> instances) {
            this.instances = List.copyOf(instances);
            this.primaryInstance = this.instances.getFirst();
            ClusterBotRuntime cluster = clusterProvider == null ? null : clusterProvider.getIfAvailable();
            BooleanSupplier lease = cluster == null ? () -> true : cluster.guard(
                    new BotChannelKey(primaryInstance.platform(), primaryInstance.botAccountId()));
            this.ownership = () -> active.get() && lease.getAsBoolean();
            this.client = new StandardWebSocketClient();
            this.client.setTaskExecutor(OneBotWebSocketClient.this.taskExecutor);
            this.headers = new WebSocketHttpHeaders();
            this.uri = URI.create(primaryInstance.connectionUrl());
            String token = primaryInstance.accessToken();
            if (StrUtil.isNotBlank(token)) {
                headers.set(HttpHeaders.AUTHORIZATION,
                        token.startsWith("Bearer ") ? token : "Bearer " + token);
            }
            this.configuredAccounts = this.instances.stream()
                    .map(BotChannelInstanceRecord::botAccountId)
                    .map(this::parseAccountId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            this.knownAccounts.addAll(configuredAccounts);
        }

        private void start() {
            if (!active.compareAndSet(false, true)) {
                return;
            }
            configuredAccounts.forEach(account ->
                    OneBotWebSocketClient.this.channelRegistry.connecting(key(account)));
            connect();
            reconnectTask = OneBotWebSocketClient.this.scheduler.scheduleWithFixedDelay(
                    this::connect, primaryInstance.reconnectInterval());
        }

        private void connect() {
            if (!running || !ownership.getAsBoolean() || connected.get()
                    || !connecting.compareAndSet(false, true)) {
                return;
            }
            connectionFuture = client.execute(new ConnectionHandler(), headers, uri);
            connectionFuture.whenComplete((connectedSession, error) -> {
                connecting.set(false);
                if (!ownership.getAsBoolean()) {
                    closeQuietly(connectedSession);
                    return;
                }
                if (ObjectUtil.isNotNull(error)) {
                    if (!running) {
                        markKnown(BotChannelStatusAction.DISCONNECTED);
                        return;
                    }
                    markKnown(BotChannelStatusAction.FAILED, error);
                    log.warn("OneBot WebSocket connection failed, instanceIds={}",
                            instanceIds(), error);
                    return;
                }
                if (!connected.get()) {
                    return;
                }
                session = connectedSession;
                log.info("OneBot WebSocket connected, instanceIds={}, configuredAccounts={}",
                        instanceIds(), configuredAccounts);
            });
        }

        private void stop() {
            active.set(false);
            if (ObjectUtil.isNotNull(reconnectTask)) {
                reconnectTask.cancel(false);
                reconnectTask = null;
            }
            CompletableFuture<WebSocketSession> pendingConnection = connectionFuture;
            if (ObjectUtil.isNotNull(pendingConnection) && !pendingConnection.isDone()) {
                pendingConnection.cancel(true);
            }
            WebSocketSession connectedSession = session;
            session = null;
            if (ObjectUtil.isNotNull(connectedSession) && connectedSession.isOpen()) {
                try {
                    connectedSession.close(CloseStatus.NORMAL);
                } catch (IOException exception) {
                    log.debug("OneBot WebSocket close failed, instanceIds={}",
                            instanceIds(), exception);
                }
            }
            connected.set(false);
            connecting.set(false);
            markKnown(BotChannelStatusAction.DISCONNECTED);
        }

        private void closeQuietly(WebSocketSession connectedSession) {
            if (ObjectUtil.isNull(connectedSession) || !connectedSession.isOpen()) {
                return;
            }
            try {
                connectedSession.close(CloseStatus.NORMAL);
            } catch (IOException exception) {
                log.debug("OneBot delayed WebSocket close failed, instanceIds={}",
                        instanceIds(), exception);
            }
        }

        private boolean accepts(String accountId) {
            String normalizedAccountId = normalizeAccountId(accountId);
            return instances.stream().anyMatch(instance ->
                    instance.botAccountId().equals(normalizedAccountId));
        }

        private void registerAccount(String accountId) {
            String normalizedAccountId = normalizeAccountId(accountId);
            long numericAccountId = parseAccountId(normalizedAccountId);
            knownAccounts.add(numericAccountId);
            BotChannelInstanceRecord owner = instances.stream()
                    .filter(instance -> instance.botAccountId().equals(normalizedAccountId))
                    .findFirst()
                    .orElse(primaryInstance);
            BotChannelKey key = key(numericAccountId);
            channelRegistry.registerIfAbsent(
                    owner.instanceId(),
                    key,
                    BotPlatform.ONEBOT.capabilities(),
                    true);
            channelRegistry.connected(key);
        }

        private void markKnown(BotChannelStatusAction action) {
            markKnown(action, null);
        }

        private void markKnown(BotChannelStatusAction action, Throwable error) {
            for (long account : knownAccounts) {
                BotChannelKey key = key(account);
                switch (action) {
                    case CONNECTING -> channelRegistry.connecting(key);
                    case CONNECTED -> channelRegistry.connected(key);
                    case DISCONNECTED -> channelRegistry.disconnected(key);
                    case FAILED -> channelRegistry.failed(key, error);
                }
            }
        }

        private String instanceIds() {
            return instances.stream()
                    .map(BotChannelInstanceRecord::instanceId)
                    .collect(Collectors.joining(","));
        }

        private BotChannelKey key(long account) {
            return new BotChannelKey(BotPlatform.ONEBOT, String.valueOf(account));
        }

        private long parseAccountId(String accountId) {
            try {
                long value = Long.parseLong(accountId);
                if (value <= 0L) {
                    throw new NumberFormatException("account id must be positive");
                }
                return value;
            } catch (NumberFormatException exception) {
                throw new BusinessException(
                        BotChannelErrorCode.INVALID_ACCOUNT_ID,
                        "OneBot self_id 不是有效的 QQ 账号: " + accountId,
                        exception);
            }
        }

        private String normalizeAccountId(String accountId) {
            if (StrUtil.isBlank(accountId)) {
                throw new BusinessException(
                        BotChannelErrorCode.MESSAGE_PARSE_FAILED,
                        "OneBot self_id 不能为空");
            }
            return accountId.trim();
        }

        private enum BotChannelStatusAction {
            CONNECTING, CONNECTED, DISCONNECTED, FAILED
        }

        private final class ConnectionHandler extends WebSocketHandlerDecorator {

            private volatile WebSocketSession shiroSession;

            private ConnectionHandler() {
                super(handler);
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                if (!ownership.getAsBoolean()) {
                    session.close(CloseStatus.NORMAL);
                    return;
                }
                WebSocketSession concurrentSession = new ConcurrentWebSocketSessionDecorator(
                        session,
                        properties.sendTimeLimitMillis(),
                        properties.getSendBufferSizeBytes());
                WebSocketSession serializedSession =
                        new SerializedWebSocketSession(concurrentSession, ownership);
                this.shiroSession = serializedSession;
                super.afterConnectionEstablished(serializedSession);
                ManagedConnection.this.session = serializedSession;
                connected.set(true);
                markKnown(BotChannelStatusAction.CONNECTED);
            }

            @Override
            public void handleMessage(WebSocketSession session,
                                      WebSocketMessage<?> message) throws Exception {
                if (!ownership.getAsBoolean()) {
                    session.close(CloseStatus.POLICY_VIOLATION);
                    return;
                }
                WebSocketSession concurrentSession = sessionForShiro(session);
                String actualAccount = messageAccountId(message);
                if (ObjectUtil.isNotNull(actualAccount)) {
                    String normalizedAccount = normalizeAccountId(actualAccount);
                    parseAccountId(normalizedAccount);
                    if (!accepts(normalizedAccount)) {
                        rejectUnknownAccount(concurrentSession, normalizedAccount);
                        return;
                    }
                    registerAccount(normalizedAccount);
                    handler.trackAccount(concurrentSession, parseAccountId(normalizedAccount));
                }
                super.handleMessage(concurrentSession, message);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session,
                                              CloseStatus closeStatus) throws Exception {
                WebSocketSession concurrentSession = sessionForShiro(session);
                connected.set(false);
                if (ManagedConnection.this.session == concurrentSession) {
                    ManagedConnection.this.session = null;
                }
                if (active.get()) {
                    markKnown(BotChannelStatusAction.DISCONNECTED);
                }
                try {
                    super.afterConnectionClosed(concurrentSession, closeStatus);
                } finally {
                    this.shiroSession = null;
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session,
                                             Throwable exception) throws Exception {
                connected.set(false);
                if (active.get()) {
                    markKnown(BotChannelStatusAction.FAILED, exception);
                }
                super.handleTransportError(sessionForShiro(session), exception);
            }

            private WebSocketSession sessionForShiro(WebSocketSession fallback) {
                WebSocketSession current = this.shiroSession;
                return ObjectUtil.isNotNull(current) ? current : fallback;
            }

            private String messageAccountId(WebSocketMessage<?> message) {
                if (!(message.getPayload() instanceof String payload)) {
                    return null;
                }
                if (!JsonUtils.isValid(payload)) {
                    throw new BusinessException(
                            BotChannelErrorCode.MESSAGE_PARSE_FAILED,
                            "OneBot 消息不是合法 JSON");
                }
                try {
                    Object value = JsonObjectWrapper.parseObject(payload).get("self_id");
                    if (ObjectUtil.isNull(value)) {
                        return null;
                    }
                    String accountId = value.toString().trim();
                    return accountId.isBlank() ? null : accountId;
                } catch (RuntimeException exception) {
                    throw new BusinessException(
                            BotChannelErrorCode.MESSAGE_PARSE_FAILED,
                            "解析 OneBot 消息中的 self_id 失败",
                            exception);
                }
            }

            private void rejectUnknownAccount(WebSocketSession session,
                                              String actualAccount) throws IOException {
                BusinessException error = new BusinessException(
                        BotChannelErrorCode.ACCOUNT_NOT_ALLOWED,
                        "OneBot 连接上报了未授权的机器人账号: " + actualAccount);
                log.warn("OneBot account rejected, instanceIds={}",
                        instanceIds(), error);
                session.close(CloseStatus.POLICY_VIOLATION);
            }
        }
    }

    private void validate(BotChannelInstanceRecord record) {
        if (ObjectUtil.isNull(record)) {
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_CONFIG_INVALID,
                    "OneBot 机器人实例记录不能为空");
        }
        if (StrUtil.isBlank(record.instanceId())) {
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_CONFIG_INVALID,
                    "OneBot 机器人实例标识不能为空");
        }
        if (StrUtil.isBlank(record.botAccountId())) {
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_CONFIG_INVALID,
                    "OneBot 机器人账号不能为空");
        }
        this.validateAccountId(record.botAccountId());
        if (StrUtil.isBlank(record.connectionUrl())) {
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_CONFIG_INVALID,
                    "OneBot WebSocket 地址不能为空");
        }
        if (ObjectUtil.isNull(record.reconnectInterval())
                || record.reconnectInterval().isZero()
                || record.reconnectInterval().isNegative()) {
            throw new BusinessException(
                    BotChannelErrorCode.INSTANCE_CONFIG_INVALID,
                    "OneBot 重连间隔必须为正数");
        }
    }

    private void validateAccountId(String accountId) {
        try {
            long value = Long.parseLong(ObjectUtil.isNull(accountId) ? "" : accountId.trim());
            if (value <= 0L) {
                throw new NumberFormatException("account id must be positive");
            }
        } catch (NumberFormatException exception) {
            throw new BusinessException(
                    BotChannelErrorCode.INVALID_ACCOUNT_ID,
                    "OneBot 账号不是有效的 QQ 账号: " + accountId,
                    exception);
        }
    }
}
