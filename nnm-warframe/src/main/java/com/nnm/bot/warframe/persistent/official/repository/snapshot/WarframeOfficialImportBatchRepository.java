package com.nnm.bot.warframe.persistent.official.repository.snapshot;

import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface WarframeOfficialImportBatchRepository extends JpaRepository<WarframeOfficialImportBatch, String> {
    List<WarframeOfficialImportBatch> findAllByStatusAndExpiresAtAfterOrderByCreatedAtDesc(String status, Instant now);
}
