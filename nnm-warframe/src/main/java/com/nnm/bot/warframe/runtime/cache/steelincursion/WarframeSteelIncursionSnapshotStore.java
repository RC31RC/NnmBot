package com.nnm.bot.warframe.runtime.cache.steelincursion;

import com.nnm.bot.warframe.runtime.config.steelincursion.WarframeSteelIncursionProperties;
import com.nnm.bot.warframe.runtime.pojo.dto.steelincursion.SteelIncursionEntryDTO;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

/** 保存本实例最近一次成功获取的有界钢铁侵袭排期。 */
@Component
public class WarframeSteelIncursionSnapshotStore {
    @Resource
    private WarframeSteelIncursionProperties properties;
    private final AtomicReference<Snapshot> current = new AtomicReference<>(new Snapshot(List.of(), null));

    /** @return 当前不可变钢铁侵袭快照。 */
    public Snapshot current() {
        return current.get();
    }

    /** @param entries 解析后的每日任务。 @param fetchedAt 本次成功采集时间。 */
    public void replace(List<SteelIncursionEntryDTO> entries, Instant fetchedAt) {
        if (entries == null || entries.size() > properties.getDataWindow().toDays() * 6L)
            throw new IllegalArgumentException("Invalid steel incursion snapshot size");
        current.set(new Snapshot(entries, fetchedAt));
    }

    public record Snapshot(List<SteelIncursionEntryDTO> entries, Instant fetchedAt) {
        public Snapshot {
            entries = List.copyOf(entries);
        }
    }
}
