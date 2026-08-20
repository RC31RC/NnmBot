package com.nnm.bot.core.onebot;

import com.nnm.bot.core.onebot.config.OneBotProperties;
import com.nnm.bot.core.channel.BotChannelKey;
import com.nnm.bot.core.channel.BotChannelCapability;
import com.nnm.bot.core.channel.BotChannelConfiguration;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
        Map<ConnectionKey, List<OneBotProperties.Instance>> grouped = new LinkedHashMap<>();
        for (OneBotProperties.Instance instance : properties.getInstances()) {
            if (!instance.isEnabled()) {
                continue;
            }
            ConnectionKey key = new ConnectionKey(
                    instance.getConnection().getUrl(),
                    instance.getConnection().getAccessToken());
            grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(instance);
        }
        grouped.values().forEach(instances -> connections.add(new ManagedConnection(instances)));
    }

    private record ConnectionKey(String url, String accessToken) {
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

        private final List<OneBotProperties.Instance> instances;
        private final StandardWebSocketClient client;
        private final WebSocketHttpHeaders headers;
        private final URI uri;
        private final AtomicBoolean connected = new AtomicBoolean();
        private final AtomicBoolean connecting = new AtomicBoolean();
        private final Set<Long> configuredAccounts;
        private final Set<Long> knownAccounts = ConcurrentHashMap.newKeySet();
        private final OneBotProperties.Instance primaryInstance;
        private volatile CompletableFuture<WebSocketSession> connectionFuture;
        private volatile WebSocketSession session;
        private ScheduledFuture<?> reconnectTask;

        private ManagedConnection(List<OneBotProperties.Instance> instances) {
            this.instances = List.copyOf(instances);
            this.primaryInstance = this.instances.getFirst();
            this.client = new StandardWebSocketClient();
            this.client.setTaskExecutor(taskExecutor);
            this.headers = new WebSocketHttpHeaders();
            this.uri = URI.create(primaryInstance.getConnection().getUrl());
            String token = primaryInstance.getConnection().getAccessToken();
            if (token != null && !token.isBlank()) {
                headers.set(HttpHeaders.AUTHORIZATION,
                        token.startsWith("Bearer ") ? token : "Bearer " + token);
            }
            this.configuredAccounts = this.instances.stream()
                    .flatMap(instance -> instance.configuredAccountIds().stream())
                    .map(this::parseAccountId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            this.knownAccounts.addAll(configuredAccounts);
        }

        private void start() {
            configuredAccounts.forEach(account -> channelRegistry.connecting(key(account)));
            connect();
            reconnectTask = scheduler.scheduleWithFixedDelay(
                    this::connect, primaryInstance.getRuntime().getReconnectInterval());
        }

        private void connect() {
            if (!running || connected.get() || !connecting.compareAndSet(false, true)) {
                return;
            }
            connectionFuture = client.execute(new ConnectionHandler(), headers, uri);
            connectionFuture.whenComplete((connectedSession, error) -> {
                connecting.set(false);
                if (error != null) {
                    if (!running) {
                        markKnown(BotChannelStatusAction.DISCONNECTED);
                        return;
                    }
                    markKnown(BotChannelStatusAction.FAILED, error);
                    log.warn("OneBot WebSocket connection failed, instanceIds={}: {}",
                            instanceIds(), error.getMessage());
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
                    log.debug("OneBot WebSocket close failed, instanceIds={}: {}",
                            instanceIds(), exception.getMessage());
                }
            }
            connected.set(false);
            connecting.set(false);
            markKnown(BotChannelStatusAction.DISCONNECTED);
        }

        private boolean accepts(String accountId) {
            String normalizedAccountId = normalizeAccountId(accountId);
            return instances.stream().anyMatch(instance -> instance.allowsAccount(normalizedAccountId));
        }

        private void registerAccount(String accountId) {
            String normalizedAccountId = normalizeAccountId(accountId);
            long numericAccountId = parseAccountId(normalizedAccountId);
            knownAccounts.add(numericAccountId);
            OneBotProperties.Instance owner = instances.stream()
                    .filter(instance -> instance.configuredAccountIds().contains(normalizedAccountId))
                    .findFirst()
                    .orElse(primaryInstance);
            BotChannelKey key = key(numericAccountId);
            channelRegistry.registerIfAbsent(
                    owner.channelInstanceId(normalizedAccountId),
                    key,
                    BotChannelConfiguration.ONEBOT_CAPABILITIES,
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
            return instances.stream().map(OneBotProperties.Instance::getId).collect(Collectors.joining(","));
        }

        private BotChannelKey key(long account) {
            return new BotChannelKey(BotPlatform.ONEBOT, String.valueOf(account));
        }

        private long parseAccountId(String accountId) {
            return Long.parseLong(accountId);
        }

        private boolean isValidAccountId(String accountId) {
            try {
                return parseAccountId(accountId) > 0L;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        private String normalizeAccountId(String accountId) {
            if (accountId == null || accountId.isBlank()) {
                throw new IllegalArgumentException("OneBot self_id must not be blank");
            }
            return accountId.trim();
        }

        private enum BotChannelStatusAction {
            CONNECTING, CONNECTED, DISCONNECTED, FAILED
        }

        private final class ConnectionHandler extends WebSocketHandlerDecorator {

            private ConnectionHandler() {
                super(handler);
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                super.afterConnectionEstablished(session);
                ManagedConnection.this.session = session;
                connected.set(true);
                markKnown(BotChannelStatusAction.CONNECTED);
            }

            @Override
            public void handleMessage(WebSocketSession session,
                                      WebSocketMessage<?> message) throws Exception {
                String actualAccount = messageAccountId(message);
                if (actualAccount != null) {
                    String normalizedAccount = normalizeAccountId(actualAccount);
                    if (!isValidAccountId(normalizedAccount) || !accepts(normalizedAccount)) {
                        rejectUnknownAccount(session, normalizedAccount);
                        return;
                    }
                    registerAccount(normalizedAccount);
                    handler.trackAccount(session, parseAccountId(normalizedAccount));
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
                markKnown(BotChannelStatusAction.DISCONNECTED);
                super.afterConnectionClosed(session, closeStatus);
            }

            @Override
            public void handleTransportError(WebSocketSession session,
                                             Throwable exception) throws Exception {
                connected.set(false);
                markKnown(BotChannelStatusAction.FAILED, exception);
                super.handleTransportError(session, exception);
            }

            private String messageAccountId(WebSocketMessage<?> message) {
                if (!(message.getPayload() instanceof String payload)) {
                    return null;
                }
                try {
                    Object value = JsonObjectWrapper.parseObject(payload).get("self_id");
                    if (value == null) {
                        return null;
                    }
                    String accountId = value.toString().trim();
                    return accountId.isBlank() ? null : accountId;
                } catch (RuntimeException ignored) {
                    return null;
                }
            }

            private void rejectUnknownAccount(WebSocketSession session,
                                              String actualAccount) throws IOException {
                IllegalStateException error = new IllegalStateException(
                        "OneBot connection reported an account that is not allowed: " + actualAccount);
                log.warn("OneBot account rejected, instanceIds={}: {}",
                        instanceIds(), error.getMessage());
                session.close(CloseStatus.POLICY_VIOLATION);
            }
        }
    }
}
