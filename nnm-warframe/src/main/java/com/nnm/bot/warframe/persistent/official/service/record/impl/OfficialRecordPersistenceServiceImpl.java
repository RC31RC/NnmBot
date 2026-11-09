package com.nnm.bot.warframe.persistent.official.service.record.impl;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.pojo.query.OfficialRecordIdentityQuery;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialMainRepository;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialRepositoryRegistry;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialTableRelations;
import com.nnm.bot.warframe.persistent.official.service.record.OfficialRecordPersistenceService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfficialRecordPersistenceServiceImpl implements OfficialRecordPersistenceService {
    @Resource
    private OfficialRepositoryRegistry repositories;
    @Resource
    private OfficialTableRelations relations;

    /**
     * 取得当前独立数据文件或人工资料类型对应的 Repository。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @return 官方主表数据访问对象。
     */
    private OfficialMainRepository<?> repository(String domain) {
        return repositories.require(WarframeOfficialDataDomain.fromCode(domain));
    }

    /**
     * 创建当前主表的待写入记录。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @return 尚未持久化的当前主表实体。
     */
    @Override
    public OfficialRecord newRecord(String domain) {
        return repository(domain).newRecord();
    }

    /**
     * 显式保存当前业务记录。
     *
     * @param record 当前业务记录。
     * @return 官方记录实体对象。
     */
    @Override
    @Transactional
    public OfficialRecord save(OfficialRecord record) {
        return repositories.require(record).saveRecord(record);
    }

    /**
     * 显式保存当前主表实体，并立即将变更同步到数据库。
     *
     * @param record 当前业务记录。
     * @return 官方记录实体对象。
     */
    @Override
    @Transactional
    public OfficialRecord saveAndFlush(OfficialRecord record) {
        OfficialRecord saved = save(record);
        repositories.require(record).flush();
        return saved;
    }

    /**
     * 按本地主键读取业务记录。
     *
     * @param id 目标记录的本地主键。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<OfficialRecord> findById(long id) {
        // 管理端记录 ID 在各数据集间唯一；普通命令按已知数据集直达对应 Repository。
        for (OfficialMainRepository<?> repository : repositories.all()) {
            Optional<? extends OfficialRecord> found = repository.findById(id);
            if (found.isPresent())
                return Optional.of(found.get());
        }
        return Optional.empty();
    }

    /**
     * 按本地主键集合批量读取业务记录。
     *
     * @param ids 目标记录标识集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<OfficialRecord> findAllById(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids))
            return List.of();
        Collection<Long> remaining = new LinkedHashSet<>(ids);
        List<OfficialRecord> result = new ArrayList<>();
        for (OfficialMainRepository<?> repository : repositories.all()) {
            List<? extends OfficialRecord> found = repository.findAllById(remaining);
            result.addAll(found);
            found.forEach(record -> remaining.remove(record.getId()));
            if (CollUtil.isEmpty(remaining))
                break;
        }
        return result;
    }

    /**
     * 读取符合身份和维护状态的待维护记录。
     *
     * @param query 当前业务的查询条件。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<OfficialRecord> findForMaintenance(OfficialRecordIdentityQuery query) {
        return repository(query.domain()).findByMaintenanceTypeAndBkeyIgnoreCase(query.maintenanceType(), query.bkey())
            .map(record -> record);
    }

    /**
     * 将已保存的实体变更同步到数据库。
     */
    @Override
    @Transactional
    public void flush() {
        relations.flush();
    }

    /**
     * 释放已经显式保存的受管对象，控制批量导入内存占用。
     *
     * @param records 待批量处理的业务记录。
     */
    @Override
    public void releaseWritten(Collection<? extends OfficialRecord> records) {
        relations.releaseWritten(records);
    }

    /**
     * 通过统一入口删除指定主记录及其全部必要子孙记录。
     *
     * @param ids 目标记录标识集合。
     */
    @Override
    @Transactional
    public void deleteRecords(Collection<Long> ids) {
        Map<String, List<Long>> grouped = new LinkedHashMap<>();
        findDataByIds(ids).forEach(record -> grouped
            .computeIfAbsent(record.getDomainCode(), ignored -> new ArrayList<>()).add(record.getId()));
        grouped.forEach((domain, selected) -> {
            relations.deleteChildren(domain, selected);
            repository(domain).deleteAllByIdInBatch(selected);
        });
    }

    /**
     * 按独立主表合同同步本 JSON 的必要子孙表。
     *
     * @param records 待批量处理的业务记录。
     * @param inputs 按主键索引的类型化输入。
     * @param removedIds 移除的标识集合。
     */
    @Override
    @Transactional
    public void synchronizeTables(List<OfficialRecord> records, Map<Long, OfficialRecordDTO> inputs,
        Collection<Long> removedIds) {
        if (CollUtil.isNotEmpty(removedIds))
            deleteRecords(removedIds);
        relations.replace(records, inputs);
        // 固定源字段回写主表后显式 save；不依赖 Hibernate 脏检查隐式写入。
        records.forEach(this::save);
    }

    /**
     * 从具体关系表恢复非受管的逐文件详情 DTO。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param ids 目标记录标识集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    @Override
    public Map<Long, OfficialRecordDTO> loadDetails(String domain, Collection<Long> ids) {
        return relations.load(domain, ids);
    }

    /**
     * 按主键读取非受管业务投影。
     *
     * @param id 目标记录的本地主键。
     * @return 当前独立官方记录的非受管摘要；没有匹配数据时返回空结果。
     */
    @Override
    public Optional<WarframeOfficialRecordDTO> findDataById(long id) {
        for (OfficialMainRepository<?> repository : repositories.all()) {
            Optional<WarframeOfficialRecordDTO> found = repository.findDataById(id);
            if (found.isPresent())
                return found;
        }
        return Optional.empty();
    }

    /**
     * 按主键集合批量读取非受管业务投影。
     *
     * @param ids 目标记录标识集合。
     * @return 当前独立官方记录的非受管摘要组成的列表。
     */
    @Override
    public List<WarframeOfficialRecordDTO> findDataByIds(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids))
            return List.of();
        Collection<Long> remaining = new LinkedHashSet<>(ids);
        List<WarframeOfficialRecordDTO> result = new ArrayList<>();
        for (OfficialMainRepository<?> repository : repositories.all()) {
            List<WarframeOfficialRecordDTO> found = repository.findDataByIds(remaining);
            result.addAll(found);
            found.forEach(record -> remaining.remove(record.getId()));
            if (CollUtil.isEmpty(remaining))
                break;
        }
        return result;
    }

    /**
     * 读取上游维护记录的稳定业务身份。
     *
     * @param paths 待处理文件的路径集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    @Override
    public Map<String, Long> officialIdentities(Collection<String> paths) {
        Map<String, Long> identities = new LinkedHashMap<>();
        for (OfficialMainRepository<?> repository : repositories.all()) {
            if (!paths.contains(repository.domain().productPath()))
                continue;
            // 闭合标量投影，不加载主表实体或完整源文本。
            repository.findAllProjectedByMaintenanceType(MaintenanceType.OFFICIAL)
                .forEach(row -> identities.put(repository.domain().domainCode() + ":" + row.getBkey(), row.getId()));
        }
        return identities;
    }

    /**
     * 根据明确领域读取主键，不扫描其他 JSON 主表。
     *
     * @param domain 独立领域。
     * @param id 主记录标识。
     * @return 当前表中的实体。
     */
    @Override
    public Optional<OfficialRecord> findById(WarframeOfficialDataDomain domain, long id) {
        return repositories.require(domain).findById(id).map(record -> record);
    }

    /**
     * 根据明确领域读取摘要，不扫描其他 JSON 主表。
     *
     * @param domain 独立领域。
     * @param id 主记录标识。
     * @return 当前表中的非受管投影。
     */
    @Override
    public Optional<WarframeOfficialRecordDTO> findDataById(WarframeOfficialDataDomain domain, long id) {
        return repositories.require(domain).findDataById(id);
    }

    /**
     * 只删除指定独立表的根记录及其子孙。
     *
     * @param domain 独立领域。
     * @param ids 根标识集合。
     */
    @Override
    @Transactional
    public void deleteRecords(WarframeOfficialDataDomain domain, Collection<Long> ids) {
        if (ids.isEmpty())
            return;
        relations.deleteChildren(domain.domainCode(), ids);
        repositories.require(domain).deleteAllByIdInBatch(ids);
    }
}
