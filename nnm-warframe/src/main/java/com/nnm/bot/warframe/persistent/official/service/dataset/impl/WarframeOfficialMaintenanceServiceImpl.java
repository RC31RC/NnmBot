package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.METADATA_BKEY;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.RECORD_GROUPS;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.constant.WarframeCacheErrorCode;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.event.WarframeOfficialDataChangePublisher;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputTypes;
import com.nnm.bot.warframe.persistent.official.parser.schema.WarframeOfficialModels;
import com.nnm.bot.warframe.persistent.official.pojo.dto.maintenance.WarframeOfficialRecordSaveDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.maintenance.WarframeOfficialRebuildMarker;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.pojo.query.OfficialRecordIdentityQuery;
import com.nnm.bot.warframe.persistent.official.repository.maintenance.WarframeOfficialRebuildMarkerRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialFileRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialMaintenanceService;
import com.nnm.bot.warframe.persistent.official.service.record.OfficialRecordPersistenceService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialDetailService;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 单个 JSON 主表及必要子孙的维护状态转换，与上游版本状态分离。 */
@Service
public class WarframeOfficialMaintenanceServiceImpl implements WarframeOfficialMaintenanceService {

    @Resource
    private WarframeOfficialFileRepository files;
    @Resource
    private WarframeOfficialDataChangePublisher dataChanges;
    @Resource
    private OfficialRecordPersistenceService records;
    @Resource
    private WarframeOfficialRebuildMarkerRepository rebuildMarkers;
    @Resource
    private WarframeOfficialDetailService details;

    @Resource
    private WarframeOfficialModels models;

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
        OfficialRecord previous = id == null ? null : require(id);
        WarframeOfficialDataDomain domain =
            id == null ? WarframeOfficialDataDomain.fromCode(request.domain()) : models.domain(previous.getClass());
        BusinessAssert.isTrue(domain != WarframeOfficialDataDomain.OTHER, WarframeManagementErrorCode.DATASET_INVALID);
        String group = StrUtil.trimToEmpty(request.group());
        BusinessAssert.isTrue(group.isEmpty() || RECORD_GROUPS.getOrDefault(domain, Set.of()).contains(group),
            OfficialDataErrorCode.GROUP_INVALID);
        lockDataset(domain);
        if (previous != null)
            records.releaseWritten(List.of(previous));
        OfficialRecord root;
        if (id != null) {
            root = records.findById(domain, id).filter(OfficialRecord::isEnabled)
                .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND));
            if (root.getMaintenanceType() == MaintenanceType.OFFICIAL) {
                root = convertRoot(id, request.confirmed(), request.replaceExisting());
            }
        } else {
            BusinessAssert.isTrue(domain != WarframeOfficialDataDomain.OTHER,
                WarframeManagementErrorCode.DATASET_INVALID);
            root = records.newRecord(domain.domainCode());
            // 新增独立资料由后端分配身份；编辑和官方覆盖沿用已有业务键，不能随名称变化。
            root.setBkey((group.isEmpty() ? "" : group + "/") + "self:" + UUID.randomUUID());
            root.setMaintenanceType(MaintenanceType.SELF_MAINTAINED);
            root.setEnabled(true);
        }
        OfficialRecordDTO data = request.data();
        BusinessAssert.isTrue(OfficialInputTypes.type(models.domain(root.getClass())).isInstance(data),
            WarframeManagementErrorCode.DATASET_INVALID, "请求的具体字段模型与所属文件不一致");
        OfficialRecordHeaderDTO header = data.header();
        root.setOfficialName(header.officialName());
        root.setLocalizedName(header.localizedName() == null ? header.officialName() : header.localizedName());
        root.setDescription(header.description());
        root.setParentName(header.parentName());
        root.setProductCategory(header.productCategory());
        root = records.saveAndFlush(root);
        details.synchronize(List.of(root), Map.of(root.getId(), data), List.of());
        dataChanges.publishAfterCommit(domain);
        return records.findDataById(domain, root.getId()).orElseThrow();
    }

    /**
     * 删除指定业务记录并维护相关数据。
     *
     * @param recordId 当前记录的本地主键。
     */
    @Override
    @Transactional
    public void delete(long recordId) {
        OfficialRecord previous = require(recordId);
        lockDataset(models.domain(previous.getClass()));
        records.releaseWritten(List.of(previous));
        OfficialRecord record = records.findById(models.domain(previous.getClass()), recordId)
            .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND));
        records.deleteRecords(models.domain(record.getClass()), List.of(recordId));
        records.flush();
        if (record.getMaintenanceType() == MaintenanceType.OFFICIAL) {
            markForRebuild(record);
        }
        dataChanges.publishAfterCommit(models.domain(record.getClass()));
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
    public WarframeOfficialRecordDTO convertToSelfMaintained(long recordId, boolean confirmed,
        boolean replaceExisting) {
        OfficialRecord saved = convertRoot(recordId, confirmed, replaceExisting);
        return records.findDataById(models.domain(saved.getClass()), saved.getId()).orElseThrow();
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
        OfficialRecord previous = require(recordId);
        lockDataset(models.domain(previous.getClass()));
        records.releaseWritten(List.of(previous));
        OfficialRecord source = records.findById(models.domain(previous.getClass()), recordId)
            .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND));
        BusinessAssert.isTrue(source.getMaintenanceType() == MaintenanceType.OFFICIAL,
            WarframeManagementErrorCode.OFFICIAL_CONVERSION_CONFIRM_REQUIRED);
        var existing = records.findForMaintenance(new OfficialRecordIdentityQuery(MaintenanceType.SELF_MAINTAINED,
            models.domain(source.getClass()).domainCode(), source.getBkey()));
        BusinessAssert.isTrue(existing.isEmpty() || replaceExisting,
            WarframeManagementErrorCode.SELF_MAINTAINED_OVERWRITE_CONFIRM_REQUIRED);
        OfficialRecord self =
            existing.orElseGet(() -> records.newRecord(models.domain(source.getClass()).domainCode()));
        copy(source, self);
        self.setMaintenanceType(MaintenanceType.SELF_MAINTAINED);
        self.setEnabled(true);
        OfficialRecordDTO data = records
            .loadDetails(models.domain(source.getClass()).domainCode(), List.of(source.getId())).get(source.getId());
        BusinessAssert.notNull(data, WarframeManagementErrorCode.DATASET_INVALID);
        OfficialRecord saved = records.saveAndFlush(self);
        details.synchronize(List.of(saved), Map.of(saved.getId(), data), List.of());
        // 自维护是并存的整树覆盖；保留官方版本，删除覆盖后可立即恢复官方查询结果。
        records.flush();
        dataChanges.publishAfterCommit(models.domain(saved.getClass()));
        return saved;
    }

    /** 锁住同一文件的短事务，协调官方删除／复制与分类采纳的提交先后。 */
    private void lockDataset(WarframeOfficialDataDomain domain) {
        BusinessAssert.notNull(domain, WarframeManagementErrorCode.DATASET_INVALID);
        // 初始化 SQL 预建全部文件行；自维护覆盖也必须在同一文件版本下提交。
        files.findFirstByProductPath(domain.productPath()).orElseThrow(
            () -> new SystemException(WarframeCacheErrorCode.DATASET_VERSION_MISSING, "数据集版本锁行未初始化，请执行本轮 SQL"));
    }

    /**
     * 取得已登记且满足当前业务约束的对象，缺失时直接失败。
     *
     * @param recordId 当前记录的本地主键。
     * @return 官方记录实体对象。
     */
    private OfficialRecord require(long recordId) {
        OfficialRecord record = records.findById(recordId).filter(OfficialRecord::isEnabled)
            .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND));
        BusinessAssert.isTrue(!METADATA_BKEY.equals(record.getBkey()), OfficialDataErrorCode.METADATA_READ_ONLY);
        return record;
    }

    /**
     * 为删除的官方记录登记待重建标记，供后续重新采纳处理。
     *
     * @param record 当前业务记录。
     */
    private void markForRebuild(OfficialRecord record) {
        WarframeOfficialDataDomain domain = models.domain(record.getClass());
        WarframeOfficialRebuildMarker marker = rebuildMarkers
            .findByProductPathAndDomainCodeAndBkey(domain.productPath(), domain.domainCode(), record.getBkey())
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
    }
}
