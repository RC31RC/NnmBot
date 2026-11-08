package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.warframe.persistent.official.constant.OfficialApprovalErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStatus;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialRefreshStaging;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialRefreshStagingRepository;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialRetentionService;
import com.nnm.bot.warframe.storage.WarframeOfficialSnapshotStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Set;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApprovalRecoveryService;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.LockKey;
import io.github.rdt.lock.RdtLock;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** 按采纳时相同的文件锁 → 元数据锁 → head 行锁顺序恢复，而非信任 Redis 任务 TTL。 */
@Service
public class WarframeOfficialApprovalRecoveryServiceImpl implements WarframeOfficialApprovalRecoveryService {
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private WarframeOfficialSnapshotParser parser;
    @Resource
    private DistributedLockManager locks;
    @Resource
    private WarframeOfficialRefreshStagingRepository staging;
    @Resource
    private WarframeOfficialRetentionService retention;
    @Resource
    private WarframeOfficialSnapshotStore objects;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeOfficialImportProperties properties;

    @Override
    @RdtLock
    public void recoverStaging(String stagingId) {
        WarframeOfficialRefreshStaging work = staging.findById(stagingId).orElse(null);
        if (work == null || !work.getCreatedAt().isBefore(Instant.now().minus(properties.getStaleStagingTtl())))
            return;
        List<String> paths = work.getProductPath() == null ? parser.supportedPaths() : List.of(work.getProductPath());
        lockPaths(paths);
        locks.lock(LockKey.named(OfficialDataConstants.class, OfficialDataConstants.LockPurpose.METADATA.key()));
        if (batches.existsById(stagingId)) {
            // 上传成功但清除账本前进程中断，预览对象仍被批次引用。
            staging.deleteById(stagingId);
            return;
        }
        WarframeOfficialManifest manifest;
        try {
            manifest = objectMapper.readValue(work.getManifestJson(), WarframeOfficialManifest.class);
        } catch (JsonProcessingException exception) {
            throw new SystemException(OfficialApprovalErrorCode.SAVED_SNAPSHOT_CORRUPTED, "官方上传暂存清单无法恢复: " + stagingId,
                exception);
        }
        retention.discardUnreferenced(manifest, Set.copyOf(paths), stagingId);
        objects.deleteAbandonedChanges(stagingId);
        staging.deleteById(stagingId);
    }

    /**
     * 按审批时相同顺序锁住整个数据集范围。
     *
     * @param paths 被采纳或上传的文件路径。
     */
    private void lockPaths(List<String> paths) {
        locks.lockAll(paths.stream().map(path -> LockKey.forOperation(OfficialDataConstants.class,
            OfficialDataConstants.LockPurpose.DATASET.key(), path)).toList());
    }
}
