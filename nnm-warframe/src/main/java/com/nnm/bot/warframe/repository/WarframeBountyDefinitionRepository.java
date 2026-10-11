package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeCommunityBountyDefinition;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarframeBountyDefinitionRepository
        extends JpaRepository<WarframeCommunityBountyDefinition, Long> {
    @EntityGraph(attributePaths = "stages")
    List<WarframeCommunityBountyDefinition> findAllByEnabledTrueAndRecordKey(String recordKey);
}
