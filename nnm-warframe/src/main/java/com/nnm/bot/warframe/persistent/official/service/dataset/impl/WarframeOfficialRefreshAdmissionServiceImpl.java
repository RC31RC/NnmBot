package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ACTIVE_REFRESH_PREFIX;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ALL_SCOPE;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.APPLY_TASK_PREFIX;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.REFRESH_ALL_TASK_PREFIX;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.REFRESH_PERMISSION;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.REFRESH_TASK_PREFIX;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.constant.TaskConstants.AdminTaskStatus;
import com.nnm.bot.common.task.AdminTaskEvent;
import com.nnm.bot.common.task.AdminTaskService;
import com.nnm.bot.common.task.AdminTaskSubmission;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStatus;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialApplyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRefreshConflictDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApplyWorkService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialImportService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialRefreshAdmissionService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialRetentionService;
import io.github.rdt.state.SharedStateStore;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

/** 数据库预览与 Redis 活动任务分别查询，写入入口在同一个分布式准入锁内复核。 */
@Service
public class WarframeOfficialRefreshAdmissionServiceImpl implements WarframeOfficialRefreshAdmissionService {

    @Resource
    private WarframeOfficialRefreshCoordination coordination;
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private WarframeOfficialSnapshotParser parser;
    @Resource
    private WarframeOfficialImportService imports;
    @Resource
    private WarframeOfficialApplyWorkService applyWork;
    @Resource
    private WarframeOfficialRetentionService retention;
    @Resource
    private WarframeOfficialImportProperties properties;
    @Resource
    private SharedStateStore state;
    @Resource
    private AdminTaskService tasks;

    /**
     * 查询当前路径及全量操作的有效预览和真实运行任务。
     *
     * @param productPath 单文件路径；全量为 null。
     * @return 影响这次刷新的任务和待审核预览。
     */
    @Override
    public WarframeOfficialRefreshConflictDTO conflicts(String productPath) {
        validate(productPath);
        List<WarframeOfficialRefreshConflictDTO.TaskDTO> running = new ArrayList<>();
        active(ALL_SCOPE).ifPresent(running::add);
        if (productPath == null) {
            parser.supportedPaths().stream().sorted().forEach(path -> active(path).ifPresent(running::add));
        } else {
            active(productPath).ifPresent(running::add);
        }
        Instant now = Instant.now();
        var all = batches
            .findAllProjectedByStatusIn(
                List.of(ImportStatus.APPLYING.name(), ImportStatus.PENDING.name(), ImportStatus.PARTIAL_FAILED.name()))
            .stream()
            .filter(batch -> ImportStatus.APPLYING.name().equals(batch.getStatus())
                || ImportStatus.PENDING.name().equals(batch.getStatus()) && batch.getExpiresAt().isAfter(now)
                || ImportStatus.PARTIAL_FAILED.name().equals(batch.getStatus())
                    && (batch.getRetryExpiresAt() == null || batch.getRetryExpiresAt().isAfter(now)))
            .toList();
        // 即使预览已到期，正在排队／执行的采纳也必须阻止新的刷新。
        all.forEach(batch -> tasks.findActive(APPLY_TASK_PREFIX + batch.getId()).flatMap(tasks::find)
            .ifPresent(event -> running.add(new WarframeOfficialRefreshConflictDTO.TaskDTO(event.taskId(),
                batch.getProductPath(), batch.getSourceRef(), "APPLY", event.status().name(), event.updatedAt()))));
        List<WarframeOfficialRefreshConflictDTO.PreviewDTO> previews = all.stream()
            .filter(batch -> (ImportStatus.APPLYING.name().equals(batch.getStatus())
                || ImportStatus.PARTIAL_FAILED.name().equals(batch.getStatus())
                    && (batch.getRetryExpiresAt() == null || batch.getRetryExpiresAt().isAfter(now))
                || ImportStatus.PENDING.name().equals(batch.getStatus()) && batch.getExpiresAt().isAfter(now))
                && (productPath == null || batch.getProductPath() == null
                    || productPath.equals(batch.getProductPath())))
            .map(batch -> new WarframeOfficialRefreshConflictDTO.PreviewDTO(batch.getId(), batch.getProductPath(),
                batch.getSourceRef(), batch.getStatus(), batch.getCreatedAt(), batch.getExpiresAt()))
            .toList();
        return new WarframeOfficialRefreshConflictDTO(running, previews);
    }

    /**
     * 原子检查冲突并提交任务；相同文件不同 tag 也不能并行。
     *
     * @param ref 用户选择的数据 tag。
     * @param productPath 单文件路径；全量为 null。
     * @return 已登记的后台任务。
     */
    @Override
    public AdminTaskSubmission submit(String ref, String productPath) {
        String selectedRef = selectedRef(ref);
        return coordination.reserve(() -> create(selectedRef, productPath, false));
    }

    /**
     * 只丢弃此次刷新实际覆盖到的预览，保留其他单文件待审预览。
     *
     * @param ref 用户选择的数据 tag。
     * @param productPath 单文件路径；全量为 null。
     * @return 已登记的后台任务。
     */
    @Override
    public AdminTaskSubmission discardAndSubmit(String ref, String productPath) {
        String selectedRef = selectedRef(ref);
        return coordination.reserve(() -> create(selectedRef, productPath, true));
    }

    /**
     * 在刷新相同的准入锁中登记采纳，重复提交同一批次复用活动任务。
     *
     * @param id 待审批次标识。
     * @param selection 本轮审核的选择。
     * @return 后台采纳任务。
     */
    @Override
    public AdminTaskSubmission submitApply(String id, WarframeOfficialApplyDTO selection) {
        BusinessAssert.isTrue(StrUtil.isNotBlank(id) && selection != null, WarframeManagementErrorCode.DATASET_INVALID);
        return coordination.reserve(() -> {
            String type = APPLY_TASK_PREFIX + id;
            Optional<UUID> existing = tasks.findActive(type);
            if (existing.isPresent())
                return new AdminTaskSubmission(existing.get(), type);
            WarframeOfficialImportBatch batch = batches.findById(id).orElse(null);
            BusinessAssert.isTrue(
                batch != null && ImportStatus.PENDING.name().equals(batch.getStatus())
                    && batch.getExpiresAt().isAfter(Instant.now()),
                WarframeManagementErrorCode.DATASET_PREVIEW_NOT_FOUND);
            BusinessAssert.isTrue(conflicts(null).tasks().isEmpty(),
                WarframeManagementErrorCode.DATASET_REFRESH_BLOCKED, "官方数据正在刷新或采纳，请等待当前任务结束");
            applyWork.freeze(id, selection);
            AdminTaskSubmission submitted =
                tasks.submit(type, REFRESH_PERMISSION, reporter -> applyWork.await(id, reporter));
            return submitted;
        });
    }

    @Override
    public AdminTaskSubmission retryApply(String id) {
        return coordination.reserve(() -> {
            applyWork.retry(id);
            return tasks.submit(APPLY_TASK_PREFIX + id, REFRESH_PERMISSION, reporter -> applyWork.await(id, reporter));
        });
    }

    private String selectedRef(String ref) {
        if (StrUtil.isNotBlank(ref))
            return ref;
        List<WarframeOfficialVersionDTO> versions = imports.versions();
        BusinessAssert.notEmpty(versions, WarframeManagementErrorCode.DATASET_VERSION_INVALID);
        return versions.getFirst().ref();
    }

    private AdminTaskSubmission create(String ref, String productPath, boolean discard) {
        WarframeOfficialRefreshConflictDTO conflict = conflicts(productPath);
        BusinessAssert.isTrue(conflict.tasks().isEmpty(), WarframeManagementErrorCode.DATASET_REFRESH_BLOCKED,
            "官方数据正在刷新或采纳，请等待当前任务结束");
        if (discard) {
            BusinessAssert.isTrue(!batches.existsByStatus(ImportStatus.APPLYING.name()),
                WarframeManagementErrorCode.DATASET_REFRESH_BLOCKED, "官方数据正在采纳，不能丢弃审核批次");
            retention.archiveBatches(
                conflict.previews().stream().map(WarframeOfficialRefreshConflictDTO.PreviewDTO::id).toList());
        } else {
            BusinessAssert.isTrue(conflict.previews().isEmpty(), WarframeManagementErrorCode.DATASET_REFRESH_BLOCKED,
                "已有未过期的官方预览，须先审核或丢弃");
        }
        String taskType =
            productPath == null ? REFRESH_ALL_TASK_PREFIX + ref : REFRESH_TASK_PREFIX + ref + ":" + productPath;
        AdminTaskSubmission submitted = tasks.submit(taskType, REFRESH_PERMISSION, reporter -> productPath == null
            ? imports.refreshAll(ref, reporter) : imports.refresh(ref, productPath, reporter));
        state.put(ACTIVE_REFRESH_PREFIX + (productPath == null ? ALL_SCOPE : productPath), submitted.taskId(),
            properties.getActiveTaskRetention());
        return submitted;
    }

    private Optional<WarframeOfficialRefreshConflictDTO.TaskDTO> active(String scope) {
        return state.<UUID>get(ACTIVE_REFRESH_PREFIX + scope, UUID.class).flatMap(tasks::find)
            .filter(event -> event.status() == AdminTaskStatus.QUEUED || event.status() == AdminTaskStatus.RUNNING)
            .map(event -> task(event, scope));
    }

    private static WarframeOfficialRefreshConflictDTO.TaskDTO task(AdminTaskEvent event, String scope) {
        String prefix = scope.equals(ALL_SCOPE) ? REFRESH_ALL_TASK_PREFIX : REFRESH_TASK_PREFIX;
        String type = event.taskType();
        String ref = type.startsWith(prefix) ? type.substring(prefix.length()) : type;
        if (!scope.equals(ALL_SCOPE) && ref.endsWith(":" + scope))
            ref = ref.substring(0, ref.length() - scope.length() - 1);
        return new WarframeOfficialRefreshConflictDTO.TaskDTO(event.taskId(), scope.equals(ALL_SCOPE) ? null : scope,
            ref, "REFRESH", event.status().name(), event.updatedAt());
    }

    /**
     * 单独丢弃与采纳登记共用准入锁，任务排队后也不能删除候选对象。
     *
     * @param id 待丢弃批次标识。
     */
    @Override
    public void discardPreview(String id) {
        coordination.reserve(() -> {
            BusinessAssert.isTrue(tasks.findActive(APPLY_TASK_PREFIX + id).isEmpty(),
                WarframeManagementErrorCode.DATASET_REFRESH_BLOCKED, "该预览正在采纳，请等待任务结束");
            imports.discard(id);
            return null;
        });
    }

    private void validate(String productPath) {
        if (productPath != null)
            BusinessAssert.isTrue(parser.supportedPaths().contains(productPath),
                WarframeManagementErrorCode.INVALID_OFFICIAL_DATA_DOMAIN);
    }
}
