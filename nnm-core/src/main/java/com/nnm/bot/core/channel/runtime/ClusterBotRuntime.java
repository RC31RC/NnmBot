package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.config.DistributedProperties;
import io.github.rdt.cache.CacheEntry;
import io.github.rdt.cache.CacheService;
import io.github.rdt.lease.RedisOwnerLeaseService;
import io.github.rdt.lease.RedisOwnerLeaseService.Lease;
import io.github.rdt.spring.config.RdtProperties;
import io.github.rdt.spring.lease.NodeIdentityLease;
import io.github.rdt.support.KeySpace;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Desired state is shared; only the lease owner creates sockets. Each node reconciles locally. */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "nnm.distributed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ClusterBotRuntime {
    @Resource
    private RedisOwnerLeaseService leases;
    @Resource
    private NodeIdentityLease identity;
    @Resource
    private DistributedProperties properties;
    @Resource
    private RdtProperties rdt;
    @Resource
    private RedissonClient redis;
    @Resource
    private KeySpace keys;
    @Resource
    private CacheService cache;
    @Resource
    private BotChannelInstanceProvider instances;
    @Resource
    private BotChannelRegistry registry;
    @Resource
    private ObjectProvider<BotChannelRuntimeManager> runtime;
    private final Map<String, OwnedConnection> owned = new HashMap<>();
    private boolean stopped;

    public void request(String instanceId, boolean running) {
        Runnable write = () -> redis.<String, String>getMap(keys.key("bot", "desired"), StringCodec.INSTANCE)
                .put(instanceId, Boolean.toString(running));
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    write.run();
                }
            });
        } else {
            write.run();
        }
    }

    /**
     * 当前实例的连接协调入口。
     *
     * <p>连接对象只存在于当前 JVM，因此每个实例都必须执行本地协调。
     * Redis owner lease 负责集群归属竞争，不能把本地协调本身改成只由一个实例执行的
     * RDT 集群任务，否则其他实例无法续租和管理自己持有的 WebSocket。</p>
     */
    @Scheduled(fixedDelayString = "${nnm.distributed.reconcile-interval:10s}")
    public synchronized void reconcile() {
        if (stopped) return;
        try {
            identity.requireHealthy();
            // Stop unsafe connections before querying the database or attempting new acquisitions.
            for (var entry : List.copyOf(owned.entrySet())) {
                if (!leases.renew(entry.getValue().lease(), properties.ownerLease())) {
                    stopLocal(entry.getKey());
                }
            }
            Map<String, String> desired = redis.<String, String>getMap(
                    keys.key("bot", "desired"), StringCodec.INSTANCE).readAllMap();
            List<BotChannelInstanceRecord> records = instances.findAll();
            Map<String, BotChannelInstanceRecord> eligible = new HashMap<>();
            records.stream().filter(BotChannelInstanceRecord::enabled)
                    .filter(record -> "true".equals(desired.get(record.instanceId())))
                    .forEach(record -> eligible.put(record.instanceId(), record));
            for (var entry : List.copyOf(owned.entrySet())) {
                if (!entry.getValue().record().equals(eligible.get(entry.getKey()))) stopLocal(entry.getKey());
            }
            for (BotChannelInstanceRecord record : eligible.values()) {
                if (!owned.containsKey(record.instanceId())) {
                    var lease = leases.acquire(leaseName(key(record)), rdt.instanceId(), properties.ownerLease());
                    if (lease.isEmpty()) continue;
                    owned.put(record.instanceId(), new OwnedConnection(record, lease.get()));
                    try {
                        runtime.getObject().startLocal(record.instanceId());
                    } catch (RuntimeException exception) {
                        stopLocal(record.instanceId());
                        log.warn("Bot owner failed to start {}", record.instanceId(), exception);
                        continue;
                    }
                }
                OwnedConnection connection = owned.get(record.instanceId());
                registry.find(key(record)).ifPresent(snapshot -> cache.put(
                        statusKey(key(record)), new SharedStatus(connection.lease(), snapshot), properties.ownerLease()));
            }
        } catch (RuntimeException exception) {
            // Redis/DB uncertainty is fail-closed, not permission to continue sending.
            for (String id : List.copyOf(owned.keySet())) stopLocal(id);
            log.warn("Bot cluster reconciliation failed; local connections stopped", exception);
        }
    }

    public synchronized BooleanSupplier guard(BotChannelKey key) {
        Lease lease = owned.values().stream().filter(value -> key(value.record()).equals(key))
                .map(OwnedConnection::lease).findFirst().orElse(null);
        // Capture this acquisition, not a lookup that could authorize an old socket after reacquiring.
        return () -> {
            if (lease == null) return false;
            try {
                identity.requireHealthy();
                return leases.isHeld(lease);
            }
            catch (RuntimeException exception) { return false; }
        };
    }

    public Optional<BotChannelSnapshot> snapshot(BotChannelKey key) {
        CacheEntry<SharedStatus> entry = cache.get(statusKey(key), SharedStatus.class);
        if (entry instanceof CacheEntry.Hit<SharedStatus> hit && hit.value() != null
                && leases.isHeld(hit.value().lease())) {
            return Optional.of(hit.value().snapshot());
        }
        return Optional.empty();
    }

    public boolean isLocalOwner(BotChannelKey key) {
        return guard(key).getAsBoolean();
    }

    private void stopLocal(String id) {
        OwnedConnection connection = owned.remove(id);
        try {
            runtime.getObject().removeRuntime(id);
        } catch (RuntimeException exception) {
            log.warn("Bot local shutdown failed: {}", id, exception);
        } finally {
            if (connection != null) {
                try { leases.release(connection.lease()); }
                catch (RuntimeException exception) { log.debug("Lease will expire: {}", id, exception); }
            }
        }
    }

    @PreDestroy
    public synchronized void close() {
        stopped = true;
        for (String id : List.copyOf(owned.keySet())) stopLocal(id);
    }

    private static BotChannelKey key(BotChannelInstanceRecord record) {
        return new BotChannelKey(record.platform(), record.botAccountId());
    }
    private static String leaseName(BotChannelKey key) { return "bot:" + key.platform() + ":" + key.botAccountId(); }
    private static String statusKey(BotChannelKey key) { return "bot-status:" + key.platform() + ":" + key.botAccountId(); }
    private record OwnedConnection(BotChannelInstanceRecord record, Lease lease) { }
    public record SharedStatus(Lease lease, BotChannelSnapshot snapshot) { }
}
