package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialApplyFileRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialBatchPurgeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 在单个数据库事务中清理官方批次的关联记录。 */
@Service
public class WarframeOfficialBatchPurgeServiceImpl implements WarframeOfficialBatchPurgeService {
    @Resource
    private WarframeOfficialApplyFileRepository applyFiles;
    @Resource
    private WarframeOfficialImportBatchRepository batches;

    /**
     * 删除指定批次的文件工作项和批次记录，并保证两类删除原子提交。
     *
     * @param batchId 待删除批次标识。
     */
    @Override
    @Transactional
    public void delete(String batchId) {
        applyFiles.deleteAllByBatchId(batchId);
        batches.deleteById(batchId);
    }
}
