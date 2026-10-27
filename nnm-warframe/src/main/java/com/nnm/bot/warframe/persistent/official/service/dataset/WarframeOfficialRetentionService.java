package com.nnm.bot.warframe.persistent.official.service.dataset;

import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;

/** 协调批次保留和引用完整性，文件读写委托给官方对象存储。 */
public interface WarframeOfficialRetentionService {
    /**
     * 为后续拉取和审核准备官方数据刷新任务。
     */
    void prepareRefresh();

    /**
     * 清理刷新失败留下且未被有效批次引用的冻结文件。
     * @param failed 失败。
     */
    void discardUnreferenced(WarframeOfficialManifest failed);
}
