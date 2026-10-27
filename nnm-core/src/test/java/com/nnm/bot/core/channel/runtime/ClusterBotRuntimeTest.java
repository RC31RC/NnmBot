package com.nnm.bot.core.channel.runtime;

import java.util.function.BooleanSupplier;
import org.mockito.InOrder;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import org.redisson.client.codec.Codec;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot;
import com.nnm.bot.core.channel.topology.BotTopologyStore;
import com.nnm.bot.core.schedule.channel.BotRuntimeReconciliationTask;
import java.util.LinkedHashMap;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.config.ClusterProperties;
import io.github.rdt.spring.lease.NodeIdentityLease;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService.Lease;
import io.github.rdt.state.SharedStateStore;
import io.github.rdt.spring.config.RdtProperties;
import io.github.rdt.support.KeySpace;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import io.github.rdt.spring.annotation.RdtScheduled;
import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClusterBotRuntimeTest {
    private final ClusterBotRuntime cluster = new ClusterBotRuntime();
    private final RedisOwnerLeaseService leases = mock(RedisOwnerLeaseService.class);
    private ObjectProvider<BotConnectionAffinityResolver> placementResolver;
    private final SharedStateStore cache = mock(SharedStateStore.class);
    private final BotChannelRuntimeManager manager = mock(BotChannelRuntimeManager.class);
    private final BotChannelInstanceProvider instances = mock(BotChannelInstanceProvider.class);
    private final RMap<String, String> desired = mock(RMap.class);
    private final Lease old = new RedisOwnerLeaseService.Lease("account", "old", 1);
    private final BotChannelKey key = new BotChannelKey(BotPlatform.ONEBOT, "account");
    private final NodeIdentityLease identity = mock(NodeIdentityLease.class);
    private final ListAppender<ILoggingEvent> logs = new ListAppender<>();

    @BeforeEach
    void setup() {
        when(identity.isHealthy()).thenReturn(true);
        when(identity.guard()).thenReturn(() -> identity.isHealthy());
        logs.start();
        ((Logger) LoggerFactory.getLogger(ClusterBotRuntime.class)).addAppender(logs);
        RedissonClient redis = mock(RedissonClient.class);
        when(redis.<String, String>getMap(anyString(), any(Codec.class))).thenReturn(desired);
        RdtProperties rdt = mock(RdtProperties.class);
        when(rdt.instanceId()).thenReturn("node-a");
        ObjectProvider<BotChannelRuntimeManager> runtime = mock(ObjectProvider.class);
        when(runtime.getObject()).thenReturn(manager);
        ReflectionTestUtils.setField(cluster, "rdt", rdt);
        ReflectionTestUtils.setField(cluster, "runtime", runtime);
        ReflectionTestUtils.setField(cluster, "leases", leases);
        ReflectionTestUtils.setField(cluster, "identity", identity);
        ReflectionTestUtils.setField(cluster, "registry", new BotChannelRegistry());
        ReflectionTestUtils.setField(cluster, "properties", new ClusterProperties(Duration.ofMinutes(1), Duration.ofMinutes(5), Duration.ofSeconds(1),
                Duration.ofSeconds(30), Duration.ofSeconds(5), Duration.ofDays(1),
                Duration.ofSeconds(60), Duration.ofSeconds(120),
                Duration.ofSeconds(30), Duration.ofMinutes(2), 128));
        BotRuntimeStateStore state = new BotRuntimeStateStore();
        ReflectionTestUtils.setField(state, "cache", cache);
        ReflectionTestUtils.setField(state, "leases", leases);
        ReflectionTestUtils.setField(state, "redis", redis);
        ReflectionTestUtils.setField(state, "keys", new KeySpace("test"));
        ReflectionTestUtils.setField(state, "properties", ReflectionTestUtils.getField(cluster, "properties"));
        ReflectionTestUtils.setField(cluster, "stateStore", state);
        BotTopologyStore topology = mock(BotTopologyStore.class);
        when(topology.snapshot()).thenAnswer(call -> {
            List<BotChannelInstanceRecord> records = instances.findAll();
            Map<Long, BotChannelInstanceRecord> indexed = new LinkedHashMap<>();
            for (int index = 0; index < records.size(); index++) indexed.put((long) index + 1, records.get(index));
            BotTopologySnapshot snapshot = BotTopologySnapshot.of(indexed, List.of(), List.of());
            BotConnectionAffinityResolver resolver = placementResolver == null ? null : placementResolver.getIfAvailable();
            return new BotTopologySnapshot(snapshot.version(), snapshot.instances(),
                    snapshot.metadata(), resolver == null ? Map.of() : resolver.resolve(records), snapshot.instanceIds(), snapshot.groups(),
                    snapshot.bindings(), snapshot.scopes());
        });
        ReflectionTestUtils.setField(cluster, "topology", topology);
        when(leases.findAll(anyCollection())).thenAnswer(call -> {
            Map<String, Lease> result = new java.util.HashMap<>();
            for (String name : call.<java.util.Collection<String>>getArgument(0)) leases.find(name).ifPresent(value -> result.put(name, value));
            return result;
        });
    }

    @AfterEach
    void detachLogs() {
        ((Logger) LoggerFactory.getLogger(ClusterBotRuntime.class)).detachAppender(logs);
        logs.stop();
    }

    @Test
    void sameNodePlacementChangesKeepTheExistingSocketAndGuard() {
        BotConnectionAffinityResolver resolver = mock(BotConnectionAffinityResolver.class);
        ObjectProvider<BotConnectionAffinityResolver> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(resolver);
        placementResolver = provider;
        when(instances.findAll()).thenReturn(List.of(record("bot")));
        when(desired.readAllMap()).thenReturn(Map.of("bot", "true"));
        when(resolver.resolve(anyList())).thenReturn(Map.of("bot", "original"));
        when(leases.acquire(anyString(), anyString(), any())).thenReturn(Optional.of(old));
        when(leases.renew(any(), any())).thenReturn(true);
        when(leases.isHeld(old)).thenReturn(true);
        when(leases.find(anyString())).thenReturn(Optional.of(new Lease("account", "node-a:1:account", 1)));
        cluster.reconcile();
        BooleanSupplier guard = cluster.guard(key);

        when(resolver.resolve(anyList())).thenReturn(Map.of("bot", "merged"));
        cluster.reconcile();
        cluster.reconcile();

        assertThat(guard.getAsBoolean()).isTrue();
        verify(manager, times(1)).startLocal(eq("bot"), any());
        verify(manager, never()).removeRuntime(anyString());
        assertThat(logs.list.stream().filter(event -> event.getLevel() == Level.WARN)).isEmpty();
    }

    @Test
    void crossNodeChangeWithoutApprovalKeepsHealthyConnectionsAndLogsOnce() {
        BotConnectionAffinityResolver resolver = mock(BotConnectionAffinityResolver.class);
        ObjectProvider<BotConnectionAffinityResolver> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(resolver);
        placementResolver = provider;
        when(instances.findAll()).thenReturn(List.of(record("bot"), record("other", "other-account")));
        when(desired.readAllMap()).thenReturn(Map.of("bot", "true"));
        when(resolver.resolve(anyList())).thenReturn(Map.of("bot", "original", "other", "other"));
        when(leases.acquire(anyString(), anyString(), any())).thenReturn(Optional.of(old));
        when(leases.renew(any(), any())).thenReturn(true);
        when(leases.isHeld(old)).thenReturn(true);
        when(leases.find("bot:ONEBOT:account"))
                .thenReturn(Optional.of(new Lease("account", "node-a:1:account", 1)));
        when(leases.find("bot:ONEBOT:other-account"))
                .thenReturn(Optional.of(new Lease("other-account", "node-b:1:account", 1)));
        cluster.reconcile();
        BooleanSupplier guard = cluster.guard(key);

        when(resolver.resolve(anyList())).thenReturn(Map.of("bot", "merged", "other", "merged"));
        cluster.reconcile();
        cluster.reconcile();

        assertThat(guard.getAsBoolean()).isTrue();
        assertThat(logs.list.stream().filter(event -> event.getLevel() == Level.WARN)).hasSize(1);
        verify(manager, never()).removeRuntime(anyString());
    }

    @Test
    void lostIdentityStopsConnectionsAndReportsTheFailureWithoutRepeatingWarnings() {
        when(instances.findAll()).thenReturn(List.of(record("bot")));
        when(desired.readAllMap()).thenReturn(Map.of("bot", "true"));
        when(leases.acquire(anyString(), anyString(), any())).thenReturn(Optional.of(old));
        when(leases.isHeld(old)).thenReturn(true);
        cluster.reconcile();
        BooleanSupplier guard = cluster.guard(key);
        assertThat(guard.getAsBoolean()).isTrue();
        when(identity.isHealthy()).thenReturn(false);

        cluster.reconcile();
        cluster.reconcile();
        cluster.reconcile();

        assertThat(cluster.reconciliationFailure()).hasValueSatisfying(message -> assertThat(message).contains("正在自动恢复"));
        assertThat(guard.getAsBoolean()).isFalse();
        verify(manager).removeRuntime("bot");
        verify(manager).startLocal(eq("bot"), any());
        assertThat(logs.list.stream().filter(event -> event.getLevel() == Level.WARN)).hasSize(1);
    }

    @Test
    void changedFailureIsLoggedAndSuccessfulReconciliationClearsTheError() {
        when(instances.findAll()).thenThrow(new IllegalStateException("database unavailable"))
                .thenThrow(new IllegalStateException("database unavailable"))
                .thenThrow(new IllegalStateException("database timeout"))
                .thenReturn(List.of());
        assertThatThrownBy(cluster::reconcile).hasMessage("database unavailable");
        assertThatThrownBy(cluster::reconcile).hasMessage("database unavailable");
        assertThatThrownBy(cluster::reconcile).hasMessage("database timeout");
        assertThat(cluster.reconciliationFailure()).contains("database timeout");

        cluster.reconcile();

        assertThat(cluster.reconciliationFailure()).isEmpty();
        assertThat(logs.list.stream().filter(event -> event.getFormattedMessage()
                .equals("Bot cluster reconciliation recovered"))).hasSize(1);
    }

    private BotChannelInstanceRecord record(String id) {
        return record(id, "account");
    }

    private BotChannelInstanceRecord record(String id, String account) {
        return new BotChannelInstanceRecord(BotPlatform.ONEBOT, id, account, true,
                "ws://localhost:3001", "", null, null, null, null, null,
                Duration.ofSeconds(5), 0, Duration.ofSeconds(5), Duration.ofSeconds(5));
    }

    @Test
    void successfulStartClearsCachedStartupFailure() {
        when(instances.findAll()).thenReturn(List.of(record("bot")));
        when(desired.readAllMap()).thenReturn(Map.of("bot", "true"));
        when(leases.acquire(anyString(), anyString(), any())).thenReturn(Optional.of(old));
        cluster.reconcile();
        InOrder ordered = inOrder(manager, cache);
        ordered.verify(manager).startLocal(eq("bot"), any());
        ordered.verify(cache).evict("bot-start-failure-v2:bot");
    }

    @Test
    void reconciliationIsScheduledLocallyForEveryInstance() throws NoSuchMethodException {
        Method method = BotRuntimeReconciliationTask.class.getDeclaredMethod("execute");

        assertThat(method.isAnnotationPresent(Scheduled.class)).isTrue();
        assertThat(method.getAnnotation(Scheduled.class).fixedDelayString())
                .isEqualTo("${nnm.cluster.reconcile-interval:10s}");
        assertThat(BotRuntimeReconciliationTask.class.isAnnotationPresent(RdtScheduled.class)).isFalse();
        assertThat(ClusterBotRuntime.class.getDeclaredMethod("reconcile").isAnnotationPresent(Scheduled.class))
                .isFalse();
        ClusterBotRuntime runtime = mock(ClusterBotRuntime.class);
        BotRuntimeReconciliationTask task = new BotRuntimeReconciliationTask();
        ReflectionTestUtils.setField(task, "runtime", runtime);
        task.execute();
        verify(runtime).reconcile();
    }

    @Test
    void startupFailureIsThrownAndRetryBackoffSurvivesCleanup() {
        when(instances.findAll()).thenReturn(List.of(record("bot")));
        when(desired.readAllMap()).thenReturn(Map.of("bot", "true"));
        when(leases.acquire(anyString(), anyString(), any())).thenReturn(Optional.of(old));
        IllegalStateException failure = new IllegalStateException("gateway startup failed");
        doThrow(failure).when(manager).startLocal(eq("bot"), any());

        assertThatThrownBy(cluster::reconcile).isSameAs(failure);
        cluster.reconcile();

        verify(manager, times(1)).startLocal(eq("bot"), any());
        verify(manager).removeRuntime("bot");
    }

    @Test
    void shutdownFailureIsThrownAndItsConnectionIsRetainedForCleanupRetry() {
        when(instances.findAll()).thenReturn(List.of(record("bot")));
        when(desired.readAllMap()).thenReturn(Map.of("bot", "true"));
        when(leases.acquire(anyString(), anyString(), any())).thenReturn(Optional.of(old));
        when(leases.isHeld(old)).thenReturn(true);
        cluster.reconcile();
        BooleanSupplier oldGuard = cluster.guard(key);
        doThrow(new IllegalStateException("gateway shutdown failed")).when(manager).removeRuntime("bot");

        assertThatThrownBy(cluster::identityLost).hasMessage("gateway shutdown failed");
        assertThat(oldGuard.getAsBoolean()).isFalse();
        verify(leases, never()).release(old);
        doNothing().when(manager).removeRuntime("bot");
        cluster.identityLost();
        verify(leases).release(old);
        assertThat(oldGuard.getAsBoolean()).isFalse();
    }

    @Test
    void nonOwnerDoesNotStartSocketAndCanTakeOverOnLaterReconcile() {
        when(instances.findAll()).thenReturn(List.of(record("bot")));
        when(desired.readAllMap()).thenReturn(Map.of("bot", "true"));
        when(leases.acquire(anyString(), anyString(), any())).thenReturn(Optional.empty(), Optional.of(old));
        cluster.reconcile();
        verify(manager, never()).startLocal(anyString(), any());
        cluster.reconcile();
        verify(manager).startLocal(eq("bot"), any());
    }

    @Test
    void oldGuardNeverAuthorizesAReacquiredConnection() {
        when(instances.findAll()).thenReturn(List.of(record("bot")));
        when(desired.readAllMap()).thenReturn(Map.of("bot", "true"));
        Lease next = new RedisOwnerLeaseService.Lease("account", "next", 2);
        when(leases.acquire(anyString(), anyString(), any())).thenReturn(Optional.of(old), Optional.of(next));
        when(leases.isHeld(old)).thenReturn(true);
        cluster.reconcile();
        BooleanSupplier staleGuard = cluster.guard(key);
        assertThat(staleGuard.getAsBoolean()).isTrue();
        when(leases.renew(eq(old), any())).thenReturn(false);
        when(leases.isHeld(old)).thenReturn(false);
        when(leases.isHeld(next)).thenReturn(true);
        cluster.reconcile();
        verify(manager).removeRuntime("bot");
        assertThat(staleGuard.getAsBoolean()).isFalse();
        assertThat(cluster.guard(key).getAsBoolean()).isTrue();
    }

    @Test
    void renameUnregistersOldRuntimeBeforeStartingReplacement() {
        when(instances.findAll()).thenReturn(List.of(record("old")), List.of(record("new")));
        when(desired.readAllMap()).thenReturn(Map.of("old", "true"), Map.of("new", "true"));
        when(leases.acquire(anyString(), anyString(), any())).thenReturn(Optional.of(old));
        when(leases.renew(eq(old), any())).thenReturn(true);
        cluster.reconcile();
        cluster.reconcile();
        InOrder order = inOrder(manager);
        order.verify(manager).startLocal(eq("old"), any());
        order.verify(manager).removeRuntime("old");
        order.verify(manager).startLocal(eq("new"), any());
    }

    @Test
    void continuouslyDisconnectedComponentIsReleasedAsOneUnit() throws Exception {
        BotChannelRegistry registry = new BotChannelRegistry();
        ReflectionTestUtils.setField(cluster, "registry", registry);
        BotConnectionAffinityResolver resolver = mock(BotConnectionAffinityResolver.class);
        ObjectProvider<BotConnectionAffinityResolver> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(resolver);
        placementResolver = provider;
        ReflectionTestUtils.setField(cluster, "properties", new ClusterProperties(Duration.ofMinutes(1), Duration.ofMinutes(5), Duration.ofSeconds(1),
                Duration.ofSeconds(1), Duration.ofMillis(10), Duration.ofDays(1),
                Duration.ofSeconds(1), Duration.ofMillis(1),
                Duration.ofMillis(1), Duration.ofSeconds(1), 128));
        BotChannelInstanceRecord first = record("first", "account-1");
        BotChannelInstanceRecord second = record("second", "account-2");
        registry.register("first", new BotChannelKey(BotPlatform.ONEBOT, "account-1"),
                BotPlatform.ONEBOT.capabilities(), true);
        registry.register("second", new BotChannelKey(BotPlatform.ONEBOT, "account-2"),
                BotPlatform.ONEBOT.capabilities(), true);
        when(instances.findAll()).thenReturn(List.of(first, second));
        when(desired.readAllMap()).thenReturn(Map.of("first", "true", "second", "true"));
        when(resolver.resolve(anyList())).thenReturn(Map.of("first", "component", "second", "component"));
        when(leases.renew(any(), any())).thenReturn(true);
        Lease affinity = new RedisOwnerLeaseService.Lease("component", "node-a", 1);
        when(leases.acquire(anyString(), anyString(), any())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            if (name.equals("bot-affinity:component")) return Optional.of(affinity);
            if (name.equals("bot:ONEBOT:account-1")) {
                return Optional.of(new RedisOwnerLeaseService.Lease(name, "node-a", 1));
            }
            if (name.equals("bot:ONEBOT:account-2")) {
                return Optional.of(new RedisOwnerLeaseService.Lease(name, "node-a", 1));
            }
            return Optional.empty();
        });

        cluster.reconcile();
        TimeUnit.MILLISECONDS.sleep(5);
        cluster.reconcile();
        TimeUnit.MILLISECONDS.sleep(5);
        cluster.reconcile();

        verify(manager).removeRuntime("first");
        verify(manager).removeRuntime("second");
        verify(leases).release(affinity);
    }

    @Test
    void desiredStateIsNotPublishedUntilTransactionCommits() {
        TransactionSynchronizationManager.initSynchronization();
        try {
            cluster.request("bot", true);
            verifyNoInteractions(desired);
            TransactionSynchronizationManager.getSynchronizations().forEach(sync -> sync.afterCommit());
            verify(desired).put("bot", "true");
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
        reset(desired);
        TransactionSynchronizationManager.initSynchronization();
        try {
            cluster.request("bot", false);
            TransactionSynchronizationManager.getSynchronizations().forEach(sync -> sync.afterCompletion(1));
            verifyNoInteractions(desired);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void affinityLeaseGatesSocketAcquisitionAndItsLossRevokesOldGuard() {
        BotConnectionAffinityResolver resolver = mock(BotConnectionAffinityResolver.class);
        ObjectProvider<BotConnectionAffinityResolver> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(resolver);
        placementResolver = provider;
        when(instances.findAll()).thenReturn(List.of(record("bot")));
        when(desired.readAllMap()).thenReturn(Map.of("bot", "true"));
        when(resolver.resolve(anyList())).thenReturn(Map.of("bot", "placement"));
        Lease affinity = new RedisOwnerLeaseService.Lease("placement", "owner", 1);
        when(leases.acquire(eq("bot-affinity:placement"), anyString(), any()))
                .thenReturn(Optional.empty(), Optional.of(affinity), Optional.empty());
        when(leases.acquire(eq("bot:ONEBOT:account"), anyString(), any())).thenReturn(Optional.of(old));
        cluster.reconcile();
        verify(manager, never()).startLocal(anyString(), any());
        cluster.reconcile();
        verify(manager).startLocal(eq("bot"), any());
        when(leases.isHeld(old)).thenReturn(true);
        when(leases.isHeld(affinity)).thenReturn(true);
        BooleanSupplier guard = cluster.guard(key);
        assertThat(guard.getAsBoolean()).isTrue();
        when(leases.isHeld(affinity)).thenReturn(false);
        when(leases.renew(eq(affinity), any())).thenReturn(false);
        cluster.reconcile();
        verify(manager).removeRuntime("bot");
        assertThat(guard.getAsBoolean()).isFalse();
    }
}
