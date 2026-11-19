package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFileDigestDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApprovalService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialPreviewPublicationService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 对象存储完成后才进入此短事务，不把网络和差异计算包进数据库事务。 */
@Service
public class WarframeOfficialPreviewPublicationServiceImpl implements WarframeOfficialPreviewPublicationService {
    @Resource
    private WarframeOfficialApprovalService approval;
    @Resource
    private WarframeOfficialImportBatchRepository batches;

    /**
     * 在短数据库事务内登记候选文件并发布预览批次。
     *
     * @param batch 已写入对象存储的待审核批次。
     * @param files 本批次官方文件摘要。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(WarframeOfficialImportBatch batch, List<OfficialFileDigestDTO> files) {
        approval.stageCandidates(batch.getSourceRef(), batch.getCommitSha(), batch.getBuildId(), files);
        batches.saveAndFlush(batch);
    }
}
