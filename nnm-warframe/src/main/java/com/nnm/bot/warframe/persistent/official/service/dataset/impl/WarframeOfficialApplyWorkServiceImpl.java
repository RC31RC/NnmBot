package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.persistent.official.constant.OfficialApprovalErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ApplyFileStatus;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ChangeType;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStage;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStatus;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialRecordSource;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFilteredChangesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialApplyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialApplyFileDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialPreviewDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialSnapshotDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialApplyFile;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialApplyFileRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApplyPreparationService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApplyStateService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApplyWorkService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApprovalService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialImportService;
import com.nnm.bot.warframe.storage.WarframeOfficialSnapshotStore;
import jakarta.annotation.Resource;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** 每个实例仅处理一个文件；数据库认领允许多实例并行处理不同文件。 */
@Slf4j
@Service
public class WarframeOfficialApplyWorkServiceImpl implements WarframeOfficialApplyWorkService {
    @Resource
    private WarframeOfficialApplyStateService state;
    @Resource
    private WarframeOfficialApplyPreparationService preparation;
    @Resource
    private WarframeOfficialApprovalService approval;
    @Resource
    private WarframeOfficialImportService imports;
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private WarframeOfficialApplyFileRepository jobs;
    @Resource
    private WarframeOfficialSnapshotParser parser;
    @Resource
    private WarframeOfficialSnapshotStore objects;
    @Resource
    private WarframeOfficialImportProperties properties;
    @Resource
    private ObjectMapper objectMapper;
    private final AtomicBoolean running = new AtomicBoolean();

    @Override
    public void freeze(String batchId, WarframeOfficialApplyDTO selection) {
        state.freeze(batchId, selection);
    }

    /** 按本实例串行耗尽当前可认领文件，其他实例仍可并行处理不同文件。 */
    @Override
    public void processAvailable() {
        if (!running.compareAndSet(false, true))
            return;
        try {
            work:
            while (!Thread.currentThread().isInterrupted()) {
                for (var batch : batches
                    .findAllByStatusAndApplyPlanJsonIsNullOrderByCreatedAtAsc(ImportStatus.APPLYING.name())) {
                    try {
                        preparation.prepare(batch.getId());
                    } catch (RuntimeException exception) {
                        log.warn("官方批次 {} 准备失败，下一轮重试", batch.getId(), exception);
                        return;
                    }
                    continue work;
                }
                var claim = state.claim();
                if (claim.isEmpty()) {
                    // 纯零差异审批无需文件工作项；断电后最后一个 DONE 的批次也在这里收尾。
                    batches.findAllByStatusOrderByCreatedAtDesc(ImportStatus.APPLYING.name())
                        .forEach(batch -> state.finish(batch.getId()));
                    return;
                }
                WarframeOfficialApplyFile job = claim.get();
                try {
                    processFile(job);
                } catch (Exception exception) {
                    log.warn("官方文件 {} 入库未完成", job.getProductPath(), exception);
                    state.fail(job.getId(), job.getOwnerToken(), exception);
                } finally {
                    state.finish(job.getBatchId());
                }
            }
        } finally {
            running.set(false);
        }
    }

    private void processFile(WarframeOfficialApplyFile job) throws Exception {
        var batch = batches.findById(job.getBatchId()).orElseThrow();
        var selection = objectMapper.readValue(batch.getApplySelectionJson(), WarframeOfficialApplyDTO.class);
        var plan = objectMapper.readValue(batch.getApplyPlanJson(), OfficialFilteredChangesDTO.class);
        var manifest = objectMapper.readValue(batch.getManifestJson(), WarframeOfficialManifest.class);
        var file = parser.parseFileDigests(manifest).stream().filter(item -> item.path().equals(job.getProductPath()))
            .findFirst().orElseThrow();
        Files.createDirectories(properties.getStagingDirectory());
        try (var snapshot =
            new WarframeOfficialSnapshotDTO(new WarframeOfficialVersionDTO(batch.getSourceRef(), batch.getCommitSha()),
                manifest, Files.createTempDirectory(properties.getStagingDirectory(), "approval-file-"))) {
            WarframeOfficialRecordSource source = consumer -> {
            };
            if (!job.isAddedDone() || !job.isChangedDone() || !job.isRemovedDone()) {
                var entry =
                    manifest.files().stream().filter(item -> item.path().equals(file.path())).findFirst().orElseThrow();
                objects.restore(entry, snapshot.directory().resolve(entry.path()));
                source = parser.recordSource(snapshot.directory(), Set.of(file.path()));
            }
            // 原始差异在事务外读取；恢复时重新读取同一份冻结预览。
            Map<ChangeType, Set<String>> approvedKeys =
                objects.approvedKeys(batch.getChangeObjectKey(), file.path(), selection);
            boolean done = false;
            while (!done) {
                done = approval.applyNextCategory(job.getId(), job.getOwnerToken(), source, file, plan, approvedKeys);
            }
        }
    }

    @Override
    public WarframeOfficialPreviewDTO await(String batchId, TaskProgressReporter reporter) {
        while (true) {
            var batch = batches.findById(batchId).orElseThrow();
            if (!ImportStatus.APPLYING.name().equals(batch.getStatus())) {
                if (ImportStatus.PARTIAL_FAILED.name().equals(batch.getStatus()))
                    throw new BusinessException(OfficialApprovalErrorCode.PARTIAL_APPLY_FAILED);
                return imports.preview(batchId);
            }
            var files = jobs.findAllByBatchId(batchId);
            reporter.report(ImportStage.APPLYING.name(),
                "已入库 " + files.stream().filter(item -> ApplyFileStatus.DONE.name().equals(item.getStatus())).count()
                    + "/" + files.size() + " 个文件",
                files.stream().filter(item -> ApplyFileStatus.DONE.name().equals(item.getStatus())).count(),
                files.size());
            try {
                Thread.sleep(properties.getApplyPollInterval().toMillis());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new SystemException(OfficialApprovalErrorCode.APPLY_WAIT_INTERRUPTED, "等待审批完成时被中断；数据库工作项仍可继续处理",
                    exception);
            }
        }
    }

    @Override
    public List<WarframeOfficialApplyFileDTO> files(String batchId) {
        batches.findById(batchId).orElseThrow();
        return jobs.findAllByBatchId(batchId).stream()
            .map(job -> new WarframeOfficialApplyFileDTO(job.getProductPath(), job.getStatus(), job.isAddedDone(),
                job.isChangedDone(), job.isRemovedDone(), job.getAttempts(), job.getError()))
            .sorted(java.util.Comparator.comparing(WarframeOfficialApplyFileDTO::path)).toList();
    }

    @Override
    public void retry(String batchId) {
        state.retry(batchId);
    }
}
