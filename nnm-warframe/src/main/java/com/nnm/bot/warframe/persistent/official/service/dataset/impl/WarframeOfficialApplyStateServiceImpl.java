package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.IMPORT_HEAD_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.warframe.persistent.official.constant.OfficialApprovalErrorCode;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ApplyFileStatus;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ChangeType;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStatus;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFilteredChangesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialApplyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialFileCategoryDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialPreviewDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialApplyFile;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialApplyFileRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialFileRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportHeadRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApplyStateService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApprovalService;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

/** 审批、认领与终态只写短事务；分类入库事务由审批服务单独管理。 */
@Service
public class WarframeOfficialApplyStateServiceImpl implements WarframeOfficialApplyStateService {
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private WarframeOfficialApplyFileRepository jobs;
    @Resource
    private WarframeOfficialFileRepository datasetFiles;
    @Resource
    private WarframeOfficialImportHeadRepository heads;
    @Resource
    private WarframeOfficialSnapshotParser parser;
    @Resource
    private WarframeOfficialImportProperties properties;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public void freeze(String batchId, WarframeOfficialApplyDTO selection) {
        var head = heads.findFirstById(IMPORT_HEAD_ID).orElseThrow();
        BusinessAssert.isTrue(!batches.existsByStatus(ImportStatus.APPLYING.name()),
            WarframeManagementErrorCode.DATASET_REFRESH_BLOCKED, "另一批官方预览正在采纳");
        WarframeOfficialImportBatch batch = batches.lockById(batchId).orElse(null);
        BusinessAssert.isTrue(batch != null && ImportStatus.PENDING.name().equals(batch.getStatus())
            && batch.getExpiresAt().isAfter(Instant.now()), WarframeManagementErrorCode.DATASET_PREVIEW_NOT_FOUND);
        BusinessAssert.isTrue(selection != null, WarframeManagementErrorCode.DATASET_INVALID);
        BusinessAssert.isTrue(
            batch.getParserContractVersion() == WarframeOfficialSnapshotParser.CONTRACT_VERSION
                && (batch.getProductPath() != null || Objects.equals(head.getBatchId(), batch.getBaselineId())),
            WarframeManagementErrorCode.DATASET_PREVIEW_CONFLICT);
        batch.setApplySelectionJson(write(selection));
        batch.setStatus(ImportStatus.APPLYING.name());
        batch.setApplyingAt(Instant.now());
        batches.saveAndFlush(batch);
    }

    private static boolean selected(WarframeOfficialApplyDTO selection, String path, ChangeType type) {
        return selection.allGroups().contains(new WarframeOfficialFileCategoryDTO(path, type.name())) || selection
            .changes().stream().anyMatch(item -> path.equals(item.path()) && type.name().equals(item.changeType()));
    }

    @Override
    @Transactional
    public void prepared(String batchId, OfficialFilteredChangesDTO plan) {
        // 冻结选择只占短准入锁；分类事务按冻结差异独立提交。
        var head = heads.findFirstById(IMPORT_HEAD_ID).orElseThrow();
        WarframeOfficialImportBatch batch = batches.lockById(batchId).orElseThrow();
        if (!ImportStatus.APPLYING.name().equals(batch.getStatus()) || batch.getApplyPlanJson() != null)
            return;
        BusinessAssert.isTrue(
            batch.getParserContractVersion() == WarframeOfficialSnapshotParser.CONTRACT_VERSION
                && (batch.getProductPath() != null || Objects.equals(head.getBatchId(), batch.getBaselineId())),
            WarframeManagementErrorCode.DATASET_PREVIEW_CONFLICT);
        var selection = read(batch.getApplySelectionJson(), WarframeOfficialApplyDTO.class);
        WarframeOfficialManifest manifest = read(batch.getManifestJson(), WarframeOfficialManifest.class);
        var files = parser.parseFileDigests(manifest).stream()
            .filter(file -> batch.getProductPath() == null || file.path().equals(batch.getProductPath())).toList();
        Set<String> paths = files.stream().map(file -> file.path()).collect(Collectors.toSet());
        BusinessAssert.isTrue(
            selection.changes().stream().allMatch(item -> item != null && paths.contains(item.path()))
                && selection.excluded().stream().allMatch(item -> item != null && paths.contains(item.path()))
                && selection.allGroups().stream().allMatch(item -> item != null && paths.contains(item.path())),
            WarframeManagementErrorCode.DATASET_INVALID, "选择中包含不属于此预览的文件");
        // 未选中的文件差异不生成工作项；已采纳 SHA 与候选相同的零差异文件也不重复入库。
        Map<String, WarframeOfficialPreviewDTO.FileChangeDTO> previews =
            read(batch.getPreviewJson(), WarframeOfficialPreviewDTO.class).files().stream()
                .collect(Collectors.toMap(WarframeOfficialPreviewDTO.FileChangeDTO::path, item -> item));
        // 把将写入的文件行先锁住，避免比较原预览后、记录各文件基线前人工写入。
        files.stream().filter(file -> {
            var preview = previews.get(file.path());
            return selected(selection, file.path(), ChangeType.ADDED)
                || selected(selection, file.path(), ChangeType.CHANGED)
                || selected(selection, file.path(), ChangeType.REMOVED) || preview == null
                || preview.addedRecords() == 0 && preview.changedRecords() == 0 && preview.removedRecords() == 0
                    && !Objects.equals(preview.baselineSha256(), file.sha256());
        }).map(file -> file.path()).sorted().forEach(path -> datasetFiles.findFirstByProductPath(path).orElseThrow(
            () -> new SystemException(OfficialApprovalErrorCode.APPLY_STATE_INCONSISTENT, "官方文件行不存在：" + path)));
        List<WarframeOfficialApplyFile> created = new ArrayList<>();
        Instant now = Instant.now();
        for (var file : files) {
            String path = file.path();
            var preview = previews.get(path);
            boolean explicitlySelected = selected(selection, path, ChangeType.ADDED)
                || selected(selection, path, ChangeType.CHANGED) || selected(selection, path, ChangeType.REMOVED);
            if (preview != null && !explicitlySelected && (preview.addedRecords() > 0 || preview.changedRecords() > 0
                || preview.removedRecords() > 0 || Objects.equals(preview.baselineSha256(), file.sha256())))
                continue;
            WarframeOfficialApplyFile job = new WarframeOfficialApplyFile();
            job.setId(UUID.randomUUID());
            job.setBatchId(batchId);
            job.setProductPath(path);
            job.setStatus(ApplyFileStatus.READY.name());
            job.setAddedDone(!selected(selection, path, ChangeType.ADDED));
            job.setChangedDone(!selected(selection, path, ChangeType.CHANGED));
            job.setRemovedDone(!selected(selection, path, ChangeType.REMOVED));
            job.setUpdatedAt(now);
            created.add(job);
        }
        jobs.saveAll(created);
        BusinessAssert.isTrue(
            plan.matchedSelections() > 0
                || plan.remainingCounts().values().stream().mapToInt(Integer::intValue).sum() == 0,
            WarframeManagementErrorCode.DATASET_INVALID, "请至少选择一条待审核差异");
        batch.setApplyPlanJson(write(plan));
        batch.setApplyingAt(Instant.now());
        batches.save(batch);
    }

    @Override
    @Transactional
    public Optional<WarframeOfficialApplyFile> claim() {
        Instant now = Instant.now();
        var candidates = jobs.findClaimableIds(ImportStatus.APPLYING.name(), ApplyFileStatus.READY.name(),
            ApplyFileStatus.RUNNING.name(), now, PageRequest.of(0, properties.getMaxFiles()));
        for (UUID candidate : candidates) {
            var locked = jobs.tryLockById(candidate);
            if (locked.isEmpty())
                continue;
            WarframeOfficialApplyFile job = locked.get();
            boolean ready = ApplyFileStatus.READY.name().equals(job.getStatus())
                && (job.getLeaseUntil() == null || !job.getLeaseUntil().isAfter(now));
            boolean expired = ApplyFileStatus.RUNNING.name().equals(job.getStatus()) && job.getLeaseUntil() != null
                && !job.getLeaseUntil().isAfter(now);
            if (!ready && !expired)
                continue;
            // 同一个文件续租仍持有原行；失联线程下一次提交会因旧令牌被拒绝。
            job.setOwnerToken(UUID.randomUUID());
            job.setStatus(ApplyFileStatus.RUNNING.name());
            job.setLeaseUntil(now.plus(properties.getApplyLeaseTtl()));
            job.setAttempts(job.getAttempts() + 1);
            job.setUpdatedAt(now);
            return Optional.of(jobs.saveAndFlush(job));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void fail(UUID jobId, UUID owner, Exception error) {
        WarframeOfficialApplyFile job = jobs.lockById(jobId).orElseThrow();
        if (!owner.equals(job.getOwnerToken()))
            return;
        boolean terminal = error instanceof BusinessException || job.getAttempts() >= properties.getMaxApplyAttempts();
        job.setStatus(terminal ? ApplyFileStatus.FAILED.name() : ApplyFileStatus.READY.name());
        job.setOwnerToken(null);
        job.setLeaseUntil(
            terminal ? null : Instant.now().plus(properties.getApplyPollInterval().multipliedBy(job.getAttempts())));
        job.setUpdatedAt(Instant.now());
        job.setError(error.getMessage());
        jobs.save(job);
    }

    @Override
    @Transactional
    public void failPreparation(String batchId, Exception error) {
        WarframeOfficialImportBatch batch = batches.lockById(batchId).orElseThrow();
        if (ImportStatus.APPLYING.name().equals(batch.getStatus()) && batch.getApplyPlanJson() == null) {
            retainFailure(batch);
            batch.setStatus(ImportStatus.PARTIAL_FAILED.name());
            batch.setApplyError(error.getMessage());
            batch.setApplyingAt(null);
            batches.save(batch);
        }
    }

    @Override
    @Transactional
    public void finish(String batchId) {
        WarframeOfficialImportBatch batch = batches.lockById(batchId).orElseThrow();
        if (!ImportStatus.APPLYING.name().equals(batch.getStatus()) || batch.getApplyPlanJson() == null)
            return;
        List<WarframeOfficialApplyFile> files = jobs.findAllByBatchId(batchId);
        if (files.stream().anyMatch(job -> ApplyFileStatus.RUNNING.name().equals(job.getStatus())
            || ApplyFileStatus.READY.name().equals(job.getStatus())))
            return;
        if (files.stream().anyMatch(job -> ApplyFileStatus.FAILED.name().equals(job.getStatus()))) {
            retainFailure(batch);
            batch.setStatus(ImportStatus.PARTIAL_FAILED.name());
            batch.setApplyError(files.stream().filter(job -> ApplyFileStatus.FAILED.name().equals(job.getStatus()))
                .map(WarframeOfficialApplyFile::getError).filter(Objects::nonNull).findFirst().orElse("文件入库失败"));
        } else {
            OfficialFilteredChangesDTO plan = read(batch.getApplyPlanJson(), OfficialFilteredChangesDTO.class);
            WarframeOfficialPreviewDTO preview = read(batch.getPreviewJson(), WarframeOfficialPreviewDTO.class);
            batch.setChangeObjectKey(plan.objectKey());
            batch
                .setPreviewJson(
                    write(new WarframeOfficialPreviewDTO(preview.id(), preview.version(), preview.buildId(),
                        preview.baselineId(), ImportStatus.APPLIED.name(), preview.expiresAt(), preview.totalBytes(),
                        plan.remainingCounts().getOrDefault(ChangeType.ADDED.name(), 0),
                        plan.remainingCounts().getOrDefault(ChangeType.CHANGED.name(), 0),
                        plan.remainingCounts().getOrDefault(ChangeType.REMOVED.name(), 0), preview.unmappedPaths(),
                        preview.files().stream().map(file -> new WarframeOfficialPreviewDTO.FileChangeDTO(file.path(),
                            file.sha256(), file.bytes(), file.recordCount(), file.changeType(), file.baselineSha256(),
                            count(plan, file.path(), ChangeType.ADDED), count(plan, file.path(), ChangeType.CHANGED),
                            count(plan, file.path(), ChangeType.REMOVED))).toList(),
                        null, null)));
            batch.setStatus(ImportStatus.APPLIED.name());
            batch.setApplyError(null);
            batch.setRetryExpiresAt(null);
            if (plan.remainingCounts().values().stream().mapToInt(Integer::intValue).sum() == 0) {
                // 全批次只在最后一个文件提交之后更新指针；不占用分类事务的全局行锁。
                heads.findFirstById(IMPORT_HEAD_ID).orElseThrow().setBatchId(batchId);
            }
        }
        batch.setApplyingAt(null);
        batches.save(batch);
    }

    private static int count(OfficialFilteredChangesDTO plan, String path, ChangeType type) {
        return plan.remainingFileCounts().getOrDefault(path, Map.of()).getOrDefault(type.name(), 0);
    }

    @Override
    @Transactional
    public void retry(String batchId) {
        WarframeOfficialImportBatch batch = batches.lockById(batchId).orElseThrow();
        BusinessAssert.isTrue(
            !ImportStatus.EXPIRED.name().equals(batch.getStatus()) || batch.getRetryExpiresAt() == null,
            WarframeManagementErrorCode.DATASET_RETRY_EXPIRED);
        BusinessAssert.isTrue(
            ImportStatus.PARTIAL_FAILED.name().equals(batch.getStatus()) && batch.getApplySelectionJson() != null,
            WarframeManagementErrorCode.DATASET_PREVIEW_NOT_FOUND);
        BusinessAssert.isTrue(batch.getRetryExpiresAt() != null && batch.getRetryExpiresAt().isAfter(Instant.now()),
            WarframeManagementErrorCode.DATASET_RETRY_EXPIRED);
        BusinessAssert.isTrue(!batches.existsByStatus(ImportStatus.APPLYING.name()),
            WarframeManagementErrorCode.DATASET_REFRESH_BLOCKED);
        for (WarframeOfficialApplyFile job : jobs.findAllByBatchId(batchId)) {
            if (ApplyFileStatus.FAILED.name().equals(job.getStatus())) {
                job.setStatus(ApplyFileStatus.READY.name());
                job.setAttempts(0);
                job.setError(null);
                job.setLeaseUntil(null);
                job.setUpdatedAt(Instant.now());
                jobs.save(job);
            }
        }
        batch.setStatus(ImportStatus.APPLYING.name());
        batch.setApplyError(null);
        batch.setApplyingAt(Instant.now());
        batches.save(batch);
    }

    /**
     * 首次失败时固定原件保留期限，避免连续人工重试无限续期。
     *
     * @param batch 首次或再次失败的批次。
     */
    private void retainFailure(WarframeOfficialImportBatch batch) {
        if (batch.getRetryExpiresAt() == null)
            batch.setRetryExpiresAt(Instant.now().plus(properties.getFailedRetention()));
    }

    /**
     * 在批次行锁内使失败数据过期，与人工重试互斥；运行中的文件仍可完成。
     *
     * @param batchId 需要检查的失败批次。
     */
    @Override
    @Transactional
    public void expireFailure(String batchId) {
        WarframeOfficialImportBatch batch = batches.lockById(batchId).orElseThrow();
        if (!ImportStatus.PARTIAL_FAILED.name().equals(batch.getStatus()))
            return;
        if (batch.getRetryExpiresAt() == null) {
            retainFailure(batch);
            batches.saveAndFlush(batch);
        } else if (!batch.getRetryExpiresAt().isAfter(Instant.now())) {
            batch.setStatus(ImportStatus.EXPIRED.name());
            batches.saveAndFlush(batch);
        }
    }

    private <T> T read(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException exception) {
            throw new SystemException(OfficialApprovalErrorCode.SAVED_SNAPSHOT_CORRUPTED, "官方审批快照损坏", exception);
        }
    }

    private String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new SystemException(OfficialApprovalErrorCode.SAVED_SNAPSHOT_CORRUPTED, "无法冻结官方审批快照", exception);
        }
    }
}
