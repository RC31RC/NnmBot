package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStatus;
import com.nnm.bot.warframe.storage.WarframeOfficialSnapshotStore;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialRetentionService;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest.FileEntry;
import java.util.stream.Stream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportHeadRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialFileRepository;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialPreviewDTO;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.LockKey;
import io.github.rdt.lock.RdtLock;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 在导入元数据互斥作用域内回收快照；不能按对象年龄误删旧的生效版本。 */
@Service
public class WarframeOfficialRetentionServiceImpl implements WarframeOfficialRetentionService {
    @Resource
    private WarframeOfficialImportProperties properties;
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private WarframeOfficialImportHeadRepository heads;
    @Resource
    private WarframeOfficialSnapshotStore objects;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeOfficialFileRepository datasetFiles;
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
        String head = heads.findById(1).map(value -> value.getBatchId()).orElse(null);
        List<WarframeOfficialImportBatch> all = batches.findAll();
        Set<String> retained = new HashSet<>();
        if (head != null)
            retained.add(head);
        all.stream().filter(batch -> ImportStatus.APPLIED.name().equals(batch.getStatus()))
            .sorted(Comparator.comparing(WarframeOfficialImportBatch::getCreatedAt).reversed())
            .limit(properties.getRetainedSnapshots()).forEach(batch -> retained.add(batch.getId()));
        all.stream()
            .filter(batch -> ImportStatus.PENDING.name().equals(batch.getStatus()) && batch.getExpiresAt().isAfter(now))
            .forEach(batch -> retained.add(batch.getId()));
        Set<String> referenced = new HashSet<>();
        all.stream().filter(batch -> retained.contains(batch.getId()))
            .forEach(batch -> batchFiles(batch).forEach(file -> referenced.add(identity(file))));
        retainAdoptedAndPendingBaselines(referenced, all, now);
        for (WarframeOfficialImportBatch batch : all) {
            if (retained.contains(batch.getId()))
                continue;
            // 已归档批次也会重试回收，以处理上次对象存储删除失败。
            if (ImportStatus.PENDING.name().equals(batch.getStatus()))
                batch.setStatus(ImportStatus.EXPIRED.name());
            else if (ImportStatus.APPLIED.name().equals(batch.getStatus()))
                batch.setStatus(ImportStatus.ARCHIVED.name());
            batches.save(batch);
            for (FileEntry file : batchFiles(batch)) {
                if (!referenced.contains(identity(file)))
                    objects.delete(file);
            }
            objects.deleteChanges(batch.getChangeObjectKey());
            batches.deleteById(batch.getId());
        }
        removeAbandonedStaging(now.minus(properties.getStaleStagingTtl()));
    }

    /**
     * 清理刷新失败留下且未被有效批次引用的冻结文件。
     *
     * @param failed 失败。
     */
    public void discardUnreferenced(WarframeOfficialManifest failed, Set<String> selectedPaths) {
        Set<String> referenced = new HashSet<>();
        List<WarframeOfficialImportBatch> all = batches.findAll();
        all.forEach(batch -> batchFiles(batch).forEach(file -> referenced.add(identity(file))));
        retainAdoptedAndPendingBaselines(referenced, all, Instant.now());
        for (FileEntry file : failed.files().stream().filter(file -> selectedPaths.contains(file.path())).toList()) {
            if (!referenced.contains(identity(file)))
                objects.delete(file);
        }
    }

    /**
     * 丢弃指定的待审核批次并清理其独占对象。
     *
     * @param batch 待丢弃的审核批次。
     */
    public void discardBatch(WarframeOfficialImportBatch batch) {
        batch.setStatus(ImportStatus.ARCHIVED.name());
        batches.save(batch);
        List<WarframeOfficialImportBatch> remaining =
            batches.findAll().stream().filter(value -> !value.getId().equals(batch.getId())).toList();
        Set<String> referenced = new HashSet<>();
        remaining.forEach(value -> batchFiles(value).forEach(file -> referenced.add(identity(file))));
        retainAdoptedAndPendingBaselines(referenced, remaining, Instant.now());
        for (FileEntry file : batchFiles(batch)) {
            if (!referenced.contains(identity(file)))
                objects.delete(file);
        }
        objects.deleteChanges(batch.getChangeObjectKey());
        batches.delete(batch);
    }

    private List<FileEntry> batchFiles(WarframeOfficialImportBatch batch) {
        return manifest(batch).files().stream()
            .filter(file -> batch.getProductPath() == null || batch.getProductPath().equals(file.path())).toList();
    }

    /**
     * 部分文件采纳时，最新批次的 manifest 不等于所有文件的当前生效版本。
     *
     * @param referenced 当前不能清理的对象身份集合。
     * @param all 全部仍存活的批次。
     * @param now 当前时间，用于判断待审核批次是否有效。
     */
    private void retainAdoptedAndPendingBaselines(Set<String> referenced, List<WarframeOfficialImportBatch> all,
        Instant now) {
        datasetFiles.findAll().forEach(file -> {
            if (file.getAdoptedSha256() != null) {
                referenced.add(file.getAdoptedSha256() + "/" + file.getProductPath());
            }
        });
        all.stream()
            .filter(batch -> ImportStatus.PENDING.name().equals(batch.getStatus()) && batch.getExpiresAt().isAfter(now))
            .forEach(batch -> {
                try {
                    WarframeOfficialPreviewDTO preview =
                        objectMapper.readValue(batch.getPreviewJson(), WarframeOfficialPreviewDTO.class);
                    preview.files().forEach(file -> {
                        if (file.baselineSha256() != null) {
                            referenced.add(file.baselineSha256() + "/" + file.path());
                        }
                    });
                } catch (JsonProcessingException exception) {
                    throw new IllegalStateException("Cannot read pending preview baseline: " + batch.getId(),
                        exception);
                }
            });
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
            throw new IllegalStateException(
                "Cannot collect dataset objects from a corrupt saved manifest: " + batch.getId(), exception);
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
            throw new IllegalStateException("Cannot clean abandoned official dataset staging", exception);
        }
    }
}
