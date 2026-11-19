package com.nnm.bot.warframe.persistent.official.service.dataset;

/** 原子清理已归档官方批次及其文件审批工作项。 */
public interface WarframeOfficialBatchPurgeService {
    /**
     * 删除批次及其所有文件审批工作项。
     *
     * @param batchId 待删除批次标识。
     */
    void delete(String batchId);
}
