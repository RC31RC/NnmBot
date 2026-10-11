package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeManualItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WarframeManualItemRepository extends JpaRepository<WarframeManualItem, Long> {

    Optional<WarframeManualItem> findBySourceKeyIgnoreCase(String sourceKey);

    List<WarframeManualItem> findAllByEnabledTrue();

    boolean existsBySourceKeyIgnoreCaseAndIdNot(String sourceKey, Long id);
}
