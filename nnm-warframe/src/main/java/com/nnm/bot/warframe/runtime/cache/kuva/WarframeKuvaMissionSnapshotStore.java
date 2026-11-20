package com.nnm.bot.warframe.runtime.cache.kuva;

import com.nnm.bot.warframe.runtime.pojo.dto.kuva.KuvaMissionDTO;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

/** 每个 JVM 独立持有最近一次成功的赤毒任务快照。 */
@Component
public class WarframeKuvaMissionSnapshotStore {
    private final AtomicReference<Snapshot> current = new AtomicReference<>(new Snapshot(List.of(), null));

    /** @return 当前不可变快照。 */
    public Snapshot current() {
        return current.get();
    }

    /** @param missions 成功解析的当前任务。 @param fetchedAt 本次成功采集时间。 */
    public void replace(List<KuvaMissionDTO> missions, Instant fetchedAt) {
        current.set(new Snapshot(missions, fetchedAt));
    }

    public record Snapshot(List<KuvaMissionDTO> missions, Instant fetchedAt) {
        public Snapshot {
            missions = List.copyOf(missions);
        }
    }
}
