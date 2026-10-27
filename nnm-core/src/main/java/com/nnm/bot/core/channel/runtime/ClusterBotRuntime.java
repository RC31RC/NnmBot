package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.core.channel.topology.BotTopologySnapshot;
import java.util.Map.Entry;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.topology.BotTopologyStore;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.config.ClusterProperties;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService.Lease;
import io.github.rdt.spring.config.RdtProperties;
import io.github.rdt.spring.lease.NodeIdentityLease;
import io.github.rdt.spring.lease.NodeIdentityListener;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Desired state is shared; only the lease owner creates sockets. Each node reconciles locally. */
@Slf4j
@Component
public class ClusterBotRuntime implements NodeIdentityListener {
    private final ReentrantLock reconciliationLock = new ReentrantLock();

    @Resource
    private RedisOwnerLeaseService leases;
    @Resource
    private NodeIdentityLease identity;
    @Resource
    private ClusterProperties properties;
    @Resource
    private RdtProperties rdt;
    @Resource
    private BotChannelRegistry registry;
    @Resource
    private ObjectProvider<BotChannelRuntimeManager> runtime;
    @Resource
    private ObjectProvider<BotConnectionPlacementCoordinator> placementCoordinator;
    @Resource
    private BotRuntimeStateStore stateStore;

    @Resource
    private BotTopologyStore topology;

    private final Map<String, OwnedConnection> owned = new HashMap<>();
    private final Map<String, Lease> affinities = new HashMap<>();
    private final Map<String, Long> componentStartedAt = new HashMap<>();
    private final Map<String, Long> componentUnhealthySince = new HashMap<>();
    private final Map<String, Long> componentBackoffUntil = new HashMap<>();
    private final Map<String, Long> startRetryUntil = new HashMap<>();
    private final Map<String, PlacementConflict> placementConflicts = new HashMap<>();
    private volatile ReconciliationFailure reconciliationFailure;
    private boolean stopped;

    public void request(String instanceId, boolean running) {
        Runnable write = () -> stateStore.request(instanceId, running);
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

    public Map<String, String> desiredStates() {
        return stateStore.desiredStates();
    }

    public void reconcile() {
        reconciliationLock.lock();
        try {
            if (stopped) return;
            try {
                if (!identity.isHealthy()) {
                    identityLost();
                    ReconciliationFailure failure = new ReconciliationFailure("node-identity",
                            "节点身份租约暂不可用，正在自动恢复，请稍后重试");
                    if (!failure.equals(reconciliationFailure)) log.warn(failure.message());
                    reconciliationFailure = failure;
                    return;
                }
                renewAffinities();
                renewOwnedConnections();

                Map<String, String> desired = stateStore.desiredStates();
                // 本轮协调只持有一个版本，不能把旧实例配置与刷新后的连接分组混用。
                BotTopologySnapshot topologySnapshot = topology.snapshot();
                List<BotChannelInstanceRecord> records = List.copyOf(topologySnapshot.instances().values());
                Map<String, String> placement = topologySnapshot.connectionGroups();
                Map<String, BotChannelInstanceRecord> eligible = new HashMap<>();
                records.stream()
                        .filter(BotChannelInstanceRecord::enabled)
                        .filter(record -> "true".equals(desired.get(record.instanceId())))
                        .forEach(record -> eligible.put(record.instanceId(), record));

                startRetryUntil.keySet().retainAll(eligible.keySet());
                stopRemovedOrDisabled(eligible);
                Map<String, String> targets = placementTargets(records, placement);
                reconcilePlacement(placement, targets);
                reconcileComponentHealth(eligible, placement);
                startEligible(eligible, placement, targets);
                publishStatuses();
                releaseUnusedAffinities();
                if (reconciliationFailure != null) {
                    reconciliationFailure = null;
                    log.info("Bot cluster reconciliation recovered");
                }
            } catch (RuntimeException exception) {
                reconciliationFailure = new ReconciliationFailure(exception.getClass().getName(),
                        exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage());
                try {
                    identityLost();
                } catch (RuntimeException cleanupFailure) {
                    if (cleanupFailure != exception) exception.addSuppressed(cleanupFailure);
                }
                throw exception;
            }
        } finally {
            reconciliationLock.unlock();
        }
    }

    private void renewAffinities() {
        for (Entry<String,Lease> entry : List.copyOf(affinities.entrySet())) {
            if (!leases.renew(entry.getValue(), properties.ownerLease())) {
                stopComponent(entry.getKey());
                affinities.remove(entry.getKey());
                componentUnhealthySince.remove(entry.getKey());
                componentStartedAt.remove(entry.getKey());
            }
        }
    }

    private void renewOwnedConnections() {
        for (Entry<String,OwnedConnection> entry : List.copyOf(owned.entrySet())) {
            if (!leases.renew(entry.getValue().lease(), properties.ownerLease())) {
                stopLocal(entry.getKey());
            }
        }
    }

    private void stopRemovedOrDisabled(Map<String, BotChannelInstanceRecord> eligible) {
        for (Entry<String,OwnedConnection> entry : List.copyOf(owned.entrySet())) {
            BotChannelInstanceRecord expected = eligible.get(entry.getKey());
            if (!entry.getValue().active || expected == null || !entry.getValue().record().equals(expected)) {
                stopLocal(entry.getKey());
            }
        }
    }

    private Map<String, String> placementTargets(List<BotChannelInstanceRecord> records,
                                               Map<String, String> placement) {
        Map<String, String> targets = new HashMap<>();
        BotConnectionPlacementCoordinator coordinator = placementCoordinator == null ? null : placementCoordinator.getIfAvailable();
        Map<String, Lease> currentOwners = leases.findAll(records.stream().map(record -> leaseName(key(record))).toList());
        for (String component : new HashSet<>(placement.values())) {
            List<String> members = BotConnectionPlacementCoordinator.members(component, placement);
            Optional<String> approved = coordinator == null ? Optional.empty()
                    : coordinator.approvedTarget(component, members);
            if (approved.isPresent()) {
                targets.put(component, approved.get());
                continue;
            }
            Set<String> owners = new HashSet<>();
            records.stream().filter(record -> members.contains(record.instanceId()))
                    .map(record -> currentOwners.get(leaseName(key(record)))).filter(Objects::nonNull)
                    .forEach(lease -> owners.add(lease.owner()));
            if (!owners.isEmpty()) targets.put(component, owners.size() == 1 ? owners.iterator().next() : "");
        }
        return targets;
    }

    private void reconcilePlacement(Map<String, String> placement, Map<String, String> targets) {
        Set<String> conflicts = new HashSet<>();
        for (OwnedConnection connection : List.copyOf(owned.values())) {
            String id = connection.record().instanceId();
            String expected = placement.get(id);
            String target = targets.get(expected);
            if (target != null && !target.isEmpty() && !rdt.instanceId().equals(target)) {
                // Only an approved transfer can select a different owner while both nodes are alive.
                stopLocal(id);
                continue;
            }
            if (Objects.equals(expected, connection.affinityName())) continue;
            if ("".equals(target)) {
                conflicts.add(id);
                PlacementConflict conflict = new PlacementConflict(connection.affinityName(), expected);
                if (!conflict.equals(placementConflicts.put(id, conflict))) {
                    log.warn("Bot placement requires administrator confirmation: instance={}, component={}", id, expected);
                }
                continue;
            }
            Lease affinity = expected == null ? null : acquireAffinity(expected);
            if (expected != null && affinity == null) continue;
            // One volatile swap keeps the existing socket and its guard; the account acquisition is unchanged.
            connection.placement = new ConnectionPlacement(expected, affinity);
        }
        placementConflicts.keySet().retainAll(conflicts);
    }

    private Lease acquireAffinity(String name) {
        Lease current = affinities.get(name);
        if (current != null) return current;
        Optional<Lease> acquired = leases.acquire("bot-affinity:" + name, rdt.instanceId(), properties.ownerLease());
        acquired.ifPresent(lease -> {
            affinities.put(name, lease);
            componentStartedAt.putIfAbsent(name, System.currentTimeMillis());
        });
        return acquired.orElse(null);
    }

    private void reconcileComponentHealth(Map<String, BotChannelInstanceRecord> eligible,
                                          Map<String, String> placement) {
        long now = System.currentTimeMillis();
        Map<String, List<OwnedConnection>> localComponents = owned.values().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        connection -> componentName(connection.record(), placement,
                                connection.affinityName())));
        for (Entry<String,List<OwnedConnection>> entry : localComponents.entrySet()) {
            String component = entry.getKey();
            List<BotChannelInstanceRecord> expected = eligible.values().stream()
                    .filter(record -> component.equals(componentName(record, placement,
                            placement.get(record.instanceId()))))
                    .toList();
            // A healthy owner from a previous topology must not be pulled into a new component
            // merely because the database graph changed. The explicit-stop path handles it.
            if (expected.size() != entry.getValue().size()) {
                continue;
            }
            boolean healthy = entry.getValue().stream().anyMatch(connection ->
                    isConnected(connection.record()));
            if (healthy) {
                componentUnhealthySince.remove(component);
                continue;
            }
            long started = componentStartedAt.computeIfAbsent(component, ignored -> now);
            if (now - started < properties.componentStartupGrace().toMillis()) {
                continue;
            }
            long unhealthySince = componentUnhealthySince.computeIfAbsent(component,
                    ignored -> now);
            if (now - unhealthySince < properties.componentFailureThreshold().toMillis()) {
                continue;
            }
            log.warn("All connections in component {} have been unavailable for {} ms; "
                            + "releasing the whole component",
                    component, now - unhealthySince);
            stopComponent(component);
            BotConnectionPlacementCoordinator coordinator = placementCoordinator == null ? null : placementCoordinator.getIfAvailable();
            if (coordinator != null) coordinator.clearApproval(component);
            componentBackoffUntil.put(component,
                    now + properties.componentFailoverBackoff().toMillis());
            componentUnhealthySince.remove(component);
            componentStartedAt.remove(component);
        }
    }

    private void startEligible(Map<String, BotChannelInstanceRecord> eligible,
                               Map<String, String> placement, Map<String, String> targets) {
        long now = System.currentTimeMillis();
        for (BotChannelInstanceRecord record : eligible.values().stream()
                .sorted(Comparator.comparing(BotChannelInstanceRecord::instanceId)).toList()) {
            if (owned.containsKey(record.instanceId())) {
                if (startRetryUntil.containsKey(record.instanceId())
                        && now >= startRetryUntil.get(record.instanceId())) {
                    retryStart(record, now);
                }
                continue;
            }
            if (now < startRetryUntil.getOrDefault(record.instanceId(), 0L)) {
                continue;
            }
            String affinityName = placement.get(record.instanceId());
            if (affinityName != null
                    && now < componentBackoffUntil.getOrDefault(affinityName, 0L)) {
                continue;
            }
            String target = targets.get(affinityName);
            if (target != null && !rdt.instanceId().equals(target)) continue;
            Lease affinity = affinityName == null ? null : acquireAffinity(affinityName);
            if (affinityName != null && affinity == null) continue;
            Optional<Lease> lease = leases.acquire(leaseName(key(record)), rdt.instanceId(), properties.ownerLease());
            if (lease.isEmpty()) continue;
            OwnedConnection connection = new OwnedConnection(record, lease.get(), affinityName,
                    affinity);
            owned.put(record.instanceId(), connection);
            retryStart(record, now);
        }
    }

    private void retryStart(BotChannelInstanceRecord record, long now) {
        try {
            runtime.getObject().startLocal(record.instanceId(), key(record));
            stateStore.clearStartFailure(record.instanceId());
            startRetryUntil.remove(record.instanceId());
            if (placementName(record) != null) {
                componentStartedAt.putIfAbsent(placementName(record), now);
            }
        } catch (RuntimeException exception) {
            OwnedConnection connection = owned.get(record.instanceId());
            if (connection != null) registry.find(key(record)).ifPresent(snapshot ->
                    stateStore.startFailed(record.instanceId(), connection.lease(), connection.affinity(), snapshot));
            startRetryUntil.put(record.instanceId(),
                    now + properties.componentFailoverBackoff().toMillis());
            throw exception;
        }
    }

    private String placementName(BotChannelInstanceRecord record) {
        OwnedConnection connection = owned.get(record.instanceId());
        return connection == null ? null : connection.affinityName();
    }

    private void publishStatuses() {
        Map<BotChannelKey, SharedStatus> statuses = new HashMap<>();
        owned.values().forEach(connection -> registry.find(key(connection.record())).ifPresent(snapshot ->
                statuses.put(key(connection.record()), new SharedStatus(connection.lease(), connection.affinity(), snapshot))));
        stateStore.publish(statuses);
    }

    private boolean isConnected(BotChannelInstanceRecord record) {
        return registry.find(key(record))
                .map(snapshot -> snapshot.status()
                        == com.nnm.bot.core.constant.BotChannelStatus.CONNECTED)
                .orElse(false);
    }

    private String componentName(BotChannelInstanceRecord record,
                                 Map<String, String> placement,
                                 String fallback) {
        return placement.getOrDefault(record.instanceId(), fallback == null
                ? record.instanceId() : fallback);
    }

    public BooleanSupplier guard(BotChannelKey key) {
        reconciliationLock.lock();
        try {
            OwnedConnection connection = owned.values().stream()
                    .filter(value -> key(value.record()).equals(key))
                    .findFirst().orElse(null);
            BooleanSupplier nodeOwnership = identity.guard();
            return () -> {
                if (connection == null || !connection.active || !nodeOwnership.getAsBoolean()) return false;
                ConnectionPlacement placement = connection.placement;
                return leases.isHeld(connection.lease())
                        && (placement.lease() == null || leases.isHeld(placement.lease()));
            };
        } finally {
            reconciliationLock.unlock();
        }
    }

    public Optional<BotChannelSnapshot> snapshot(BotChannelKey key) {
        return Optional.ofNullable(stateStore.snapshots(List.of(key)).get(key));
    }

    public BotRuntimeStateStore.State states(List<BotChannelInstanceMetadata> instances) {
        return stateStore.read(instances);
    }

    public Map<BotChannelKey, BotChannelSnapshot> snapshots(List<BotChannelKey> keys) {
        return stateStore.snapshots(keys);
    }

    public Map<String, BotChannelSnapshot> startFailures(List<String> ids) {
        return stateStore.startFailures(ids);
    }

    public Optional<String> reconciliationFailure() {
        ReconciliationFailure failure = reconciliationFailure;
        return failure == null ? Optional.empty() : Optional.of(failure.message());
    }

    public Optional<BotChannelSnapshot> startFailure(String instanceId) {
        return stateStore.startFailure(instanceId);
    }

    public boolean isLocalOwner(BotChannelKey key) {
        return guard(key).getAsBoolean();
    }

    private void stopComponent(String component) {
        for (OwnedConnection connection : List.copyOf(owned.values())) {
            String current = connection.affinityName() == null
                    ? connection.record().instanceId() : connection.affinityName();
            if (component.equals(current)) {
                stopLocal(connection.record().instanceId());
            }
        }
    }

    private void stopLocal(String id) {
        placementConflicts.remove(id);
        OwnedConnection connection = owned.get(id);
        if (connection != null) connection.active = false;
        runtime.getObject().removeRuntime(id);
        if (connection != null) leases.release(connection.lease());
        owned.remove(id);
    }

    @Override
    public void identityLost() {
        reconciliationLock.lock();
        try {
            // Revoke every callback first, even if one connector fails during cleanup.
            owned.values().forEach(connection -> connection.active = false);
            for (String id : List.copyOf(owned.keySet())) stopLocal(id);
            releaseUnusedAffinities();
            componentUnhealthySince.clear();
        } finally {
            reconciliationLock.unlock();
        }
    }

    private void releaseUnusedAffinities() {
        Set<String> activeComponents = new HashSet<>();
        owned.values().forEach(connection -> {
            if (connection.affinityName() != null) {
                activeComponents.add(connection.affinityName());
            }
        });
        for (Entry<String,Lease> entry : List.copyOf(affinities.entrySet())) {
            if (activeComponents.contains(entry.getKey())) continue;
            leases.release(entry.getValue());
            affinities.remove(entry.getKey());
            componentStartedAt.remove(entry.getKey());
        }
    }

    @PreDestroy
    public void close() {
        reconciliationLock.lock();
        try {
            stopped = true;
            identityLost();
        } finally {
            reconciliationLock.unlock();
        }
    }

    private static BotChannelKey key(BotChannelInstanceRecord record) {
        return new BotChannelKey(record.platform(), record.botAccountId());
    }

    private static String leaseName(BotChannelKey key) {
        return "bot:" + key.platform() + ":" + key.botAccountId();
    }

    private static String statusKey(BotChannelKey key) {
        return "bot-status:" + key.platform() + ":" + key.botAccountId();
    }

    private static String startFailureKey(String instanceId) {
        return "bot-start-failure:" + instanceId;
    }

    private record PlacementConflict(String current, String expected) {
    }

    private record ReconciliationFailure(String type, String message) {
    }

    private record ConnectionPlacement(String name, Lease lease) {
    }

    private static final class OwnedConnection {
        private final BotChannelInstanceRecord record;
        private final Lease lease;
        private volatile ConnectionPlacement placement;
        private volatile boolean active = true;

        private OwnedConnection(BotChannelInstanceRecord record, Lease lease, String affinityName,
                                Lease affinity) {
            this.record = record;
            this.lease = lease;
            this.placement = new ConnectionPlacement(affinityName, affinity);
        }

        private BotChannelInstanceRecord record() { return record; }
        private Lease lease() { return lease; }
        private String affinityName() { return placement.name(); }
        private Lease affinity() { return placement.lease(); }
    }

    public record SharedStatus(Lease lease, Lease affinity, BotChannelSnapshot snapshot) {
    }
}
