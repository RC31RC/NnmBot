package com.nnm.bot.warframe.cache;

import java.util.concurrent.Callable;
import com.nnm.bot.warframe.application.WorldStateRuntimeSnapshot;
import com.nnm.bot.core.config.DistributedProperties;
import com.nnm.bot.warframe.messaging.producer.WarframeWorldStateUpdatedMessageProducer;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.RdtLock;
import jakarta.annotation.Resource;
import io.github.rdt.cache.CacheEntry;
import io.github.rdt.cache.CacheService;
import java.util.Objects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 维护集群共享的最新世界状态快照。
 *
 * <p>Redis 保存当前快照，广播消息只负责唤醒各实例刷新本地缓存，不承担历史快照重放。</p>
 */
@Component
@ConditionalOnProperty(prefix = "nnm.distributed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SharedWorldStateStore {
    @Resource
    private CacheService cache;
    @Resource
    private DistributedProperties properties;
    @Resource
    private DistributedLockManager lockManager;
    @Resource
    private ObjectProvider<WarframeWorldStateUpdatedMessageProducer> updateProducer;

    @RdtLock
    public <T> T refresh(Callable<T> action) {
        lockManager.lock("worldstate-refresh-v1");
        try {
            return action.call();
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("WorldState refresh failed", exception);
        }
    }

    public WorldStateRuntimeSnapshot read() {
        CacheEntry<WorldStateRuntimeSnapshot> value = cache.get("warframe-worldstate-v1", WorldStateRuntimeSnapshot.class);
        return value instanceof CacheEntry.Hit<WorldStateRuntimeSnapshot> hit ? hit.value() : null;
    }

    public WorldStateRuntimeSnapshot write(WorldStateRuntimeSnapshot snapshot) {
        WorldStateRuntimeSnapshot value = Objects.requireNonNull(snapshot, "snapshot");
        afterCommit(() -> {
            cache.put("warframe-worldstate-v1", value, properties.worldStateRetention());
            WarframeWorldStateUpdatedMessageProducer producer =
                    updateProducer == null ? null : updateProducer.getIfAvailable();
            if (producer != null) {
                producer.publish(value.contentHash());
            }
        });
        return value;
    }

    private void afterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }
}
