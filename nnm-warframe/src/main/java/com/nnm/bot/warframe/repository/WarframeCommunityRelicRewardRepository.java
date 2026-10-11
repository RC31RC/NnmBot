package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicReward;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarframeCommunityRelicRewardRepository
        extends JpaRepository<WarframeCommunityRelicReward, Long> {
    List<WarframeCommunityRelicReward> findAllByEnabledTrue();
    List<WarframeCommunityRelicReward> findAllByEnabledTrueAndRarityIgnoreCase(String rarity);
}
