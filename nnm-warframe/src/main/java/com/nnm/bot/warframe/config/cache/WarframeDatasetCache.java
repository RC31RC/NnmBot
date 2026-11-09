package com.nnm.bot.warframe.config.cache;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.warframe.constant.WarframeCacheErrorCode;
import com.nnm.bot.warframe.constant.WarframeDatasetCacheNames;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * cache2k 拥有业务缓存；此处只维护固定数据集的版本与加载互斥信息。 快照先构建后替换，按键补查按真实批次回填，旧代加载不能发布为新代数据。
 */
@Component
@Slf4j
public class WarframeDatasetCache {
    @Resource
    private CacheManager cacheManager;
    @Resource
    private WarframeCacheProperties cacheProperties;
    @Resource(name = "applicationTaskExecutor")
    private AsyncTaskExecutor executor;

    private final Map<String, State> states = new ConcurrentHashMap<>();
    // 一个实例同时只重建一份快照，避免旧快照、新快照与临时对象叠加放大内存。
    private final ReentrantLock rebuildLock = new ReentrantLock();
    private final AtomicBoolean rebuildScheduled = new AtomicBoolean();

    /**
     * 记录单调递增的数据版本；重复、乱序广播不会撤销较新版本。
     *
     * @param change 数据集及已提交版本。
     */
    public void observe(DatasetRevisionDTO change) {
        state(change.dataset()).revision.accumulateAndGet(change.revision(), Math::max);
    }

    /**
     * 读取不可变轻量快照；已有快照刷新期间继续可用，首次加载失败直接传播。
     *
     * @param dataset 来源隔离的数据集名。
     * @param view 同一数据集中的投影名称。
     * @param loader 只读取本数据集的快照构建器。
     * @return 当前有效快照。
     * @param <T> 不可变投影类型。
     */
    @SuppressWarnings("unchecked")
    public <T> T snapshot(String dataset, String view, Supplier<T> loader) {
        State state = state(dataset);
        Cache cache = region(dataset, WarframeDatasetCacheNames.SNAPSHOTS);
        Snapshot previous = cache.get(view, Snapshot.class);
        if (previous == null) {
            synchronized (state.snapshots) {
                previous = cache.get(view, Snapshot.class);
                if (previous == null)
                    return (T)buildSnapshot(dataset, view, cache, state, loader).value();
            }
        }
        if (previous.revision() != state.revision.get() && rebuildScheduled.compareAndSet(false, true)) {
            try {
                executor.execute(() -> {
                    try {
                        buildSnapshot(dataset, view, cache, state, loader);
                    } catch (RuntimeException exception) {
                        log.warn("数据集快照重建失败，保留有效快照 dataset={} view={}", dataset, view, exception);
                    } finally {
                        rebuildScheduled.set(false);
                    }
                });
            } catch (RuntimeException exception) {
                rebuildScheduled.set(false);
                log.warn("数据集重建提交失败，后续查询将重试 dataset={}", dataset, exception);
            }
        }
        return (T)previous.value();
    }

    /**
     * 一次收集未命中键，并委托 Repository 真正批量读取；成功的空结果短暂缓存。
     *
     * @param dataset 来源隔离的数据集名。
     * @param keys 已规范化且属于本数据集的业务键。
     * @param loader 一次批量补查，数据库异常须向外传播。
     * @return 仅包含找到的数据，不包含负缓存占位对象。
     * @param <K> 字符串业务键或数据库主键类型。
     * @param <T> 非受管业务投影类型。
     */
    @SuppressWarnings("unchecked")
    public <K, T> Map<K, T> records(String dataset, Collection<K> keys, Function<Collection<K>, Map<K, T>> loader) {
        if (keys.isEmpty())
            return Map.of();
        State state = state(dataset);
        Cache cache = region(dataset, WarframeDatasetCacheNames.RECORDS);
        // 同数据集并发冷查询在互斥内复核，避免每个调用者重复补查相同键。
        synchronized (state.records) {
            while (true) {
                long revision = state.revision.get();
                Map<K, T> result = new LinkedHashMap<>();
                var missing = new LinkedHashSet<K>();
                Instant now = Instant.now();
                for (K key : new LinkedHashSet<>(keys)) {
                    CachedRecord entry = cache.get(new RecordKey(revision, key), CachedRecord.class);
                    if (entry == null || (entry.value() == null && !now.isBefore(entry.negativeUntil()))) {
                        missing.add(key);
                    } else if (entry.value() != null) {
                        result.put(key, (T)entry.value());
                    }
                }
                if (!missing.isEmpty()) {
                    long started = System.nanoTime();
                    Map<K, T> loaded = Objects.requireNonNull(loader.apply(missing), "batch loader result");
                    if (revision != state.revision.get())
                        continue;
                    Instant negativeUntil = Instant.now().plus(cacheProperties.getNegativeTtl());
                    for (K key : missing) {
                        T value = loaded.get(key);
                        cache.put(new RecordKey(revision, key), new CachedRecord(value, negativeUntil));
                        if (value != null)
                            result.put(key, value);
                    }
                    log.debug("数据集批量补查 dataset={} revision={} requested={} found={} elapsedMs={}", dataset, revision,
                        missing.size(), loaded.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
                }
                if (revision == state.revision.get())
                    return Collections.unmodifiableMap(result);
            }
        }
    }

    /**
     * 在内存预算内串行构建快照；只发布与当前已知版本一致的结果。
     *
     * @param dataset 数据集名。
     * @param view 投影名。
     * @param cache cache2k 管理的快照区域。
     * @param state 当前数据集版本状态。
     * @param loader 快照构建器。
     * @return 已完成构建的有效快照。
     */
    private Snapshot buildSnapshot(String dataset, String view, Cache cache, State state, Supplier<?> loader) {
        rebuildLock.lock();
        try {
            while (true) {
                long revision = state.revision.get();
                long started = System.nanoTime();
                Object value = Objects.requireNonNull(loader.get(), "snapshot loader result");
                if (revision != state.revision.get())
                    continue;
                Snapshot next = new Snapshot(revision, value);
                cache.put(view, next);
                long size = value instanceof DatasetSnapshotMetrics snapshot ? snapshot.entryCount()
                    : value instanceof Map<?, ?> map ? map.size()
                        : value instanceof Collection<?> collection ? collection.size() : -1;
                log.debug("数据集快照构建 dataset={} view={} revision={} entries={} elapsedMs={}", dataset, view, revision,
                    size, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
                return next;
            }
        } finally {
            rebuildLock.unlock();
        }
    }

    /** @param dataset 数据集名。 @return 固定数据集的轻量版本与互斥状态。 */
    private State state(String dataset) {
        return states.computeIfAbsent(dataset, ignored -> new State());
    }

    /** @param dataset 数据集名。 @param suffix 缓存用途。 @return 预先登记的缓存区域。 */
    private Cache region(String dataset, String suffix) {
        Cache cache = cacheManager.getCache(WarframeDatasetCacheNames.region(dataset, suffix));
        if (cache == null)
            throw new SystemException(WarframeCacheErrorCode.REGION_UNREGISTERED, dataset);
        return cache;
    }

    private static final class State {
        private final AtomicLong revision = new AtomicLong();
        private final Object snapshots = new Object();
        private final Object records = new Object();
    }

    private record Snapshot(long revision, Object value) {
    }

    private record RecordKey(long revision, Object bkey) {
    }

    private record CachedRecord(Object value, Instant negativeUntil) {
    }
}
