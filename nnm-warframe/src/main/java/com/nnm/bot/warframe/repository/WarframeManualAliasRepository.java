package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.domain.entity.WarframeManualAlias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarframeManualAliasRepository extends JpaRepository<WarframeManualAlias, Long> {

    List<WarframeManualAlias> findAllByEnabledTrue();

    List<WarframeManualAlias> findAllByManualItemId(Long manualItemId);

    List<WarframeManualAlias> findAllByNormalizedAlias(String normalizedAlias);

    boolean existsByManualItemIdAndNormalizedAlias(
            Long manualItemId, String normalizedAlias);

    boolean existsByManualItemIdAndNormalizedAliasAndIdNot(
            Long manualItemId, String normalizedAlias, Long id);

    void deleteAllByManualItemId(Long manualItemId);
}
