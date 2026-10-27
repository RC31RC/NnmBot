package com.nnm.bot.warframe.persistent.official.repository.snapshot;

import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarframeOfficialImportBatchRepository extends JpaRepository<WarframeOfficialImportBatch, String> {
}
