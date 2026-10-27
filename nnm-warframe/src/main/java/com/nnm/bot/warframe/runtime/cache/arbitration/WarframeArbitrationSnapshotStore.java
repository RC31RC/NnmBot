package com.nnm.bot.warframe.runtime.cache.arbitration;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationPullProperties;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleEntryDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/** 每个 JVM 仅保留一份有界不可变排期快照；重启后重新拉取，不写数据库或 Redis。 */
@Component
public class WarframeArbitrationSnapshotStore {
    private final ReentrantLock snapshotLock = new ReentrantLock();

    @Resource
    private WarframeArbitrationPullProperties properties;

    private final AtomicReference<Snapshot> current = new AtomicReference<>(new Snapshot(List.of(), null));

    /**
     * 读取当前有效快照或业务状态。
     *
     * @return 快照对象。
     */
    public Snapshot current() {
        return current.get();
    }

    /**
     * 替换当前内容并保持所属业务数据的一致性。
     *
     * @param entries 条目集合。
     * @param fetchedAt 拉取时间时刻。
     */
    public void replace(List<ArbitrationScheduleEntryDTO> entries, Instant fetchedAt) {
        snapshotLock.lock();
        try {
            Objects.requireNonNull(fetchedAt, "fetchedAt");
            if (CollUtil.isEmpty(entries) || entries.size() > properties.getMaxRecords()) {
                throw new IllegalArgumentException("Invalid arbitration snapshot size");
            }
            Instant retainAfter = fetchedAt.minus(properties.getHistoryRetention());
            TreeMap<Instant, ArbitrationScheduleEntryDTO> retained = new TreeMap<>();
            // 只保留本进程见过的有限历史；上游撤回的未来任务不能从旧快照中复活。
            for (ArbitrationScheduleEntryDTO old : current.get().entries()) {
                if (old.activation().isBefore(fetchedAt) && old.expiry().isAfter(retainAfter)) {
                    retained.put(old.activation(), old);
                }
            }
            for (ArbitrationScheduleEntryDTO entry : entries) {
                if (entry.expiry().isAfter(retainAfter)) retained.put(entry.activation(), entry);
            }
            // 历史和最新响应共用记录数预算，优先淘汰最早历史，不能随运行时间无界增长。
            while (retained.size() > properties.getMaxRecords()) retained.pollFirstEntry();
            current.set(new Snapshot(List.copyOf(retained.values()), fetchedAt));
        } finally {
            snapshotLock.unlock();
        }
    }

    public record Snapshot(List<ArbitrationScheduleEntryDTO> entries, Instant fetchedAt) {
        /**
         * 构造快照，并保存初始化参数。
         *
         * @param entries 条目集合。
         * @param fetchedAt 拉取时间时刻。
         */
        public Snapshot {
            entries = List.copyOf(entries);
        }
    }
}
