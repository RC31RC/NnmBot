package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeArbitrationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WarframeArbitrationScheduleRepository
        extends JpaRepository<WarframeArbitrationSchedule, Instant> {
    List<WarframeArbitrationSchedule> findByExpiryGreaterThanAndActivationLessThanOrderByActivationAsc(
            Instant from, Instant to);

    List<WarframeArbitrationSchedule> findByActivationGreaterThanEqualAndActivationLessThanOrderByActivationAsc(
            Instant from, Instant to);
    List<WarframeArbitrationSchedule> findAllByOrderByActivationAsc();
    Optional<WarframeArbitrationSchedule> findFirstByOrderByFetchedAtDesc();
    long deleteByExpiryLessThanEqual(Instant cutoff);
}
