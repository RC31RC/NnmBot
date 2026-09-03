package com.nnm.bot.core.channel.registry;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotChannelStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** 统一保存渠道实例身份、能力和易变运行状态。 */
@Component
public class BotChannelRegistry {

    private final ConcurrentHashMap<BotChannelKey, MutableChannel> channels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BotChannelKey> instanceKeys = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<Consumer<BotChannelSnapshot>> listeners =
            new CopyOnWriteArrayList<>();

    public synchronized void register(String instanceId,
                                      BotChannelKey key,
                                      Set<BotChannelCapability> capabilities,
                                      boolean enabled) {
        Assert.hasText(instanceId, "instanceId must not be blank");
        Assert.notNull(key, "key must not be null");
        Set<BotChannelCapability> safeCapabilities = CollUtil.isEmpty(capabilities) ? Set.of() : Set.copyOf(capabilities);
        String normalizedInstanceId = instanceId.trim();
        BotChannelKey existingKey = instanceKeys.get(normalizedInstanceId);
        if (ObjectUtil.isNotNull(existingKey) && !existingKey.equals(key)) {
            throw new BusinessException(
                    BotChannelErrorCode.CHANNEL_REGISTRATION_CONFLICT,
                    "机器人实例标识已注册到其他渠道: " + normalizedInstanceId);
        }
        MutableChannel newChannel = new MutableChannel(
                normalizedInstanceId, key, safeCapabilities,
                enabled ? BotChannelStatus.DISCONNECTED : BotChannelStatus.DISABLED);
        MutableChannel previous = channels.putIfAbsent(key, newChannel);
        if (ObjectUtil.isNotNull(previous) && !previous.instanceId.equals(newChannel.instanceId)) {
            throw new BusinessException(
                    BotChannelErrorCode.CHANNEL_REGISTRATION_CONFLICT,
                    "机器人账号已注册到其他实例: " + key);
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

    public void stopped(String instanceId) {
        BotChannelKey key = instanceKeys.get(normalizeInstanceId(instanceId));
        if (ObjectUtil.isNotNull(key)) {
            update(key, BotChannelStatus.STOPPED, null);
        }
    }

    public void failed(BotChannelKey key, Throwable error) {
        update(key, BotChannelStatus.ERROR,
                ObjectUtil.isNull(error) ? null : error.getMessage());
    }

    public synchronized void unregister(String instanceId) {
        BotChannelKey key = instanceKeys.remove(normalizeInstanceId(instanceId));
        if (ObjectUtil.isNotNull(key)) {
            channels.remove(key);
        }
    }

    public boolean contains(BotChannelKey key) {
        return channels.containsKey(key);
    }

    public boolean supports(BotChannelKey key, BotChannelCapability capability) {
        Assert.notNull(key, "key must not be null");
        Assert.notNull(capability, "capability must not be null");
        MutableChannel channel = channels.get(key);
        return ObjectUtil.isNotNull(channel) && channel.capabilities.contains(capability);
    }

    public Optional<BotChannelSnapshot> find(BotChannelKey key) {
        Assert.notNull(key, "key must not be null");
        MutableChannel channel = channels.get(key);
        return ObjectUtil.isNull(channel) ? Optional.empty() : Optional.of(channel.snapshot());
    }

    public List<BotChannelSnapshot> snapshots() {
        return channels.values().stream()
                .map(MutableChannel::snapshot)
                .sorted(Comparator.comparing(BotChannelSnapshot::instanceId))
                .toList();
    }

    /**
     * 订阅运行状态变化。订阅者只收到实际状态或错误信息变化，避免重连调度造成
     * 大量重复事件；连接关闭后由订阅者显式释放。
     */
    public Subscription subscribe(Consumer<BotChannelSnapshot> listener) {
        Assert.notNull(listener, "listener must not be null");
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    private void update(BotChannelKey key, BotChannelStatus status, String error) {
        MutableChannel channel = channels.get(key);
        if (ObjectUtil.isNull(channel)) {
            return;
        }
        BotChannelSnapshot snapshot = channel.update(status, error);
        if (ObjectUtil.isNull(snapshot)) {
            return;
        }
        listeners.forEach(listener -> {
            try {
                listener.accept(snapshot);
            } catch (RuntimeException ignored) {
                // 单个管理端订阅异常不能影响机器人连接线程的状态登记。
            }
        });
    }

    private static String normalizeInstanceId(String instanceId) {
        Assert.hasText(instanceId, "instanceId must not be blank");
        return instanceId.trim();
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

        private synchronized BotChannelSnapshot update(
                BotChannelStatus status, String error) {
            if (this.status == BotChannelStatus.DISABLED) {
                return null;
            }
            if (this.status == status && java.util.Objects.equals(this.lastError, error)) {
                return null;
            }
            this.status = status;
            this.statusChangedAt = Instant.now();
            if (status == BotChannelStatus.CONNECTED) {
                this.lastError = null;
            } else if (status == BotChannelStatus.STOPPED) {
                this.lastError = null;
            } else if (ObjectUtil.isNotNull(error)) {
                this.lastError = error;
            }
            return snapshot();
        }

        private BotChannelSnapshot snapshot() {
            return new BotChannelSnapshot(
                    instanceId, key, capabilities, status, statusChangedAt, lastError);
        }
    }

    @FunctionalInterface
    public interface Subscription {

        void close();
    }
}
