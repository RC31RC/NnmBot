package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.IMPORT_HEAD_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.persistent.official.constant.OfficialApprovalErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ApplyFileStatus;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStatus;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFilteredChangesDTO;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest.FileEntry;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialRefreshStaging;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialApplyFileRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportHeadRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialRefreshStagingRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApplyStateService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialRetentionService;
import com.nnm.bot.warframe.storage.WarframeOfficialSnapshotStore;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.LockKey;
import io.github.rdt.lock.RdtLock;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 在元数据互斥范围回收已采纳原文；仅待审候选需要保留原件。 */
@Service
public class WarframeOfficialRetentionServiceImpl implements WarframeOfficialRetentionService {
    @Resource
    private WarframeOfficialImportProperties properties;
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private WarframeOfficialRefreshStagingRepository staging;
    @Resource
    private WarframeOfficialApplyFileRepository jobs;
    @Resource
    private WarframeOfficialApplyStateService state;
    @Resource
    private WarframeOfficialImportHeadRepository heads;
    @Resource
    private WarframeOfficialSnapshotStore objects;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private DistributedLockManager lockManager;

    /**
     * 在集群元数据锁内清理过期预览和不再引用的快照。
     */
    @Override
    @RdtLock
    public void cleanupExpired() {
        lockManager.lock(LockKey.named(OfficialDataConstants.class, OfficialDataConstants.LockPurpose.METADATA.key()));
        cleanupExpiredLocked();
    }

    /**
     * 为后续拉取和审核准备官方数据刷新任务。
     */
    public void prepareRefresh() {
        cleanupExpiredLocked();
        long pending = batches.findAll().stream().filter(batch -> ImportStatus.PENDING.name().equals(batch.getStatus())
            && batch.getExpiresAt().isAfter(Instant.now())).count();
        BusinessAssert.isTrue(pending < properties.getMaxPendingPreviews(),
            WarframeManagementErrorCode.DATASET_PREVIEW_CAPACITY);
    }

    private void cleanupExpiredLocked() {
        Instant now = Instant.now();
        // 先锁住失败批次完成过期状态迁移；重试会锁同一行，避免回收与重试同时发生。
        for (WarframeOfficialImportBatch batch : batches.findAll()) {
            if (ImportStatus.PARTIAL_FAILED.name().equals(batch.getStatus())
                && (batch.getRetryExpiresAt() == null || !batch.getRetryExpiresAt().isAfter(now)))
                state.expireFailure(batch.getId());
        }
        String head = heads.findById(IMPORT_HEAD_ID).map(value -> value.getBatchId()).orElse(null);
        List<WarframeOfficialImportBatch> all = batches.findAll();
        Set<String> referenced = referencedFiles(all, staging.findAll(), null, Instant.now());
        for (WarframeOfficialImportBatch batch : all) {
            String status = batch.getStatus();
            if (ImportStatus.PENDING.name().equals(status) && batch.getExpiresAt().isAfter(now))
                continue;
            if (ImportStatus.APPLYING.name().equals(status) || ImportStatus.PARTIAL_FAILED.name().equals(status)) {
                if (batch.getApplyPlanJson() != null) {
                    Set<String> unfinished = unfinishedPaths(batch);
                    objects.deleteUnusedChangeGroups(batch.getChangeObjectKey(), unfinished);
                    objects.deleteUnusedChangeGroups(planKey(batch), unfinished);
                }
                deleteUnreferencedFiles(batch, referenced);
                continue;
            }
            if (ImportStatus.APPLIED.name().equals(status) && batch.getId().equals(head)) {
                objects.deleteAbandonedChanges(batch.getId());
                deleteUnreferencedFiles(batch, referenced);
                continue;
            }
            boolean failedAudit = ImportStatus.EXPIRED.name().equals(status) && batch.getRetryExpiresAt() != null;
            if (failedAudit && batch.getObjectsReleasedAt() != null)
                continue;
            if (ImportStatus.PENDING.name().equals(status))
                batch.setStatus(ImportStatus.EXPIRED.name());
            else if (ImportStatus.APPLIED.name().equals(status))
                batch.setStatus(ImportStatus.ARCHIVED.name());
            batches.save(batch);
            deleteUnreferencedFiles(batch, referenced);
            // 本批次的原始和过滤后差异都使用独占前缀；异常时下一轮继续回收。
            objects.deleteAbandonedChanges(batch.getId());
            if (failedAudit) {
                batch.setObjectsReleasedAt(Instant.now());
                batches.save(batch);
            } else {
                batches.deleteById(batch.getId());
            }
        }
        removeAbandonedStaging(now.minus(properties.getStaleStagingTtl()));
    }

    /**
     * 逐文件回收未被审核、执行或上传引用的原件。
     *
     * @param batch 待清理批次。
     * @param referenced 跨批次及上传仍需保留的文件身份。
     */
    private void deleteUnreferencedFiles(WarframeOfficialImportBatch batch, Set<String> referenced) {
        for (FileEntry file : batchFiles(batch)) {
            if (!referenced.contains(identity(file)))
                objects.delete(file);
        }
    }

    /**
     * 冻结后只保留非 DONE 工作项所需原件；未选中的差异须重新拉取。
     *
     * @param batch 正在采纳或部分失败的批次。
     * @return 后续还会恢复原件和读取差异的文件路径。
     */
    private Set<String> unfinishedPaths(WarframeOfficialImportBatch batch) {
        Set<String> paths = new HashSet<>();
        jobs.findAllByBatchId(batch.getId()).stream()
            .filter(job -> !ApplyFileStatus.DONE.name().equals(job.getStatus()))
            .forEach(job -> paths.add(job.getProductPath()));
        return paths;
    }

    /**
     * 取得本批次过滤后的独占差异对象键。
     *
     * @param batch 已完成差异准备的批次。
     * @return 过滤后差异的对象前缀。
     */
    private String planKey(WarframeOfficialImportBatch batch) {
        try {
            return Objects.requireNonNull(
                objectMapper.readValue(batch.getApplyPlanJson(), OfficialFilteredChangesDTO.class).objectKey());
        } catch (JsonProcessingException | NullPointerException exception) {
            throw new SystemException(OfficialApprovalErrorCode.SAVED_SNAPSHOT_CORRUPTED,
                "官方审批差异计划无法读取: " + batch.getId(), exception);
        }
    }

    /**
     * 统一计算正在使用的文件身份，供定时清理及失败上传回收共同使用。
     *
     * @param all 全部批次。
     * @param uploads 仍在上传的账本。
     * @param excludedStaging 当前需要丢弃的上传账本标识。
     * @param now 当前时间。
     * @return 全局仍需保留的原件身份。
     */
    private Set<String> referencedFiles(List<WarframeOfficialImportBatch> all,
        List<WarframeOfficialRefreshStaging> uploads, String excludedStaging, Instant now) {
        Set<String> referenced = new HashSet<>();
        for (WarframeOfficialImportBatch batch : all) {
            Set<String> needed;
            if (ImportStatus.PENDING.name().equals(batch.getStatus()) && batch.getExpiresAt().isAfter(now)
                || ImportStatus.APPLYING.name().equals(batch.getStatus()) && batch.getApplyPlanJson() == null
                || ImportStatus.PARTIAL_FAILED.name().equals(batch.getStatus()) && batch.getApplyPlanJson() == null)
                needed = batchFiles(batch).stream().map(FileEntry::path).collect(Collectors.toSet());
            else if (ImportStatus.APPLYING.name().equals(batch.getStatus())
                || ImportStatus.PARTIAL_FAILED.name().equals(batch.getStatus()))
                needed = unfinishedPaths(batch);
            else
                continue;
            batchFiles(batch).stream().filter(file -> needed.contains(file.path()))
                .forEach(file -> referenced.add(identity(file)));
        }
        uploads.stream().filter(work -> !work.getId().equals(excludedStaging))
            .forEach(work -> stagedFiles(work).forEach(file -> referenced.add(identity(file))));
        return referenced;
    }

    /**
     * 清理刷新失败留下且未被有效批次引用的冻结文件。
     *
     * @param failed 失败。
     */
    public void discardUnreferenced(WarframeOfficialManifest failed, Set<String> selectedPaths, String stagingId) {
        Set<String> referenced = referencedFiles(batches.findAll(), staging.findAll(), stagingId, Instant.now());
        for (FileEntry file : failed.files().stream().filter(file -> selectedPaths.contains(file.path())).toList()) {
            if (!referenced.contains(identity(file)))
                objects.delete(file);
        }
    }

    /**
     * 仅归档数据库状态；不能在全局准入锁内删除 MinIO 对象。
     *
     * @param batchId 待丢弃的审核批次标识。
     */
    @Override
    @Transactional
    public void archiveBatch(String batchId) {
        archiveBatches(List.of(batchId));
    }

    /**
     * 一笔短事务归档待审批次，耗时对象清理由定时任务完成。
     *
     * @param batchIds 本次重启涉及的预览批次标识。
     */
    @Override
    @Transactional
    public void archiveBatches(List<String> batchIds) {
        if (batchIds.isEmpty())
            return;
        heads.findFirstById(IMPORT_HEAD_ID).orElseThrow(
            () -> new SystemException(OfficialApprovalErrorCode.APPLY_STATE_INCONSISTENT, "官方数据指针未初始化，请更新 init.sql"));
        List<WarframeOfficialImportBatch> toArchive = batches.findAllById(batchIds);
        BusinessAssert.isTrue(
            toArchive.size() == batchIds.size() && toArchive.stream()
                .allMatch(batch -> (ImportStatus.PENDING.name().equals(batch.getStatus())
                    || ImportStatus.PARTIAL_FAILED.name().equals(batch.getStatus()))),
            WarframeManagementErrorCode.DATASET_PREVIEW_NOT_FOUND);
        toArchive.forEach(batch -> batch.setStatus(ImportStatus.ARCHIVED.name()));
        batches.saveAllAndFlush(toArchive);
    }

    /**
     * 仅从上传账本中提取该次尝试引用的候选文件。
     *
     * @param work 尚未完成的上传账本。
     * @return 本次尝试选定的文件。
     */
    private List<FileEntry> stagedFiles(WarframeOfficialRefreshStaging work) {
        try {
            return objectMapper.readValue(work.getManifestJson(), WarframeOfficialManifest.class).files().stream()
                .filter(file -> work.getProductPath() == null || work.getProductPath().equals(file.path())).toList();
        } catch (JsonProcessingException exception) {
            throw new SystemException(OfficialApprovalErrorCode.SAVED_SNAPSHOT_CORRUPTED,
                "暂存官方数据清单无法读取: " + work.getId(), exception);
        }
    }

    private List<FileEntry> batchFiles(WarframeOfficialImportBatch batch) {
        return manifest(batch).files().stream()
            .filter(file -> batch.getProductPath() == null || batch.getProductPath().equals(file.path())).toList();
    }

    /**
     * 读取并校验官方数据发布清单。
     *
     * @param batch 批次。
     * @return 游戏资料数据文件发布清单对象。
     */
    private WarframeOfficialManifest manifest(WarframeOfficialImportBatch batch) {
        try {
            return objectMapper.readValue(batch.getManifestJson(), WarframeOfficialManifest.class);
        } catch (JsonProcessingException exception) {
            throw new SystemException(OfficialApprovalErrorCode.SAVED_SNAPSHOT_CORRUPTED,
                "已保存的官方数据清单无法读取: " + batch.getId(), exception);
        }
    }

    /**
     * 构造不依赖数组位置的稳定业务身份。
     *
     * @param file 本次上传或读取的文件。
     * @return 构造不依赖数组位置的稳定业务身份对应的文本。
     */
    private static String identity(FileEntry file) {
        return file.sha256() + "/" + file.path();
    }

    /**
     * 清理不再被有效审核批次引用的暂存数据。
     *
     * @param cutoff 截止时间。
     */
    private void removeAbandonedStaging(Instant cutoff) {
        Path directory = properties.getStagingDirectory();
        if (!Files.isDirectory(directory))
            return;
        try (Stream<Path> entries = Files.list(directory)) {
            for (Path entry : entries.toList()) {
                String name = entry.getFileName().toString();
                if ((!name.startsWith("snapshot-") && !name.startsWith("approval-")) || !Files.isDirectory(entry)
                    || !Files.getLastModifiedTime(entry).toInstant().isBefore(cutoff))
                    continue;
                try (Stream<Path> paths = Files.walk(entry)) {
                    for (Path path : paths.sorted(Comparator.reverseOrder()).toList())
                        Files.delete(path);
                }
            }
        } catch (IOException exception) {
            throw new SystemException(CommonErrorCode.RESOURCE_CLEANUP_FAILED, "清理过期官方数据暂存失败", exception);
        }
    }
}
