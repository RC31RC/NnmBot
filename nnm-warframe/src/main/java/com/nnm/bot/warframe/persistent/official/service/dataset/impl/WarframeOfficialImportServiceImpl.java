package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import java.util.stream.Collectors;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStatus;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ChangeType;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest.FileEntry;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialRecordSource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.official.client.source.WarframeOfficialSourceClient;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialFile;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportHead;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialPreviewDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialPreviewDTO.FileChangeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialFileRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportHeadRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialRetentionService;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFileDigestDTO;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApprovalService;
import com.nnm.bot.warframe.persistent.official.client.source.WarframeOfficialSnapshotDownloader;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialSnapshotDTO;
import com.nnm.bot.warframe.storage.WarframeOfficialSnapshotStore;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialImportService;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.RdtLock;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialRefreshGroups;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class WarframeOfficialImportServiceImpl implements WarframeOfficialImportService {
    @Resource
    private WarframeOfficialRetentionService retention;
    @Resource
    private DistributedLockManager lockManager;
    @Resource
    private WarframeOfficialSourceClient client;
    @Resource
    private WarframeOfficialSnapshotDownloader downloader;
    @Resource
    private WarframeOfficialSnapshotStore objects;
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private WarframeOfficialFileRepository datasetFiles;
    @Resource
    private WarframeOfficialImportHeadRepository heads;
    @Resource
    private WarframeOfficialImportProperties properties;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeOfficialSnapshotParser snapshotParser;
    @Resource
    private WarframeOfficialApprovalService approval;

    /**
     * 列出可选的官方数据版本。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeOfficialVersionDTO> versions() {
        return client.versions();
    }

    /**
     * 获取当前上游数据并更新本地有效状态。
     *
     * @param ref 明确的标签名称或完整提交哈希。
     * @param productPath 官方 JSON 文件在发布包内的相对路径。
     * @param reporter 向管理端任务提交进度的回调。
     * @return 游戏资料数据文件预览对象。
     */
    @Override
    @RdtLock
    public WarframeOfficialPreviewDTO refresh(String ref, String productPath, TaskProgressReporter reporter) {
        lockManager.lock("warframe-official-import-v1");
        if (productPath != null) {
            BusinessAssert.notNull(WarframeOfficialDataDomain.fromPath(productPath),
                    WarframeManagementErrorCode.DATASET_INVALID, "未登记的静态文件路径：" + productPath);
        }
        Set<String> selectedPaths = productPath == null
                ? Set.copyOf(snapshotParser.supportedPaths()) : WarframeOfficialRefreshGroups.paths(productPath);
        retention.prepareRefresh();
        String baseline = heads.findById(1).map(WarframeOfficialImportHead::getBatchId).orElse(null);
        String baselineHash = approval.fingerprint();
        String selectedRef = ref;
        if (StrUtil.isBlank(selectedRef)) {
            List<WarframeOfficialVersionDTO> versions = client.versions();
            BusinessAssert.notEmpty(versions, WarframeManagementErrorCode.DATASET_VERSION_INVALID);
            selectedRef = versions.getFirst().ref();
        }
        WarframeOfficialPreviewDTO completed = null;
        WarframeOfficialManifest downloaded = null;
        try (WarframeOfficialSnapshotDTO snapshot = downloader.download(selectedRef, reporter, selectedPaths)) {
            downloaded = snapshot.manifest();
            List<OfficialFileDigestDTO> fileStates = snapshotParser.parseFileDigests(snapshot.manifest()).stream()
                    .filter(file -> selectedPaths.contains(file.path())).toList();
            Set<String> paths = approval.changedPaths(fileStates);
            // 下载器已经用 manifest hash 校验本次选定的全部文件。
            WarframeOfficialRecordSource incoming = snapshotParser.recordSource(snapshot.directory(), paths);
            String previewId = UUID.randomUUID().toString();
            Map<String, Integer> changeCounts = objects.putChanges(previewId,
                    consumer -> { if (CollUtil.isNotEmpty(paths)) approval.changes(incoming, paths, consumer); }, snapshot.directory());
            BusinessAssert.equals(approval.fingerprint(), baselineHash,
                    WarframeManagementErrorCode.DATASET_PREVIEW_CONFLICT, "下载期间官方记录已变化，请重新生成预览");
            for (FileEntry file : snapshot.manifest().files()) {
                if (!selectedPaths.contains(file.path())) continue;
                objects.put(file, snapshot.directory().resolve(file.path()));
            }
            WarframeOfficialImportBatch batch = new WarframeOfficialImportBatch();
            batch.setId(previewId);
            batch.setSourceRef(snapshot.version().ref());
            batch.setCommitSha(snapshot.version().commit());
            batch.setBuildId(snapshot.manifest().buildId());
            batch.setManifestJson(objectMapper.writeValueAsString(snapshot.manifest()));
            batch.setProductPath(productPath);
            batch.setBaselineId(baseline);
            batch.setBaselineHash(baselineHash);
            batch.setParserContractVersion(WarframeOfficialSnapshotParser.CONTRACT_VERSION);
            batch.setCreatedAt(Instant.now());
            batch.setExpiresAt(batch.getCreatedAt().plus(properties.getPreviewTtl()));
            batch.setStatus(ImportStatus.PENDING.name());
            WarframeOfficialPreviewDTO preview = buildPreview(batch, snapshot.manifest(), changeCounts);
            batch.setPreviewJson(objectMapper.writeValueAsString(preview));
            // 只有下载、结构、snapshotParser 和对象存储全部成功后才发布待审核批次。
            approval.stageCandidates(snapshot.version().ref(), snapshot.version().commit(),
                    snapshot.manifest().buildId(), fileStates);
            batches.save(batch);
            completed = preview;
            return preview;
        } catch (IOException | RuntimeException exception) {
            if (completed != null) {
                log.warn("Official preview {} was saved; temporary staging cleanup failed", completed.id(), exception);
                return completed;
            }
            if (downloaded != null) {
                try {
                    retention.discardUnreferenced(downloaded);
                } catch (RuntimeException cleanup) {
                    exception.addSuppressed(cleanup);
                }
            }
            if (exception instanceof IOException io) throw unavailable(io);
            throw (RuntimeException) exception;
        }
    }

    /**
     * 生成变更预览，供后续确认采纳。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 游戏资料数据文件预览对象。
     */
    @Override
    public WarframeOfficialPreviewDTO preview(String id) {
        WarframeOfficialImportBatch batch = requireBatch(id);
        try {
            WarframeOfficialPreviewDTO stored = objectMapper.readValue(batch.getPreviewJson(), WarframeOfficialPreviewDTO.class);
            return new WarframeOfficialPreviewDTO(stored.id(), stored.version(), stored.buildId(), stored.baselineId(),
                    batch.getStatus(), stored.expiresAt(), stored.totalBytes(), stored.addedRecords(),
                    stored.changedRecords(), stored.removedRecords(), stored.unmappedPaths(), stored.files());
        } catch (JsonProcessingException exception) {
            throw new BusinessException(WarframeManagementErrorCode.DATASET_INVALID, "已保存预览损坏", exception);
        }
    }

    /**
     * 对比当前数据并生成待采纳变更。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @param current 从一开始的当前页码。
     * @param size 当前操作允许的数据数量。
     * @return 包含当前页记录及总数的分页结果。
     */
    @Override
    public PageResult<WarframeOfficialRecordChangeDTO> changes(String id, int current, int size) {
        requireBatch(id);
        try {
            return objects.changes(id, current, size);
        } catch (IOException exception) {
            throw unavailable(exception);
        }
    }

    /**
     * 采纳已经确认的业务变更。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 游戏资料数据文件预览对象。
     */
    @Override
    @RdtLock
    public WarframeOfficialPreviewDTO apply(String id) {
        lockManager.lock("warframe-official-import-v1");
        WarframeOfficialImportBatch batch = requireBatch(id);
        if (ImportStatus.APPLIED.name().equals(batch.getStatus())) return preview(id);
        WarframeOfficialManifest manifest = manifest(batch);
        try {
            Files.createDirectories(properties.getStagingDirectory());
            try (WarframeOfficialSnapshotDTO snapshot = new WarframeOfficialSnapshotDTO(new WarframeOfficialVersionDTO(batch.getSourceRef(), batch.getCommitSha()),
                    manifest, Files.createTempDirectory(properties.getStagingDirectory(), "approval-"))) {
                // 只有源版本或解析契约变化、或存在待重建身份时才恢复并解析业务文件。
                List<OfficialFileDigestDTO> datasetFiles = snapshotParser.parseFileDigests(manifest).stream()
                        .filter(file -> batch.getProductPath() == null
                                || WarframeOfficialRefreshGroups.paths(batch.getProductPath()).contains(file.path())).toList();
                WarframeOfficialRecordSource incoming = consumer -> { };
                Set<String> paths = approval.changedPaths(datasetFiles);
                if (CollUtil.isNotEmpty(paths)) {
                    for (FileEntry entry : manifest.files()) {
                        if (paths.contains(entry.path())) {
                            objects.restore(entry, snapshot.directory().resolve(entry.path()));
                        }
                    }
                    incoming = snapshotParser.recordSource(snapshot.directory(), paths);
                }
                approval.apply(id, incoming, datasetFiles);
            }
            return preview(id);
        } catch (IOException | RuntimeException exception) {
            boolean applied = batches.findById(id).map(value -> ImportStatus.APPLIED.name().equals(value.getStatus())).orElse(false);
            if (applied) {
                log.warn("Official batch {} committed; cache notification or temporary cleanup needs retry", id, exception);
                return preview(id);
            }
            if (exception instanceof IOException io) throw unavailable(io);
            throw (RuntimeException) exception;
        }
    }

    /**
     * 取得仍可处理的批次并校验当前状态。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 游戏资料数据文件批次对象。
     */
    private WarframeOfficialImportBatch requireBatch(String id) {
        BusinessAssert.isTrue(id != null && id.matches("[a-f0-9-]{36}"), WarframeManagementErrorCode.DATASET_PREVIEW_NOT_FOUND);
        WarframeOfficialImportBatch batch = batches.findById(id).orElse(null);
        BusinessAssert.notNull(batch, WarframeManagementErrorCode.DATASET_PREVIEW_NOT_FOUND);
        BusinessAssert.isTrue(ImportStatus.APPLIED.name().equals(batch.getStatus())
                        || (ImportStatus.PENDING.name().equals(batch.getStatus()) && batch.getExpiresAt().isAfter(Instant.now())),
                WarframeManagementErrorCode.DATASET_PREVIEW_NOT_FOUND);
        return batch;
    }

    /**
     * 根据冻结输入和当前基线构造变更预览。
     *
     * @param batch 批次。
     * @param manifest 发布清单。
     * @param recordChanges 记录变更。
     * @return 游戏资料数据文件预览对象。
     */
    private WarframeOfficialPreviewDTO buildPreview(WarframeOfficialImportBatch batch, WarframeOfficialManifest manifest,
                                                Map<String, Integer> recordChanges) {
        Map<String, String> adopted = datasetFiles.findAll().stream()
                .filter(file -> file.getAdoptedSha256() != null)
                .collect(Collectors.toMap(
                        WarframeOfficialFile::getProductPath, WarframeOfficialFile::getAdoptedSha256));
        List<FileChangeDTO> changes = new ArrayList<>();
        long totalBytes = 0;
        for (FileEntry file : manifest.files()) {
            if (batch.getProductPath() != null && !WarframeOfficialRefreshGroups.paths(batch.getProductPath()).contains(file.path())) continue;
            String oldHash = adopted.get(file.path());
            String type = oldHash == null ? ChangeType.ADDED.name() : oldHash.equals(file.sha256()) ? ChangeType.UNCHANGED.name() : ChangeType.CHANGED.name();
            changes.add(new FileChangeDTO(file.path(), file.sha256(), file.bytes(), file.recordCount(), type));
            totalBytes += file.bytes();
        }
        changes.sort(Comparator.comparing(FileChangeDTO::path));
        List<String> unmapped = manifest.files().stream().map(WarframeOfficialManifest.FileEntry::path)
                .filter(path -> !snapshotParser.supportedPaths().contains(path)).sorted().toList();
        return new WarframeOfficialPreviewDTO(batch.getId(),
                new WarframeOfficialVersionDTO(batch.getSourceRef(), batch.getCommitSha()), batch.getBuildId(),
                batch.getBaselineId(), batch.getStatus(), batch.getExpiresAt(), totalBytes,
                recordChanges.getOrDefault(ChangeType.ADDED.name(), 0), recordChanges.getOrDefault(ChangeType.CHANGED.name(), 0), recordChanges.getOrDefault(ChangeType.REMOVED.name(), 0), unmapped, changes);
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
            throw new BusinessException(WarframeManagementErrorCode.DATASET_INVALID, "已保存清单损坏", exception);
        }
    }

    /**
     * 构造当前数据不可用时的明确展示结果。
     *
     * @param exception 需要保留原因和字段定位的原始异常。
     * @return 保留失败原因的业务异常。
     */
    private static BusinessException unavailable(IOException exception) {
        return new BusinessException(WarframeManagementErrorCode.DATASET_UNAVAILABLE,
                "官方成品暂存或读取失败；未完成的批次不会成为生效数据", exception);
    }
}
