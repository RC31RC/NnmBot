package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.model.ArbitrationScheduleEntry;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface WarframeArbitrationStore {
    void save(List<ArbitrationScheduleEntry> entries, String source, Instant fetchedAt, Instant cutoff);

    void merge(
            Collection<ArbitrationScheduleEntry> entries,
            Collection<Instant> deletedActivations,
            String source,
            Instant fetchedAt);
}
