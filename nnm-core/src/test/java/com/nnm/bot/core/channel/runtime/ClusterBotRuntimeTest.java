package com.nnm.bot.core.channel.runtime;

import org.redisson.client.codec.Codec;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.config.DistributedProperties;
import io.github.rdt.spring.lease.NodeIdentityLease;
import io.github.rdt.lease.RedisOwnerLeaseService;
import io.github.rdt.cache.CacheService;
import io.github.rdt.spring.config.RdtProperties;
import io.github.rdt.support.KeySpace;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import io.github.rdt.spring.annotation.RdtScheduled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClusterBotRuntimeTest {
    private final ClusterBotRuntime cluster = new ClusterBotRuntime();
    private final RedisOwnerLeaseService leases = mock(RedisOwnerLeaseService.class);
    private final BotChannelRuntimeManager manager = mock(BotChannelRuntimeManager.class);
    private final BotChannelInstanceProvider instances = mock(BotChannelInstanceProvider.class);
    private final RMap<String, String> desired = mock(RMap.class);
    private final RedisOwnerLeaseService.Lease old = new RedisOwnerLeaseService.Lease("account", "old", 1);
    private final BotChannelKey key = new BotChannelKey(BotPlatform.ONEBOT, "account");

    @BeforeEach
    void setup() {
        RedissonClient redis = mock(RedissonClient.class);
        when(redis.<String, String>getMap(anyString(), any(Codec.class))).thenReturn(desired);
        RdtProperties rdt = mock(RdtProperties.class);
        when(rdt.instanceId()).thenReturn("node-a");
        ObjectProvider<BotChannelRuntimeManager> runtime = mock(ObjectProvider.class);
        when(runtime.getObject()).thenReturn(manager);
        ReflectionTestUtils.setField(cluster, "redis", redis);
        ReflectionTestUtils.setField(cluster, "rdt", rdt);
        ReflectionTestUtils.setField(cluster, "runtime", runtime);
        ReflectionTestUtils.setField(cluster, "leases", leases);
        ReflectionTestUtils.setField(cluster, "instances", instances);
        ReflectionTestUtils.setField(cluster, "identity", mock(NodeIdentityLease.class));
        ReflectionTestUtils.setField(cluster, "registry", new BotChannelRegistry());
        ReflectionTestUtils.setField(cluster, "cache", mock(CacheService.class));
        ReflectionTestUtils.setField(cluster, "keys", new KeySpace("test"));
        ReflectionTestUtils.setField(cluster, "properties", new DistributedProperties(true,
                Duration.ofMinutes(1), Duration.ofMinutes(5), Duration.ofSeconds(1),
                Duration.ofSeconds(30), Duration.ofSeconds(5), Duration.ofDays(1),
                Duration.ofDays(2), Duration.ofSeconds(60), 128));
    }

    private BotChannelInstanceRecord record(String id) {
        return new BotChannelInstanceRecord(BotPlatform.ONEBOT, id, "account", true,
                "ws://localhost:3001", "", null, null, null, null, null,
                Duration.ofSeconds(5), 0, Duration.ofSeconds(5), Duration.ofSeconds(5));
    }

    @Test
    void reconciliationIsScheduledLocallyForEveryInstance() throws NoSuchMethodException {
        var method = ClusterBotRuntime.class.getDeclaredMethod("reconcile");

        assertThat(method.isAnnotationPresent(Scheduled.class)).isTrue();
        assertThat(method.isAnnotationPresent(RdtScheduled.class)).isFalse();
    }

    @Test
    void nonOwnerDoesNotStartSocketAndCanTakeOverOnLaterReconcile() {
        when(instances.findAll()).thenReturn(List.of(record("bot")));
        when(desired.readAllMap()).thenReturn(Map.of("bot", "true"));
        when(leases.acquire(anyString(), anyString(), any())).thenReturn(Optional.empty(), Optional.of(old));
        cluster.reconcile();
        verify(manager, never()).startLocal(anyString());
        cluster.reconcile();
        verify(manager).startLocal("bot");
    }

    @Test
    void oldGuardNeverAuthorizesAReacquiredConnection() {
        when(instances.findAll()).thenReturn(List.of(record("bot")));
        when(desired.readAllMap()).thenReturn(Map.of("bot", "true"));
        var next = new RedisOwnerLeaseService.Lease("account", "next", 2);
        when(leases.acquire(anyString(), anyString(), any())).thenReturn(Optional.of(old), Optional.of(next));
        when(leases.isHeld(old)).thenReturn(true);
        cluster.reconcile();
        var staleGuard = cluster.guard(key);
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
        var order = inOrder(manager);
        order.verify(manager).startLocal("old");
        order.verify(manager).removeRuntime("old");
        order.verify(manager).startLocal("new");
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
}
