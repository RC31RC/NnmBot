package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeOfficialExportRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface WarframeOfficialExportRecordRepository
        extends JpaRepository<WarframeOfficialExportRecord, Long>,
        JpaSpecificationExecutor<WarframeOfficialExportRecord> {

    Optional<WarframeOfficialExportRecord>
            findByExportTypeIgnoreCaseAndUniqueNameIgnoreCase(
                    String exportType, String uniqueName);

    List<WarframeOfficialExportRecord> findAllByEnabledTrue();
}
