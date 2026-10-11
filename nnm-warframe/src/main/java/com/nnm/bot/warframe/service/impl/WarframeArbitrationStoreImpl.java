package com.nnm.bot.warframe.service.impl;

import com.nnm.bot.warframe.application.WarframeArbitrationStore;
import com.nnm.bot.warframe.model.ArbitrationScheduleEntry;
import com.nnm.bot.warframe.model.entity.WarframeArbitrationSchedule;
import com.nnm.bot.warframe.repository.WarframeArbitrationScheduleRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Service
public class WarframeArbitrationStoreImpl implements WarframeArbitrationStore {
    @Resource
    private WarframeArbitrationScheduleRepository repository;

    @Override
    @Transactional
    public void save(List<ArbitrationScheduleEntry> entries, String source, Instant fetchedAt, Instant cutoff) {
        // Network and parsing happen before this transaction; a failed batch preserves the old schedule.
        merge(entries.stream().filter(entry -> entry.expiry().isAfter(cutoff)).toList(),
                List.of(), source, fetchedAt);
        repository.deleteByExpiryLessThanEqual(cutoff);
    }

    @Override
    @Transactional
    public void merge(
            Collection<ArbitrationScheduleEntry> entries,
            Collection<Instant> deletedActivations,
            String source,
            Instant fetchedAt) {
        repository.saveAll(entries.stream()
                .map(entry -> toRecord(entry, source, fetchedAt))
                .toList());
        if (!deletedActivations.isEmpty()) {
            repository.deleteAllById(deletedActivations);
        }
    }

    private WarframeArbitrationSchedule toRecord(
            ArbitrationScheduleEntry entry,
            String source,
            Instant fetchedAt) {
        var record = new WarframeArbitrationSchedule();
        record.setActivation(entry.activation());
        record.setExpiry(entry.expiry());
        record.setUpstreamId(entry.id());
        record.setNode(entry.node());
        record.setMissionType(entry.missionType());
        record.setEnemy(entry.enemy());
        record.setPlanet(entry.planet());
        record.setBoundsJson(entry.boundsJson());
        record.setSourceUrl(source);
        record.setFetchedAt(fetchedAt);
        return record;
    }
}
