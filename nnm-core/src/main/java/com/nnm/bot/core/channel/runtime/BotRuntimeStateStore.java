package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime.SharedStatus;
import com.nnm.bot.core.config.ClusterProperties;
import io.github.rdt.state.SharedStateStore;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService.Lease;
import io.github.rdt.support.KeySpace;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

/** Redis 运行状态的统一入口；展示快照可批量读取，发送授权仍实时校验租约。 */
@Component
public class BotRuntimeStateStore {
    @Resource
    private SharedStateStore cache;
    @Resource
    private RedisOwnerLeaseService leases;
    @Resource(name = "rdtRuntimeRedis")
    private RedissonClient redis;
    @Resource
    private KeySpace keys;
    @Resource
    private ClusterProperties properties;

    public void request(String instanceId, boolean running) {
        clearStartFailure(instanceId);
        redis.<String, String>getMap(keys.key("bot", "desired"), StringCodec.INSTANCE)
                .put(instanceId, Boolean.toString(running));
    }

    public Map<String, String> desiredStates() {
        return Map.copyOf(redis.<String, String>getMap(keys.key("bot", "desired"), StringCodec.INSTANCE).readAllMap());
    }

    public void publish(Map<BotChannelKey,SharedStatus> statuses) {
        Map<String,SharedStatus> values = new HashMap<>();
        statuses.forEach((key, status) -> values.put(statusKey(key), status));
        if (!values.isEmpty()) cache.putAll(values, properties.ownerLease());
    }

    public State read(List<BotChannelInstanceMetadata> instances) {
        Map<BotChannelKey, String> requested = new HashMap<>();
        instances.forEach(instance -> requested.put(new BotChannelKey(instance.platform(), instance.botAccountId()), instance.instanceId()));
        return read(requested, desiredStates());
    }

    private State read(Map<BotChannelKey, String> requested, Map<String, String> desired) {
        if (requested.isEmpty()) return new State(desired, Map.of(), Map.of());
        Map<String,SharedStatus> statuses = cache.getAll(
                requested.keySet().stream().map(this::statusKey).toList(), ClusterBotRuntime.SharedStatus.class);
        List<String> ids = requested.values().stream().filter(id -> id != null).toList();
        Map<String, StartFailure> errors = ids.isEmpty() ? Map.of()
                : cache.getAll(ids.stream().map(this::failureKey).toList(), StartFailure.class);
        List<Lease> expected = Stream.concat(statuses.values().stream().flatMap(entry -> {
            SharedStatus status = entry;
            return status == null ? Stream.empty() : Stream.of(status.lease(), status.affinity());
        }), errors.values().stream().flatMap(entry -> {
            StartFailure failure = entry;
            return failure == null ? Stream.empty() : Stream.of(failure.lease(), failure.affinity());
        })).filter(lease -> lease != null).toList();
        // 同一轮状态展示共用一批租约代次，避免逐实例查询以及旧 owner 的错误覆盖接管后的连接。
        Map<String, Lease> current = expected.isEmpty() ? Map.of()
                : leases.findAll(expected.stream().map(Lease::name).distinct().toList());
        Map<BotChannelKey, BotChannelSnapshot> snapshots = new HashMap<>();
        Map<String, BotChannelSnapshot> failures = new HashMap<>();
        requested.forEach((key, id) -> {
            SharedStatus status = statuses.get(statusKey(key));
            if (status != null && valid(status.lease(), status.affinity(), current)) snapshots.put(key, status.snapshot());
            StartFailure failure = id == null ? null : errors.get(failureKey(id));
            if (failure != null && validFailure(failure, current)) failures.put(id, failure.snapshot());
        });
        return new State(desired, snapshots, failures);
    }

    public Map<BotChannelKey, BotChannelSnapshot> snapshots(Collection<BotChannelKey> requested) {
        Map<BotChannelKey, String> keys = new HashMap<>();
        requested.forEach(key -> keys.put(key, null));
        return read(keys, Map.of()).snapshots();
    }

    public void startFailed(String id, Lease lease, Lease affinity, BotChannelSnapshot snapshot) {
        cache.put(failureKey(id), new StartFailure(lease, affinity, snapshot), properties.taskRetention());
    }

    public void clearStartFailure(String id) {
        cache.evict(failureKey(id));
    }

    public Optional<BotChannelSnapshot> startFailure(String id) {
        return Optional.ofNullable(startFailures(List.of(id)).get(id));
    }

    public Map<String, BotChannelSnapshot> startFailures(Collection<String> ids) {
        if (ids.isEmpty()) return Map.of();
        Map<String, StartFailure> values = cache.getAll(ids.stream().map(this::failureKey).toList(), StartFailure.class);
        List<Lease> expected = values.values().stream().filter(failure -> failure != null)
                .flatMap(failure -> Stream.of(failure.lease(), failure.affinity())).filter(lease -> lease != null).toList();
        Map<String, Lease> current = expected.isEmpty() ? Map.of()
                : leases.findAll(expected.stream().map(Lease::name).distinct().toList());
        Map<String, BotChannelSnapshot> failures = new HashMap<>();
        ids.forEach(id -> {
            StartFailure failure = values.get(failureKey(id));
            if (failure != null && validFailure(failure, current)) failures.put(id, failure.snapshot());
        });
        return Map.copyOf(failures);
    }

    private boolean validFailure(StartFailure failure, Map<String, Lease> current) {
        if (failure.lease() == null) return false;
        Lease owner = current.get(failure.lease().name());
        Lease affinity = failure.affinity() == null ? null : current.get(failure.affinity().name());
        // 启动失败后租约会释放，仍保留失败原因；一旦出现其他代次的 owner 就不再采用旧错误。
        return (owner == null || owner.equals(failure.lease()))
                && (affinity == null || affinity.equals(failure.affinity()));
    }

    private boolean valid(Lease lease, Lease affinity, Map<String, Lease> current) {
        return lease != null && lease.equals(current.get(lease.name()))
                && (affinity == null || affinity.equals(current.get(affinity.name())));
    }

    public record StartFailure(Lease lease, Lease affinity, BotChannelSnapshot snapshot) {
    }

    public record State(Map<String, String> desiredStates, Map<BotChannelKey, BotChannelSnapshot> snapshots,
                        Map<String, BotChannelSnapshot> startFailures) {
        public State {
            desiredStates = Map.copyOf(desiredStates);
            snapshots = Map.copyOf(snapshots);
            startFailures = Map.copyOf(startFailures);
        }
    }

    private String statusKey(BotChannelKey key) { return "bot-status:" + key.platform() + ":" + key.botAccountId(); }
    private String failureKey(String id) { return "bot-start-failure-v2:" + id; }
}
