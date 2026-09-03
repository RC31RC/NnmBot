package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.domain.entity.WarframeOfficialRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarframeOfficialRegionRepository
        extends JpaRepository<WarframeOfficialRegion, Long> {
}
