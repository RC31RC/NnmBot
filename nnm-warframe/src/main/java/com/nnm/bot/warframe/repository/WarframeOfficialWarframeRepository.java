package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeOfficialWarframe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarframeOfficialWarframeRepository
        extends JpaRepository<WarframeOfficialWarframe, Long> {
}
