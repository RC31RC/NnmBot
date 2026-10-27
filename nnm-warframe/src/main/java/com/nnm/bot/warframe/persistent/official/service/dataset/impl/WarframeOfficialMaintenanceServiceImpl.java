package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.IMPORT_HEAD_ID;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputTypes;
import com.nnm.bot.warframe.persistent.official.pojo.query.OfficialRecordIdentityQuery;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.official.event.WarframeOfficialDataChangePublisher;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeWorldStateCodebookInvalidator;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.maintenance.WarframeOfficialRebuildMarker;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportHeadRepository;
import com.nnm.bot.warframe.persistent.official.service.record.OfficialRecordPersistenceService;
import com.nnm.bot.warframe.persistent.official.repository.maintenance.WarframeOfficialRebuildMarkerRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialMaintenanceService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialDetailService;
import com.nnm.bot.warframe.persistent.official.parser.schema.WarframeOfficialModels;
import com.nnm.bot.warframe.persistent.official.pojo.dto.maintenance.WarframeOfficialRecordSaveDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

/** 单个 JSON 主表及必要子孙的维护状态转换，与上游版本状态分离。 */
@Service
public class WarframeOfficialMaintenanceServiceImpl implements WarframeOfficialMaintenanceService {

    @Resource
    private WarframeOfficialImportHeadRepository heads;
    @Resource
    private WarframeOfficialDataChangePublisher dataChanges;
    @Resource
    private WarframeWorldStateCodebookInvalidator codebookInvalidator;
    @Resource
    private OfficialRecordPersistenceService records;
    @Resource
    private WarframeOfficialRebuildMarkerRepository rebuildMarkers;
    @Resource
    private WarframeOfficialDetailService details;

    @Resource
    private WarframeOfficialModels models;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 显式保存当前业务记录。
     *
     * @param id 目标记录的本地主键。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 游戏资料官方记录对象。
     */
    @Override
    @Transactional
    public WarframeOfficialRecordDTO save(Long id, WarframeOfficialRecordSaveDTO request) {
        BusinessAssert.isTrue(request != null && request.data() instanceof OfficialObjectDTO,
                WarframeManagementErrorCode.DATASET_INVALID);
        lockImportBaseline();
        OfficialRecord root;
        if (id != null) {
            root = require(id);
            if (root.getMaintenanceType() == MaintenanceType.OFFICIAL) {
                root = convertRoot(id, request.confirmed(), request.replaceExisting());
            }
        } else {
            WarframeOfficialDataDomain domain = WarframeOfficialDataDomain.fromCode(request.domain());
            BusinessAssert.isTrue(domain != WarframeOfficialDataDomain.OTHER
                    && StrUtil.isNotBlank(request.bkey()) && request.bkey().length() <= 500,
                    WarframeManagementErrorCode.DATASET_INVALID);
            var existing = records.findForMaintenance(new OfficialRecordIdentityQuery(
                    MaintenanceType.SELF_MAINTAINED, domain.domainCode(), request.bkey()));
            BusinessAssert.isTrue(existing.isEmpty() || request.replaceExisting(),
                    WarframeManagementErrorCode.SELF_MAINTAINED_OVERWRITE_CONFIRM_REQUIRED);
            root = existing.orElseGet(() -> records.newRecord(domain.domainCode()));
            root.setBkey(request.bkey());
            root.setMaintenanceType(MaintenanceType.SELF_MAINTAINED);
            root.setEnabled(true);
        }
        OfficialRecordDTO data = request.data();
        BusinessAssert.isTrue(OfficialInputTypes.type(models.domain(root.getClass())).isInstance(data),
                WarframeManagementErrorCode.DATASET_INVALID, "请求的具体字段模型与所属文件不一致");
        OfficialRecordHeaderDTO header = data.header();
        String json;
        try { json = objectMapper.writeValueAsString(data); }
        catch (JsonProcessingException exception) { throw new IllegalArgumentException("官方记录无法序列化", exception); }
        root.setRawJson(json);
        root.setLocalizedRawJson(json);
        root.setOfficialName(header.officialName());
        root.setLocalizedName(header.localizedName() == null ? header.officialName() : header.localizedName());
        root.setDescription(header.description());
        root.setParentName(header.parentName());
        root.setProductCategory(header.productCategory());
        root = records.saveAndFlush(root);
        details.synchronize(List.of(root), List.of());
        invalidateAfterCommit();
        return records.findDataById(root.getId()).orElseThrow();
    }

    /**
     * 删除指定业务记录并维护相关数据。
     *
     * @param recordId 当前记录的本地主键。
     */
    @Override
    @Transactional
    public void delete(long recordId) {
        lockImportBaseline();
        OfficialRecord record = require(recordId);
        records.deleteRecords(List.of(recordId));
        records.flush();
        if (record.getMaintenanceType() == MaintenanceType.OFFICIAL) {
            markForRebuild(record);
        }
        invalidateAfterCommit();
    }

    /**
     * 将选定官方记录及其详情转换为同模型的自维护数据。
     *
     * @param recordId 当前记录的本地主键。
     * @param confirmed 调用方是否已确认当前变更。
     * @param replaceExisting 是否允许替换同身份已有数据。
     * @return 游戏资料官方记录对象。
     */
    @Override
    @Transactional
    public WarframeOfficialRecordDTO convertToSelfMaintained(
            long recordId, boolean confirmed, boolean replaceExisting) {
        OfficialRecord saved = convertRoot(recordId, confirmed, replaceExisting);
        return records.findDataById(saved.getId()).orElseThrow();
    }

    /**
     * 转换当前独立主表记录，并同步维护记录元数据。
     *
     * @param recordId 当前记录的本地主键。
     * @param confirmed 调用方是否已确认当前变更。
     * @param replaceExisting 是否允许替换同身份已有数据。
     * @return 官方记录实体对象。
     */
    private OfficialRecord convertRoot(long recordId, boolean confirmed, boolean replaceExisting) {
        BusinessAssert.isTrue(confirmed, WarframeManagementErrorCode.OFFICIAL_CONVERSION_CONFIRM_REQUIRED);
        lockImportBaseline();
        OfficialRecord source = require(recordId);
        BusinessAssert.isTrue(source.getMaintenanceType() == MaintenanceType.OFFICIAL,
                WarframeManagementErrorCode.OFFICIAL_CONVERSION_CONFIRM_REQUIRED);
        var existing = records.findForMaintenance(new OfficialRecordIdentityQuery(
                MaintenanceType.SELF_MAINTAINED,
                models.domain(source.getClass()).domainCode(), source.getBkey()));
        BusinessAssert.isTrue(existing.isEmpty() || replaceExisting,
                WarframeManagementErrorCode.SELF_MAINTAINED_OVERWRITE_CONFIRM_REQUIRED);
        OfficialRecord self = existing.orElseGet(() -> records.newRecord(
                models.domain(source.getClass()).domainCode()));
        copy(source, self);
        self.setMaintenanceType(MaintenanceType.SELF_MAINTAINED);
        self.setEnabled(true);
        OfficialRecord saved = records.saveAndFlush(self);
        details.synchronize(List.of(saved), List.of());
        // 自维护是并存的整树覆盖；保留官方版本，删除覆盖后可立即恢复官方查询结果。
        records.flush();
        invalidateAfterCommit();
        return saved;
    }

    /**
     * 锁定当前官方数据基线，串行保护自维护写入与版本采纳。
     */
    private void lockImportBaseline() {
        heads.findFirstById(IMPORT_HEAD_ID).orElseThrow(() -> new IllegalStateException(
                "官方数据指针未初始化，请更新 init.sql"));
    }

    /**
     * 在当前事务成功提交后刷新相关缓存。
     */
    private void invalidateAfterCommit() {
        dataChanges.publishAfterCommit();
        codebookInvalidator.invalidateAfterCommit();
    }

    /**
     * 取得已登记且满足当前业务约束的对象，缺失时直接失败。
     *
     * @param recordId 当前记录的本地主键。
     * @return 官方记录实体对象。
     */
    private OfficialRecord require(long recordId) {
        return records.findById(recordId).filter(OfficialRecord::isEnabled)
                .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND));
    }

    /**
     * 为删除的官方记录登记待重建标记，供后续重新采纳处理。
     * @param record 当前业务记录。
     */
    private void markForRebuild(OfficialRecord record) {
        WarframeOfficialDataDomain domain = models.domain(record.getClass());
        WarframeOfficialRebuildMarker marker = rebuildMarkers.findByProductPathAndDomainCodeAndBkey(
                        domain.productPath(), domain.domainCode(), record.getBkey())
                .orElseGet(WarframeOfficialRebuildMarker::new);
        marker.setProductPath(domain.productPath());
        marker.setDomainCode(domain.domainCode());
        marker.setBkey(record.getBkey());
        marker.setRequestedAt(Instant.now());
        rebuildMarkers.save(marker);
    }

    /**
     * 复制当前数据，避免调用方修改共享状态。
     *
     * @param source 需要复制的当前官方主表实体。
     * @param target 本次操作的目标对象或路径。
     */
    private static void copy(OfficialRecord source, OfficialRecord target) {
        target.setBkey(source.getBkey());
        target.setOfficialName(source.getOfficialName());
        target.setLocalizedName(source.getLocalizedName());
        target.setParentName(source.getParentName());
        target.setProductCategory(source.getProductCategory());
        target.setExcludeFromCodex(source.getExcludeFromCodex());
        target.setDescription(source.getDescription());
        target.setLongDescription(source.getLongDescription());
        target.setWikiSlug(source.getWikiSlug());
        target.setMarketSlug(source.getMarketSlug());
        target.setRawJson(source.getRawJson());
        target.setLocalizedRawJson(source.getLocalizedRawJson());
    }
}
