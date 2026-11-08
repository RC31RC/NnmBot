package com.nnm.bot.warframe.persistent.official.service.dataset;

import java.time.Instant;

/** 在文件锁与数据库基线锁下安全回收中断的采纳。 */
public interface WarframeOfficialApprovalRecoveryService {
    /**
     * 回收没有发布预览的陈旧上传账本及其冻结对象。
     *
     * @param stagingId 上传账本标识。
     */
    void recoverStaging(String stagingId);
}
