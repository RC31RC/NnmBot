package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.warframe.persistent.official.constant.OfficialApprovalErrorCode;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStatus;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialApplyDTO;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApplyPreparationService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApplyStateService;
import com.nnm.bot.warframe.storage.WarframeOfficialSnapshotStore;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.LockKey;
import io.github.rdt.lock.RdtLock;
import jakarta.annotation.Resource;
import java.nio.file.Files;
import org.springframework.stereotype.Service;

/** 准备阶段访问对象存储，不在数据库事务里执行网络 IO。 */
@Service
public class WarframeOfficialApplyPreparationServiceImpl implements WarframeOfficialApplyPreparationService {
    @Resource
    private DistributedLockManager locks;
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private WarframeOfficialApplyStateService state;
    @Resource
    private WarframeOfficialSnapshotStore objects;
    @Resource
    private WarframeOfficialImportProperties properties;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    @RdtLock
    public void prepare(String batchId) {
        locks.lock(LockKey.forOperation(OfficialDataConstants.class,
            OfficialDataConstants.LockPurpose.PREPARATION.key(), batchId));
        var batch = batches.findById(batchId).orElse(null);
        if (batch == null || !ImportStatus.APPLYING.name().equals(batch.getStatus())
            || batch.getApplyPlanJson() != null)
            return;
        String nextKey = WarframeOfficialSnapshotStore.nextChangesKey(batchId);
        try {
            Files.createDirectories(properties.getStagingDirectory());
            var selection = objectMapper.readValue(batch.getApplySelectionJson(), WarframeOfficialApplyDTO.class);
            var plan =
                objects.filterChanges(batch.getChangeObjectKey(), nextKey, selection, properties.getStagingDirectory());
            state.prepared(batchId, plan);
        } catch (IllegalArgumentException | BusinessException exception) {
            try {
                objects.deleteChanges(nextKey);
            } catch (RuntimeException cleanup) {
                exception.addSuppressed(cleanup);
            }
            state.failPreparation(batchId, exception);
        } catch (Exception exception) {
            // 数据库提交结果未知时只在确认批次没有引用新对象后才回收。
            var current = batches.findById(batchId).orElse(null);
            if (current == null || current.getApplyPlanJson() == null)
                objects.deleteChanges(nextKey);
            throw new SystemException(OfficialApprovalErrorCode.PREPARATION_RETRYABLE, "官方审批准备未完成，可重新认领", exception);
        }
    }
}
