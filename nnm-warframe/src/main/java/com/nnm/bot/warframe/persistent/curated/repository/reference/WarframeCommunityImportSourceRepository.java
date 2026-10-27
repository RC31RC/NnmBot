package com.nnm.bot.warframe.persistent.curated.repository.reference;

import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityImportSource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarframeCommunityImportSourceRepository extends JpaRepository<WarframeCommunityImportSource, String> {
}
