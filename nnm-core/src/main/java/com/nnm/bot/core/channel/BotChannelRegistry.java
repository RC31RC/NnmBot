package com.nnm.bot.core.channel;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** 统一保存渠道实例身份、能力和易变运行状态。 */
@Component
public class BotChannelRegistry {

    private final ConcurrentHashMap<BotChannelKey, MutableChannel> channels =
            new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BotChannelKey> instanceKeys =
            new ConcurrentHashMap<>();

    public synchronized void register(String instanceId,
                                      BotChannelKey key,
                                      Set<BotChannelCapability> capabilities,
                                      boolean enabled) {
        Assert.hasText(instanceId, "instanceId must not be blank");
        Assert.notNull(key, "key must not be null");
        Set<BotChannelCapability> safeCapabilities = capabilities == null
                ? Set.of()
                : Set.copyOf(capabilities);
        String normalizedInstanceId = instanceId.trim();
        BotChannelKey existingKey = instanceKeys.get(normalizedInstanceId);
        if (existingKey != null && !existingKey.equals(key)) {
            throw new IllegalStateException(
                    "Duplicate bot channel instanceId " + normalizedInstanceId + ": "
                            + existingKey + " / " + key);
        }
        MutableChannel newChannel = new MutableChannel(
                normalizedInstanceId, key, safeCapabilities,
                enabled ? BotChannelStatus.DISCONNECTED : BotChannelStatus.DISABLED);
        MutableChannel previous = channels.putIfAbsent(key, newChannel);
        if (previous != null && !previous.instanceId.equals(newChannel.instanceId)) {
            throw new IllegalStateException(
                    "Duplicate bot channel account " + key + ": "
                            + previous.instanceId + " / " + instanceId);
        }
        instanceKeys.putIfAbsent(normalizedInstanceId, key);
    }

    public synchronized void registerIfAbsent(String instanceId,
                                               BotChannelKey key,
                                               Set<BotChannelCapability> capabilities,
                                               boolean enabled) {
        if (!channels.containsKey(key)) {
            register(instanceId, key, capabilities, enabled);
        }
    }

    public void connecting(BotChannelKey key) {
        update(key, BotChannelStatus.CONNECTING, null);
    }

    public void connected(BotChannelKey key) {
        update(key, BotChannelStatus.CONNECTED, null);
    }

    public void disconnected(BotChannelKey key) {
        update(key, BotChannelStatus.DISCONNECTED, null);
    }

    public void failed(BotChannelKey key, Throwable error) {
        update(key, BotChannelStatus.ERROR,
                error == null ? null : error.getMessage());
    }

    public boolean contains(BotChannelKey key) {
        return channels.containsKey(key);
    }

    public boolean supports(BotChannelKey key, BotChannelCapability capability) {
        Assert.notNull(key, "key must not be null");
        Assert.notNull(capability, "capability must not be null");
        MutableChannel channel = channels.get(key);
        return channel != null && channel.capabilities.contains(capability);
    }

    public Optional<BotChannelSnapshot> find(BotChannelKey key) {
        Assert.notNull(key, "key must not be null");
        MutableChannel channel = channels.get(key);
        return channel == null ? Optional.empty() : Optional.of(channel.snapshot());
    }

    public List<BotChannelSnapshot> snapshots() {
        return channels.values().stream()
                .map(MutableChannel::snapshot)
                .sorted(Comparator.comparing(BotChannelSnapshot::instanceId))
                .toList();
    }

    private void update(BotChannelKey key, BotChannelStatus status, String error) {
        MutableChannel channel = channels.get(key);
        if (channel == null) {
            throw new IllegalStateException("Unknown bot channel: " + key);
        }
        channel.update(status, error);
    }

    private static final class MutableChannel {

        private final String instanceId;
        private final BotChannelKey key;
        private final Set<BotChannelCapability> capabilities;
        private volatile BotChannelStatus status;
        private volatile Instant statusChangedAt = Instant.now();
        private volatile String lastError;

        private MutableChannel(String instanceId,
                               BotChannelKey key,
                               Set<BotChannelCapability> capabilities,
                               BotChannelStatus status) {
            this.instanceId = instanceId;
            this.key = key;
            this.capabilities = capabilities;
            this.status = status;
        }

        private synchronized void update(BotChannelStatus status, String error) {
            if (this.status == BotChannelStatus.DISABLED) {
                return;
            }
            this.status = status;
            this.statusChangedAt = Instant.now();
            if (status == BotChannelStatus.CONNECTED) {
                this.lastError = null;
            } else if (error != null) {
                this.lastError = error;
            }
        }

        private BotChannelSnapshot snapshot() {
            return new BotChannelSnapshot(
                    instanceId, key, capabilities, status, statusChangedAt, lastError);
        }
    }
}
