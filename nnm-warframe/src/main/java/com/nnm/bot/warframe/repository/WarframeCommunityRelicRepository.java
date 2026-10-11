package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeCommunityRelic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarframeCommunityRelicRepository extends JpaRepository<WarframeCommunityRelic, Long> {
    List<WarframeCommunityRelic> findAllByEnabledTrue();
}
