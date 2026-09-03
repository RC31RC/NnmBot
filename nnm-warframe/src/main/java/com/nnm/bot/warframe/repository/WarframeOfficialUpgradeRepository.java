package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.domain.entity.WarframeOfficialUpgrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarframeOfficialUpgradeRepository
        extends JpaRepository<WarframeOfficialUpgrade, Long> {
}
