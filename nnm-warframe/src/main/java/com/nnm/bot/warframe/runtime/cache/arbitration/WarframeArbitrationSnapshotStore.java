package com.nnm.bot.warframe.runtime.cache.arbitration;

import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationSourceProperties;
import com.nnm.bot.warframe.runtime.pojo.dto.arbitration.ArbitrationScheduleEntryDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/** 每个 JVM 仅保留一份有界不可变排期快照；重启后重新拉取，不写数据库或 Redis。 */
@Component
public class WarframeArbitrationSnapshotStore {
    @Resource
    private WarframeArbitrationSourceProperties properties;

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
        if (entries == null || entries.size() > properties.getMaxRecords())
            throw new IllegalArgumentException("Invalid arbitration snapshot size");
        current.set(new Snapshot(
            entries.stream().sorted(Comparator.comparing(ArbitrationScheduleEntryDTO::activation)).toList(),
            fetchedAt));
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
