package com.nnm.bot.warframe.persistent.official.service.dataset;

import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFileDigestDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import java.util.List;

/** 将候选文件状态和可审核批次在同一数据库事务中发布。 */
public interface WarframeOfficialPreviewPublicationService {
    /**
     * 发布已冻结的审核预览；失败时两类元数据一并回滚。
     *
     * @param batch 已完成对象存储写入的待审批次。
     * @param files 本轮冻结的官方文件。
     */
    void publish(WarframeOfficialImportBatch batch, List<OfficialFileDigestDTO> files);
}
