package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import cn.hutool.core.util.StrUtil;

import com.nnm.bot.common.constant.TaskConstants.AdminTaskStatus;
import com.nnm.bot.common.task.AdminTaskEvent;
import com.nnm.bot.common.task.AdminTaskService;
import com.nnm.bot.common.task.AdminTaskSubmission;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStatus;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRefreshConflictDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialImportService;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialRefreshAdmissionService;
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
    private static final String ACTIVE_PREFIX = "warframe-official-refresh-active:";
    private static final String ALL = "ALL";

    @Resource
    private WarframeOfficialRefreshCoordination coordination;
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private WarframeOfficialSnapshotParser parser;
    @Resource
    private WarframeOfficialImportService imports;
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
        active(ALL).ifPresent(running::add);
        if (productPath == null) {
            parser.supportedPaths().stream().sorted().forEach(path -> active(path).ifPresent(running::add));
        } else {
            active(productPath).ifPresent(running::add);
        }
        List<WarframeOfficialRefreshConflictDTO.PreviewDTO> previews = batches
            .findAllByStatusAndExpiresAtAfterOrderByCreatedAtDesc(ImportStatus.PENDING.name(), Instant.now()).stream()
            .filter(batch -> productPath == null || batch.getProductPath() == null
                || productPath.equals(batch.getProductPath()))
            .map(batch -> new WarframeOfficialRefreshConflictDTO.PreviewDTO(batch.getId(), batch.getProductPath(),
                batch.getSourceRef(), batch.getCreatedAt(), batch.getExpiresAt()))
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
            "已有冲突的官方数据刷新任务，须先取消并等待任务实际停止");
        if (discard) {
            for (WarframeOfficialRefreshConflictDTO.PreviewDTO preview : conflict.previews()) {
                imports.discard(preview.id());
            }
        } else {
            BusinessAssert.isTrue(conflict.previews().isEmpty(), WarframeManagementErrorCode.DATASET_REFRESH_BLOCKED,
                "已有未过期的官方预览，须先审核或丢弃");
        }
        String taskType = productPath == null ? "WARFRAME_OFFICIAL_REFRESH_ALL:" + ref
            : "WARFRAME_OFFICIAL_REFRESH:" + ref + ":" + productPath;
        AdminTaskSubmission submitted = tasks.submit(taskType, "warframe:game:refresh", reporter -> productPath == null
            ? imports.refreshAll(ref, reporter) : imports.refresh(ref, productPath, reporter));
        state.put(ACTIVE_PREFIX + (productPath == null ? ALL : productPath), submitted.taskId(),
            properties.getActiveTaskRetention());
        return submitted;
    }

    private Optional<WarframeOfficialRefreshConflictDTO.TaskDTO> active(String scope) {
        return state.<UUID>get(ACTIVE_PREFIX + scope, UUID.class).flatMap(tasks::find)
            .filter(event -> event.status() == AdminTaskStatus.QUEUED || event.status() == AdminTaskStatus.RUNNING)
            .map(event -> task(event, scope));
    }

    private static WarframeOfficialRefreshConflictDTO.TaskDTO task(AdminTaskEvent event, String scope) {
        String prefix = scope.equals(ALL) ? "WARFRAME_OFFICIAL_REFRESH_ALL:" : "WARFRAME_OFFICIAL_REFRESH:";
        String type = event.taskType();
        String ref = type.startsWith(prefix) ? type.substring(prefix.length()) : type;
        if (!scope.equals(ALL) && ref.endsWith(":" + scope))
            ref = ref.substring(0, ref.length() - scope.length() - 1);
        return new WarframeOfficialRefreshConflictDTO.TaskDTO(event.taskId(), scope.equals(ALL) ? null : scope, ref,
            event.status().name(), event.updatedAt());
    }

    private void validate(String productPath) {
        if (productPath != null)
            BusinessAssert.isTrue(parser.supportedPaths().contains(productPath),
                WarframeManagementErrorCode.INVALID_OFFICIAL_DATA_DOMAIN);
    }
}
