package com.nnm.bot.warframe.persistent.official.service.record.impl;

import java.util.Objects;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.METADATA_BKEY;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.IMPORT_HEAD_ID;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.WarframeOfficialRegionDTO;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialRepositoryRegistry;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialTableRelations;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportHeadRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Map;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialRegionDTO;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.LinkedHashSet;

@Service
public class WarframeOfficialRecordReadServiceImpl implements WarframeOfficialRecordReadService {

    @Resource
    private WarframeOfficialImportHeadRepository heads;

    @Resource
    private OfficialRepositoryRegistry repositories;
    @Resource
    private OfficialTableRelations relations;

    /**
     * 读取当前已采纳的官方数据批次标识，尚无基线时返回空结果。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<String> currentBatchId() {
        return heads.findBatchId(IMPORT_HEAD_ID);
    }

    /**
     * 读取当前条件下的业务记录。
     *
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    @Override
    public List<WarframeOfficialRecordReadDTO> records() {
        return WarframeOfficialDataDomain.itemIndexCodes().stream()
                .flatMap(code -> repositories.require(WarframeOfficialDataDomain.fromCode(code)).findSummaries().stream())
                .filter(record -> !record.getBkey().equals(METADATA_BKEY))
                .map(record -> toRecord(record, null)).toList();
    }

    /**
     * 读取当前条件下的业务记录。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    @Override
    // 摘要选择和多级子表查询必须共享同一快照，避免审批提交时拼接两版数据。
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<WarframeOfficialRecordReadDTO> records(WarframeOfficialDataDomain domain) {
        List<WarframeOfficialRecordDTO> roots = repositories.require(domain).findSummaries().stream()
                .filter(record -> !record.getBkey().equals(METADATA_BKEY)).toList();
        Map<Long, OfficialRecordDTO> details = relations.load(domain.domainCode(),
                roots.stream().map(WarframeOfficialRecordDTO::getId).toList());
        return roots.stream().map(record -> toRecord(record, details.get(record.getId()))).toList();
    }

    /**
     * 按已在数据库筛选出的本地主键装载完整记录树。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param recordIds 已筛选的主记录标识。
     * @return 所选主记录及其同源完整详情树。
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<WarframeOfficialRecordReadDTO> recordsByIds(
            WarframeOfficialDataDomain domain,
            Collection<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) return List.of();
        List<WarframeOfficialRecordDTO> roots = repositories.require(domain).findSummariesByIds(recordIds).stream()
                .filter(record -> record.isEnabled() && !METADATA_BKEY.equals(record.getBkey()))
                .toList();
        Map<Long, OfficialRecordDTO> details = relations.load(domain.domainCode(),
                roots.stream().map(WarframeOfficialRecordDTO::getId).toList());
        Map<Long, WarframeOfficialRecordDTO> byId = new LinkedHashMap<>();
        roots.forEach(record -> byId.put(record.getId(), record));
        return new LinkedHashSet<>(recordIds).stream().map(byId::get).filter(Objects::nonNull)
                .map(record -> toRecord(record, details.get(record.getId()))).toList();
    }

    /**
     * 读取当前业务身份对应的记录。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param bkey 记录的稳定业务键；子表中表示直接父记录的主键。
     * @return 当前独立官方记录的摘要及类型化详情；没有匹配数据时返回空结果。
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Optional<WarframeOfficialRecordReadDTO> record(WarframeOfficialDataDomain domain, String bkey) {
        Optional<WarframeOfficialRecordDTO> selected = repositories.require(domain).findPreferredSummary(bkey);
        return selected.map(root -> toRecord(root, relations
                .load(domain.domainCode(), List.of(root.getId())).get(root.getId())));
    }

    /**
     * 读取已采纳的星系和节点资料，供引用解析与展示使用。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeOfficialRegionDTO> regions() {
        return records(WarframeOfficialDataDomain.REGION).stream().map(record -> {
            OfficialRegionDTO data = (OfficialRegionDTO) record.data();
            return new WarframeOfficialRegionDTO(record, data.getSystemName(), integer(data.getSystemIndex()),
                    integer(data.getNodeType()), integer(data.getMasteryReq()), integer(data.getMinEnemyLevel()),
                    integer(data.getMaxEnemyLevel()));
        }).toList();
    }

    /**
     * 可选整数精确转换，拒绝悄悄截断小数和超出范围的值。
     *
     * @param value 待校验的精确数值；缺失值保持为空。
     * @return 当前操作计算或读取的数值。
     */
    private static Integer integer(BigDecimal value) {
        return value == null ? null : value.intValueExact();
    }

    /**
     * 转换为记录。
     *
     * @param record 当前业务记录。
     * @param data 当前操作的数据。
     * @return 游戏资料官方记录读取对象。
     */
    private static WarframeOfficialRecordReadDTO toRecord(WarframeOfficialRecordDTO record, OfficialRecordDTO data) {
        return new WarframeOfficialRecordReadDTO(record.getId(), WarframeOfficialDataDomain.fromCode(record.getDomainCode()),
                record.getBkey(), record.getProductPath(), record.getDomainCode(),
                record.getOfficialName(), record.getLocalizedName(), record.getParentName(), record.getProductCategory(),
                record.getDescription(), record.getLongDescription(), record.getWikiSlug(), record.getMarketSlug(), (OfficialObjectDTO) data);
    }
}
