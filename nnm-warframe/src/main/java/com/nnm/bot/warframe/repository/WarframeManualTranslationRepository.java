package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.domain.entity.WarframeManualTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarframeManualTranslationRepository
        extends JpaRepository<WarframeManualTranslation, Long> {
}
