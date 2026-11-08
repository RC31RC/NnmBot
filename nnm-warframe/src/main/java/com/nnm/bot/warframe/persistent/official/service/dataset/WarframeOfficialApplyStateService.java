package com.nnm.bot.warframe.persistent.official.service.dataset;

import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFilteredChangesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialApplyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialApplyFile;
import java.util.Optional;
import java.util.UUID;

/** 文件工作项的独立短事务，避免对象存储操作占用数据库事务。 */
public interface WarframeOfficialApplyStateService {
    /**
     * 冻结审批选择，拒绝对同一批次重复提交或与其他审批并发。
     *
     * @param batchId 待审批次标识。
     * @param selection 管理员选定的分类及记录。
     */
    void freeze(String batchId, WarframeOfficialApplyDTO selection);

    /**
     * 检查冻结基线并生成逐文件工作项。
     *
     * @param batchId 已冻结的批次标识。
     * @param plan 对象存储生成的剩余差异计划。
     */
    void prepared(String batchId, OfficialFilteredChangesDTO plan);

    /**
     * 跳过其他实例锁住的文件，认领一个可执行工作项。
     *
     * @return 本实例认领的文件；没有可认领文件时为空。
     */
    Optional<WarframeOfficialApplyFile> claim();

    /**
     * 记录当前持有者的文件执行失败，并按配置决定是否重试。
     *
     * @param jobId 文件工作项标识。
     * @param owner 本次认领的所有者令牌。
     * @param error 本次失败原因。
     */
    void fail(UUID jobId, UUID owner, Exception error);

    /**
     * 标记无法完成差异准备的批次，供管理员查看或重试。
     *
     * @param batchId 批次标识。
     * @param error 准备失败原因。
     */
    void failPreparation(String batchId, Exception error);

    /**
     * 在所有文件成功或失败后提交批次终态。
     *
     * @param batchId 批次标识。
     */
    void finish(String batchId);

    /**
     * 重置失败文件的认领状态，不重置已提交的分类。
     *
     * @param batchId 同一次审批的批次标识。
     */
    void retry(String batchId);

    /**
     * 原子检查失败原件的保留期限并标记过期，与人工重试互斥。
     *
     * @param batchId 失败的批次标识。
     */
    void expireFailure(String batchId);
}
