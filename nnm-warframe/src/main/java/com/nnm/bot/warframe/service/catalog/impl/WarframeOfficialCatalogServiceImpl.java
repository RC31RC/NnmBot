package com.nnm.bot.warframe.service.catalog.impl;

import com.nnm.bot.warframe.service.catalog.WarframeOfficialCatalogService;
import com.nnm.bot.warframe.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.model.entity.WarframeOfficialExportRecord;
import com.nnm.bot.warframe.model.entity.WarframeOfficialRegion;
import com.nnm.bot.warframe.pojo.WarframeOfficialDataRecord;
import com.nnm.bot.warframe.pojo.WarframeOfficialRegionData;
import com.nnm.bot.warframe.repository.WarframeOfficialExportRecordRepository;
import com.nnm.bot.warframe.repository.WarframeOfficialRegionRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WarframeOfficialCatalogServiceImpl implements WarframeOfficialCatalogService {

    @Resource
    private WarframeOfficialExportRecordRepository recordRepository;
    @Resource
    private WarframeOfficialRegionRepository regionRepository;

    @Override
    public List<WarframeOfficialDataRecord> records() {
        return recordRepository.findAllByEnabledTrue().stream()
                .map(WarframeOfficialCatalogServiceImpl::toRecord)
                .toList();
    }

    @Override
    public List<WarframeOfficialDataRecord> records(WarframeOfficialDataDomain domain) {
        return records().stream()
                .filter(record -> record.domain() == domain)
                .toList();
    }

    @Override
    public List<WarframeOfficialRegionData> regions() {
        Map<Long, WarframeOfficialRegion> details = regionRepository.findAll().stream()
                .collect(Collectors.toMap(
                        WarframeOfficialRegion::getOfficialRecordId,
                        Function.identity()));
        return recordRepository.findAllByEnabledTrue().stream()
                .filter(record -> WarframeOfficialDataDomain.fromExportType(
                        record.getExportType()) == WarframeOfficialDataDomain.REGION)
                .map(record -> toRegion(record, details.get(record.getId())))
                .toList();
    }

    private static WarframeOfficialDataRecord toRecord(WarframeOfficialExportRecord record) {
        return new WarframeOfficialDataRecord(
                record.getId(),
                WarframeOfficialDataDomain.fromExportType(record.getExportType()),
                record.getUniqueName(),
                record.getExportFile(),
                record.getExportType(),
                record.getRecordType(),
                record.getOfficialName(),
                record.getLocalizedName(),
                record.getParentName(),
                record.getProductCategory(),
                record.getDescription(),
                record.getLongDescription(),
                record.getWikiSlug(),
                record.getMarketSlug(),
                record.getRawJson(),
                record.getLocalizedRawJson());
    }

    private static WarframeOfficialRegionData toRegion(
            WarframeOfficialExportRecord record, WarframeOfficialRegion region) {
        return new WarframeOfficialRegionData(
                toRecord(record),
                region == null ? null : region.getSystemName(),
                region == null ? null : region.getSystemIndex(),
                region == null ? null : region.getNodeType(),
                region == null ? null : region.getMasteryReq(),
                region == null ? null : region.getMissionIndex(),
                region == null ? null : region.getFactionIndex(),
                region == null ? null : region.getMinEnemyLevel(),
                region == null ? null : region.getMaxEnemyLevel());
    }
}
