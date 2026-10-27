package com.nnm.bot.core.qqofficial.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.http.WebSocket.Listener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.port.BotGroupMemberEventSink;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.constant.BotChannelStatus;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.qqofficial.api.QqOfficialTokenClient;
import com.nnm.bot.core.plugin.execution.CommandIngressService;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandIngressResult;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.qqofficial.discovery.QqOfficialObservedScopeStore;
import java.net.http.WebSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class QqOfficialGatewayClientTest {
    private final QqOfficialGatewayClient client = new QqOfficialGatewayClient();
    private final BotChannelRegistry registry = new BotChannelRegistry();
    private final BotChannelKey key = new BotChannelKey(BotPlatform.QQ_OFFICIAL, "app");
    private final AtomicBoolean owned = new AtomicBoolean(true);
    private Listener listener;
    private WebSocket socket;
    private ThreadPoolTaskScheduler scheduler;
    private QqOfficialObservedScopeStore observedScopes;
    private BotGroupMemberEventSink memberEvents;

    @BeforeEach
    void setup() {
        ClusterBotRuntime cluster = mock(ClusterBotRuntime.class);
        when(cluster.guard(key)).thenReturn(owned::get);
        StaticListableBeanFactory beans = new StaticListableBeanFactory(Map.of("cluster", cluster));
        ReflectionTestUtils.setField(client, "clusterProvider", beans.getBeanProvider(ClusterBotRuntime.class));
        ReflectionTestUtils.setField(client, "httpClient", mock(HttpClient.class));
        ReflectionTestUtils.setField(client, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(client, "channelRegistry", registry);
        ReflectionTestUtils.setField(client, "eventExecutor", directExecutor());
        ReflectionTestUtils.setField(client, "connectionExecutor", mock(AsyncTaskExecutor.class));
        scheduler = mock(ThreadPoolTaskScheduler.class);
        ReflectionTestUtils.setField(client, "scheduler", scheduler);
        observedScopes = mock(QqOfficialObservedScopeStore.class);
        ObjectProvider<QqOfficialObservedScopeStore> observedProvider = mock(ObjectProvider.class);
        when(observedProvider.getIfAvailable()).thenReturn(observedScopes);
        ReflectionTestUtils.setField(client, "observedScopeStoreProvider", observedProvider);
        memberEvents = mock(BotGroupMemberEventSink.class);
        ObjectProvider<BotGroupMemberEventSink> memberEventsProvider = mock(ObjectProvider.class);
        when(memberEventsProvider.getIfAvailable()).thenReturn(memberEvents);
        ReflectionTestUtils.setField(client, "memberEventSinkProvider", memberEventsProvider);
        registry.register("qq", key, BotPlatform.QQ_OFFICIAL.capabilities(), true);
        registry.connecting(key);
        client.startInstance(new BotChannelInstanceRecord(BotPlatform.QQ_OFFICIAL, "qq", "app",
                true, null, null, "app", "secret", "http://localhost/token",
                "http://localhost/api", null, Duration.ofSeconds(5), 117440512,
                Duration.ofSeconds(5), Duration.ofSeconds(5)));
        Map<?, ?> connections = (Map<?, ?>) ReflectionTestUtils.getField(client, "connections");
        listener = (WebSocket.Listener) connections.get("qq");
        socket = mock(WebSocket.class);
        when(socket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(socket));
        listener.onOpen(socket);
    }

    @AfterEach
    void cleanup() {
        client.destroy();
    }

    @Test
    void socketOpenIsNotReadyAndFragmentedReadyEstablishesConnection() {
        assertThat(registry.find(key).orElseThrow().status()).isEqualTo(BotChannelStatus.CONNECTING);
        listener.onText(socket, "{\"op\":0,\"t\":\"READY\",", false);
        assertThat(registry.find(key).orElseThrow().status()).isEqualTo(BotChannelStatus.CONNECTING);
        listener.onText(socket, "\"s\":1,\"d\":{\"session_id\":\"session\"}}", true);
        assertThat(registry.find(key).orElseThrow().status()).isEqualTo(BotChannelStatus.CONNECTED);
    }

    @Test
    void expiredOwnershipClosesSocketAndClearsConnectedStateWithoutReconnect() {
        listener.onText(socket, "{\"op\":0,\"t\":\"READY\",\"d\":{\"session_id\":\"session\"}}", true);
        listener.onText(socket, "{\"op\":0,", false);
        ScheduledFuture heartbeat = mock(ScheduledFuture.class);
        ScheduledFuture reconnect = mock(ScheduledFuture.class);
        ReflectionTestUtils.setField(listener, "heartbeat", heartbeat);
        ReflectionTestUtils.setField(listener, "reconnect", reconnect);
        clearInvocations(scheduler, socket);
        owned.set(false);

        listener.onText(socket, "{\"op\":0,\"t\":\"GROUP_MEMBER_ADD\",\"d\":{\"group_openid\":\"group\",\"member_openid\":\"member\"}}", true);

        verify(socket).abort();
        verify(socket, never()).request(anyLong());
        verify(heartbeat).cancel(false);
        verify(reconnect).cancel(false);
        assertThat(ReflectionTestUtils.getField(listener, "incoming").toString()).isEmpty();
        verifyNoInteractions(memberEvents, observedScopes);
        verify(scheduler, never()).schedule(any(Runnable.class), any(Instant.class));
        assertThat(ReflectionTestUtils.getField(listener, "webSocket")).isNull();
        assertThat(registry.find(key).orElseThrow().status()).isEqualTo(BotChannelStatus.DISCONNECTED);

        // A recovered lease cannot authorize this revoked callback; the cluster creates a new listener.
        owned.set(true);
        WebSocket late = mock(WebSocket.class);
        listener.onOpen(late);
        verify(late).abort();

        BotChannelInstanceRecord record = (BotChannelInstanceRecord) ReflectionTestUtils.getField(listener, "instance");
        client.startInstance(record);
        Map<?, ?> connections = (Map<?, ?>) ReflectionTestUtils.getField(client, "connections");
        Listener recovered = (WebSocket.Listener) connections.get("qq");
        WebSocket freshSocket = mock(WebSocket.class);
        recovered.onOpen(freshSocket);
        recovered.onText(freshSocket, "{\"op\":0,\"t\":\"READY\",\"d\":{\"session_id\":\"fresh-session\"}}", true);
        assertThat(registry.find(key).orElseThrow().status()).isEqualTo(BotChannelStatus.CONNECTED);
        recovered.onText(freshSocket,
                "{\"op\":0,\"t\":\"GROUP_MEMBER_ADD\",\"d\":{"
                        + "\"group_openid\":\"group\",\"member_openid\":\"member\"}}", true);
        verify(observedScopes).observeMember("app", "group", "member", null, null);
        verify(memberEvents).handleMemberJoined(
                eq(BotPlatform.QQ_OFFICIAL), eq("app"), eq("group"), any());
        verify(freshSocket, never()).abort();
    }

    @Test
    void staleTextCannotAbortOrDisconnectReplacementSocket() {
        ReflectionTestUtils.invokeMethod(listener, "scheduleReconnect");
        WebSocket replacement = mock(WebSocket.class);
        listener.onOpen(replacement);
        listener.onText(replacement, "{\"op\":0,\"t\":\"RESUMED\",\"s\":2,\"d\":{}}", true);

        listener.onText(socket, "{\"op\":7}", true);

        verify(replacement, never()).abort();
        assertThat(ReflectionTestUtils.getField(listener, "webSocket")).isSameAs(replacement);
        assertThat(registry.find(key).orElseThrow().status()).isEqualTo(BotChannelStatus.CONNECTED);
    }

    @Test
    void missingHeartbeatAcknowledgmentDisconnectsAndOldCallbacksCannotAffectNewSocket() {
        ReflectionTestUtils.invokeMethod(listener, "sendHeartbeat", socket);
        ReflectionTestUtils.invokeMethod(listener, "sendHeartbeat", socket);
        verify(socket).abort();
        assertThat(registry.find(key).orElseThrow().status()).isEqualTo(BotChannelStatus.DISCONNECTED);
        WebSocket replacement = mock(WebSocket.class);
        listener.onOpen(replacement);
        listener.onText(replacement, "{\"op\":0,\"t\":\"RESUMED\",\"s\":2,\"d\":{}}", true);
        listener.onError(socket, new IllegalStateException("stale error"));
        listener.onClose(socket, 1001, "stale close");
        assertThat(registry.find(key).orElseThrow().status()).isEqualTo(BotChannelStatus.CONNECTED);
        assertThat(ReflectionTestUtils.getField(listener, "webSocket")).isSameAs(replacement);
    }

    @Test
    void stopRejectsLateOpenAndDoesNotRestartSocket() {
        client.stopInstance("qq");
        WebSocket late = mock(WebSocket.class);
        listener.onOpen(late);
        verify(late).abort();
        assertThat(registry.find(key).orElseThrow().status()).isEqualTo(BotChannelStatus.DISCONNECTED);
        assertThat(ReflectionTestUtils.getField(listener, "webSocket")).isNull();
    }

    @Test
    void timeoutAlreadyRunningCannotDisconnectReadySocket() {
        listener.onText(socket, "{\"op\":0,\"t\":\"READY\",\"d\":{\"session_id\":\"session\"}}", true);

        ReflectionTestUtils.invokeMethod(listener, "checkReadyTimeout", socket);

        verify(socket, never()).abort();
        assertThat(registry.find(key).orElseThrow().status()).isEqualTo(BotChannelStatus.CONNECTED);
    }

    @Test
    void heartbeatStartsOnlyAfterIdentificationSendCompletes() {
        CompletableFuture<WebSocket> send = new CompletableFuture<WebSocket>();
        prepareHello(send);

        verify(scheduler, never()).scheduleAtFixedRate(any(Runnable.class), any(Duration.class));
        send.complete(socket);

        verify(scheduler).scheduleAtFixedRate(any(Runnable.class), eq(Duration.ofMillis(1000)));
    }

    @Test
    void identificationPayloadContainsConfiguredIntentsAndShard() throws Exception {
        CompletableFuture<WebSocket> send = new CompletableFuture<WebSocket>();
        prepareHello(send);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(socket).sendText(payloadCaptor.capture(), eq(true));
        JsonNode payload = new ObjectMapper().readTree(payloadCaptor.getValue());

        assertThat(payload.path("op").asInt()).isEqualTo(2);
        assertThat(payload.path("d").path("token").asText()).isEqualTo("QQBot token");
        assertThat(payload.path("d").path("intents").asLong()).isEqualTo(117440512L);
        assertThat(payload.path("d").path("shard"))
                .isEqualTo(new ObjectMapper().createArrayNode().add(0).add(1));
    }

    @Test
    void identificationFailureDisconnectsWithoutStartingHeartbeat() {
        CompletableFuture<WebSocket> send = new CompletableFuture<WebSocket>();
        prepareHello(send);

        send.completeExceptionally(new IllegalStateException("send failed"));

        verify(socket).abort();
        verify(scheduler, never()).scheduleAtFixedRate(any(Runnable.class), any(Duration.class));
        assertThat(ReflectionTestUtils.getField(listener, "webSocket")).isNull();
    }

    @Test
    void lateIdentificationCompletionCannotScheduleHeartbeatAfterStop() {
        CompletableFuture<WebSocket> send = new CompletableFuture<WebSocket>();
        prepareHello(send);
        client.stopInstance("qq");

        send.complete(socket);

        verify(scheduler, never()).scheduleAtFixedRate(any(Runnable.class), any(Duration.class));
        assertThat(ReflectionTestUtils.getField(listener, "webSocket")).isNull();
    }

    @Test
    void observesRobotGroupAndMemberLifecycleEvents() {
        listener.onText(socket,
                "{\"op\":0,\"t\":\"GROUP_ADD_ROBOT\",\"d\":{\"group_openid\":\"group-1\"}}",
                true);
        listener.onText(socket,
                "{\"op\":0,\"t\":\"GROUP_MEMBER_ADD\",\"d\":{"
                        + "\"group_openid\":\"group-1\",\"member_openid\":\"member-1\"}}",
                true);
        listener.onText(socket,
                "{\"op\":0,\"t\":\"GROUP_MEMBER_REMOVE\",\"d\":{"
                        + "\"group_openid\":\"group-1\",\"member_openid\":\"member-1\"}}",
                true);
        listener.onText(socket,
                "{\"op\":0,\"t\":\"GROUP_DEL_ROBOT\",\"d\":{\"group_openid\":\"group-1\"}}",
                true);

        verify(observedScopes).observeGroup("app", "group-1");
        verify(observedScopes).observeMember("app", "group-1", "member-1", null, null);
        verify(observedScopes).removeMember("app", "group-1", "member-1");
        verify(observedScopes).deactivateGroup("app", "group-1");
        verify(memberEvents).handleMemberJoined(
                eq(BotPlatform.QQ_OFFICIAL), eq("app"), eq("group-1"), any());
        verify(memberEvents).handleMemberLeft(
                eq(BotPlatform.QQ_OFFICIAL), eq("app"), eq("group-1"), eq("member-1"));
    }

    private void prepareHello(CompletableFuture<WebSocket> send) {
        QqOfficialTokenClient tokens = mock(QqOfficialTokenClient.class);
        when(tokens.getAccessToken()).thenReturn("token");
        ReflectionTestUtils.setField(listener, "tokenClient", tokens);
        when(socket.sendText(anyString(), eq(true))).thenReturn(send);
        listener.onText(socket, "{\"op\":10,\"d\":{\"heartbeat_interval\":1000}}", true);
    }

    @Test
    void scopeObservationFailureDoesNotSwallowAnIncomingCommand() {
        CommandIngressService dispatcher = mock(CommandIngressService.class);
        StaticListableBeanFactory beans = new StaticListableBeanFactory(Map.of("dispatcher", dispatcher));
        ReflectionTestUtils.setField(client, "commandIngressProvider",
                beans.getBeanProvider(CommandIngressService.class));
        doThrow(new IllegalStateException("observation unavailable"))
                .when(observedScopes).observeGroup("app", "group");

        listener.onText(socket, """
                {"op":0,"t":"GROUP_AT_MESSAGE_CREATE","d":{
                  "id":"message-1","group_openid":"group",
                  "author":{"member_openid":"member"},"content":"签到"
                }}
                """, true);

        ArgumentCaptor<BotMessageContext> context = ArgumentCaptor.forClass(BotMessageContext.class);
        verify(dispatcher).accept(context.capture(), any());
        assertThat(context.getValue().messageId()).isEqualTo("message-1");
        assertThat(context.getValue().message()).isEqualTo("签到");
    }

    @Test
    void saturatedAuxiliaryQueueAndRejectedCommandDoNotDelayHeartbeatOrStop() throws Exception {
        CommandIngressService ingress = mock(CommandIngressService.class);
        when(ingress.accept(any(), any())).thenReturn(CommandIngressResult.REJECTED_CAPACITY);
        StaticListableBeanFactory beans = new StaticListableBeanFactory(Map.of("ingress", ingress));
        ReflectionTestUtils.setField(client, "commandIngressProvider", beans.getBeanProvider(CommandIngressService.class));
        ThreadPoolTaskExecutor events = new ThreadPoolTaskExecutor();
        events.setCorePoolSize(1);
        events.setMaxPoolSize(1);
        events.setQueueCapacity(1);
        events.setWaitForTasksToCompleteOnShutdown(true);
        events.setAwaitTerminationSeconds(5);
        events.initialize();
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ReflectionTestUtils.setField(client, "eventExecutor", events);
        try {
            events.execute(() -> {
                started.countDown();
                try {
                    release.await();
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(interrupted);
                }
            });
            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
            events.execute(() -> { });
            listener.onText(socket, "{\"op\":0,\"t\":\"READY\",\"d\":{\"session_id\":\"ready\"}}", true);
            listener.onText(socket, """
                    {"op":0,"t":"GROUP_AT_MESSAGE_CREATE","d":{
                      "id":"message-1","group_openid":"group",
                      "author":{"member_openid":"member"},"content":"签到"
                    }}
                    """, true);
            verify(ingress).accept(any(), any());
            assertThat(registry.find(key).orElseThrow().status()).isEqualTo(BotChannelStatus.CONNECTED);
            AtomicBoolean awaiting = (AtomicBoolean) ReflectionTestUtils.getField(listener, "awaitingHeartbeatAck");
            awaiting.set(true);
            listener.onText(socket, "{\"op\":11}", true);
            assertThat(awaiting).isFalse();
            client.stopInstance("qq");
            verify(socket).abort();
        } finally {
            release.countDown();
            events.shutdown();
        }
    }

    private AsyncTaskExecutor directExecutor() {
        return new AsyncTaskExecutor() {
            @Override
            public void execute(Runnable task) {
                task.run();
            }

            @Override
            public void execute(Runnable task, long startTimeout) {
                task.run();
            }

            @Override
            public Future<?> submit(Runnable task) {
                task.run();
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                try {
                    return CompletableFuture.completedFuture(task.call());
                } catch (Exception exception) {
                    CompletableFuture<T> future = new CompletableFuture<T>();
                    future.completeExceptionally(exception);
                    return future;
                }
            }
        };
    }
}
