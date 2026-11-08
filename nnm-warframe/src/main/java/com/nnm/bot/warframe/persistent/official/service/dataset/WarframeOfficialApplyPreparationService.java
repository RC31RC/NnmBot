package com.nnm.bot.warframe.persistent.official.service.dataset;

/** 恢复一次冻结选择的对象存储差异；跨实例仅一个准备者。 */
public interface WarframeOfficialApplyPreparationService {
    /**
     * 验证冻结选择并生成未选差异，成功后开放数据库工作项认领。
     *
     * @param batchId 待准备的审批批次标识。
     */
    void prepare(String batchId);
}
