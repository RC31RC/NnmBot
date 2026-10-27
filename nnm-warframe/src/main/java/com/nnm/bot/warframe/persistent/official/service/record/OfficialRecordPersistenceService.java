package com.nnm.bot.warframe.persistent.official.service.record;

import com.nnm.bot.warframe.persistent.official.pojo.query.OfficialRecordIdentityQuery;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 多数据集导入与维护的事务协调；数据读写委托给各自的主表 Repository。 */
public interface OfficialRecordPersistenceService {
    /**
     * 创建当前主表的待写入记录。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @return 尚未持久化的当前主表实体。
     */
    OfficialRecord newRecord(String domain);

    /**
     * 显式保存当前业务记录。
     *
     * @param record 当前业务记录。
     * @return 官方记录实体对象。
     */
    OfficialRecord save(OfficialRecord record);

    /**
     * 显式保存当前主表实体，并立即将变更同步到数据库。
     *
     * @param record 当前业务记录。
     * @return 官方记录实体对象。
     */
    OfficialRecord saveAndFlush(OfficialRecord record);

    /**
     * 按本地主键读取业务记录。
     *
     * @param id 目标记录的本地主键。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<OfficialRecord> findById(long id);

    /**
     * 按本地主键集合批量读取业务记录。
     *
     * @param ids 目标记录标识集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<OfficialRecord> findAllById(Collection<Long> ids);

    /**
     * 读取符合身份和维护状态的待维护记录。
     *
     * @param query 当前业务的查询条件。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<OfficialRecord> findForMaintenance(OfficialRecordIdentityQuery query);

    /**
     * 将已保存的实体变更同步到数据库。
     */
    void flush();

    /**
     * 释放已经显式保存的受管对象，控制批量导入内存占用。
     *
     * @param records 待批量处理的业务记录。
     */
    void releaseWritten(Collection<? extends OfficialRecord> records);

    /**
     * 通过统一入口删除指定主记录及其全部必要子孙记录。
     *
     * @param ids 目标记录标识集合。
     */
    void deleteRecords(Collection<Long> ids);

    /**
     * 按独立主表合同同步本 JSON 的必要子孙表。
     *
     * @param records 待批量处理的业务记录。
     * @param removedIds 移除的标识集合。
     */
    void synchronizeTables(List<OfficialRecord> records, Collection<Long> removedIds);

    /**
     * 从具体关系表恢复非受管的逐文件详情 DTO。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param ids 目标记录标识集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    Map<Long, OfficialRecordDTO> loadDetails(String domain, Collection<Long> ids);

    /**
     * 按主键读取非受管业务投影。
     *
     * @param id 目标记录的本地主键。
     * @return 当前独立官方记录的非受管摘要；没有匹配数据时返回空结果。
     */
    Optional<WarframeOfficialRecordDTO> findDataById(long id);

    /**
     * 按主键集合批量读取非受管业务投影。
     *
     * @param ids 目标记录标识集合。
     * @return 当前独立官方记录的非受管摘要组成的列表。
     */
    List<WarframeOfficialRecordDTO> findDataByIds(Collection<Long> ids);

    /**
     * 计算已采纳记录的内容指纹，用于校验审批基线。
     *
     * @return 计算已采纳记录的内容指纹，用于校验审批基线对应的文本。
     */
    String contentFingerprint();

    /**
     * 读取上游维护记录的稳定业务身份。
     *
     * @param paths 待处理文件的路径集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    Map<String, Long> officialIdentities(Collection<String> paths);
}
