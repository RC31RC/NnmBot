package com.nnm.bot.admin.service.bot.channel.impl;

import com.nnm.bot.core.channel.runtime.BotRuntimeStateStore.State;
import com.nnm.bot.admin.service.bot.channel.BotChannelStatusService;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.topology.BotTopologyStore;
import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.constant.BotChannelStatus;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class BotChannelStatusServiceImpl implements BotChannelStatusService {
    @Resource
    private BotChannelRegistry registry;
    @Resource
    private ObjectProvider<ClusterBotRuntime> clusterProvider;
    @Resource
    private BotTopologyStore topology;
    private final Map<String, FallbackStatus> fallbackStatuses = new ConcurrentHashMap<>();

    @Override
    public boolean isDistributed() {
        return clusterProvider.getIfAvailable() != null;
    }

    @Override
    public List<BotChannelSnapshot> snapshots() {
        List<BotChannelInstanceMetadata> instances = topology.snapshot().metadata().values().stream()
                .sorted(Comparator.comparing(BotChannelInstanceMetadata::instanceId)).toList();
        Set<String> instanceIds = instances.stream().map(BotChannelInstanceMetadata::instanceId).collect(Collectors.toSet());
        fallbackStatuses.keySet().removeIf(instanceId -> !instanceIds.contains(instanceId));

        ClusterBotRuntime cluster = clusterProvider.getIfAvailable();
        State state = cluster == null ? null : cluster.states(instances);
        Map<String, String> desiredStates = state == null ? Map.of() : state.desiredStates();
        Map<BotChannelKey, BotChannelSnapshot> shared = state == null ? Map.of() : state.snapshots();
        Map<String, BotChannelSnapshot> failures = state == null ? Map.of() : state.startFailures();
        return instances.stream().map(instance -> {
            BotChannelKey key = new BotChannelKey(instance.platform(), instance.botAccountId());
            if (!instance.enabled()) return fallback(instance, key, BotChannelStatus.DISABLED, null);
            BotChannelSnapshot snapshot = cluster == null ? registry.find(key).orElse(null) : shared.get(key);
            // 停止意图高于残留失败；健康状态仍以有效租约对应的运行快照为准。
            if ("false".equals(desiredStates.get(instance.instanceId()))) {
                return fallback(instance, key, snapshot != null && isActive(snapshot.status())
                        ? BotChannelStatus.STOPPING : BotChannelStatus.STOPPED, null);
            }
            if (snapshot != null) {
                fallbackStatuses.remove(instance.instanceId());
                return snapshot;
            }
            BotChannelSnapshot failureSnapshot = failures.get(instance.instanceId());
            if (failureSnapshot != null && "true".equals(desiredStates.get(instance.instanceId()))) return failureSnapshot;
            if ("true".equals(desiredStates.get(instance.instanceId()))) {
                // 有效共享快照优先于本节点协调错误，避免其他节点正常连接时误报失败。
                String failure = cluster == null ? null
                        : cluster.reconciliationFailure().orElse(null);
                return fallback(instance, key, failure == null
                        ? BotChannelStatus.CONNECTING : BotChannelStatus.ERROR, failure);
            }
            return fallback(instance, key, BotChannelStatus.STOPPED, null);
        }).toList();
    }

    private BotChannelSnapshot fallback(
            BotChannelInstanceMetadata instance,
            BotChannelKey key,
            BotChannelStatus status,
            String lastError) {
        FallbackStatus fallback = fallbackStatuses.compute(
                instance.instanceId(),
                        (ignored, previous) -> previous != null
                                && previous.status() == status
                        && Objects.equals(previous.lastError(), lastError)
                        ? previous
                        : new FallbackStatus(status, Instant.now(), lastError));
        return new BotChannelSnapshot(
                instance.instanceId(),
                key,
                instance.platform().capabilities(),
                fallback.status(),
                fallback.statusChangedAt(),
                fallback.lastError());
    }

    private boolean isActive(BotChannelStatus status) {
        return status != BotChannelStatus.DISABLED
                && status != BotChannelStatus.STOPPED
                && status != BotChannelStatus.START_FAILED
                && status != BotChannelStatus.STOPPING;
    }

    private record FallbackStatus(
            BotChannelStatus status,
            Instant statusChangedAt,
            String lastError) {
    }
}
