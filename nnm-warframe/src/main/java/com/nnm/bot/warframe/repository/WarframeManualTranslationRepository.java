package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeManualTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarframeManualTranslationRepository
        extends JpaRepository<WarframeManualTranslation, Long> {
}
