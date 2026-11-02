package com.nnm.bot.warframe.persistent.official.service.dataset;

import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import java.util.Set;

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
     * @param failed 失败。
     */
    void discardUnreferenced(WarframeOfficialManifest failed, Set<String> selectedPaths);

    /**
     * 丢弃批次并回收不再被其他批次或生效文件引用的快照。
     *
     * @param batch 待丢弃的审核批次。
     */
    void discardBatch(WarframeOfficialImportBatch batch);
}
