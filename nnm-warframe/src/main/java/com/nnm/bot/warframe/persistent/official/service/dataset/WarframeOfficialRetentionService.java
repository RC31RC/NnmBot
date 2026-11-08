package com.nnm.bot.warframe.persistent.official.service.dataset;

import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import java.util.Set;
import java.util.List;

/** 协调批次保留和引用完整性，文件读写委托给官方对象存储。 */
public interface WarframeOfficialRetentionService {
    /**
     * 清理过期预览、未引用快照和陈旧本地暂存目录。
     */
    void cleanupExpired();

    /**
     * 为后续拉取和审核准备官方数据刷新任务。
     */
    void prepareRefresh();

    /**
     * 清理刷新失败留下且未被有效批次引用的冻结文件。
     *
     * @param failed 失败任务的完整清单。
     * @param selectedPaths 该次尝试实际上传的路径。
     * @param stagingId 本次尝试的账本标识；其他上传账本引用仍须保留。
     */
    void discardUnreferenced(WarframeOfficialManifest failed, Set<String> selectedPaths, String stagingId);

    /**
     * 短事务归档待审批次，对象存储清理由定时任务异步完成。
     *
     * @param batchId 待丢弃的审核批次标识。
     */
    void archiveBatch(String batchId);

    /**
     * 一笔短事务归档本次刷新范围内的预览，不在准入锁内逐份回收对象。
     *
     * @param batchIds 待丢弃的审核批次标识。
     */
    void archiveBatches(List<String> batchIds);
}
