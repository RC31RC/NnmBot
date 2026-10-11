package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeCommunityWorldStateRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarframeCommunityWorldStateRuleRepository
        extends JpaRepository<WarframeCommunityWorldStateRule, Long> {
    List<WarframeCommunityWorldStateRule> findByDatasetCodeAndEnabledTrueOrderByRecordKeyAsc(
            String datasetCode);
}
