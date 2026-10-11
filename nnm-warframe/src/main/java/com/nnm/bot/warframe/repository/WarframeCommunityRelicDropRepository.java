package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicDrop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarframeCommunityRelicDropRepository
        extends JpaRepository<WarframeCommunityRelicDrop, Long> {
    List<WarframeCommunityRelicDrop> findAllByEnabledTrue();
}
