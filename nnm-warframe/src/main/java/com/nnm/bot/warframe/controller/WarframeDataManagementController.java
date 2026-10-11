package com.nnm.bot.warframe.controller;

import com.nnm.bot.warframe.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordChangeType;
import com.nnm.bot.common.exception.BusinessException;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.task.AdminTaskService;
import com.nnm.bot.common.task.AdminTaskSubmission;
import com.nnm.bot.warframe.application.WarframeDataManagementService;
import com.nnm.bot.warframe.application.WarframeArbitrationAdminService;
import com.nnm.bot.warframe.application.WarframeCommunityDatasetService;
import com.nnm.bot.warframe.application.WarframeOfficialDataService;
import com.nnm.bot.warframe.application.WarframeOfficialDataQueryService;
import com.nnm.bot.warframe.application.WarframeWorldStateManagementService;
import com.nnm.bot.warframe.application.WarframeWorldStateService;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetInfo;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetItem;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetMergeRequest;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreview;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewPage;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetQueryRequest;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetRefreshSummary;
import com.nnm.bot.warframe.pojo.WarframeArbitrationMergeRequest;
import com.nnm.bot.warframe.pojo.WarframeArbitrationPreview;
import com.nnm.bot.warframe.pojo.WarframeItemAliasItem;
import com.nnm.bot.warframe.pojo.WarframeAliasQueryRequest;
import com.nnm.bot.warframe.pojo.WarframeItemAliasSaveRequest;
import com.nnm.bot.warframe.pojo.WarframeItemItem;
import com.nnm.bot.warframe.pojo.WarframeItemQueryRequest;
import com.nnm.bot.warframe.pojo.WarframeOfficialRefreshResult;
import com.nnm.bot.warframe.pojo.WarframeOfficialRecordItem;
import com.nnm.bot.warframe.pojo.WarframeOfficialRecordQueryRequest;
import com.nnm.bot.warframe.pojo.WarframeWorldStateItem;
import com.nnm.bot.warframe.pojo.WarframeWorldStateRawItem;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/admin/v1/warframe")
public class WarframeDataManagementController {

    @Resource
    private WarframeDataManagementService service;
    @Resource
    private WarframeCommunityDatasetService communityDatasetService;
    @Resource
    private WarframeArbitrationAdminService arbitrationAdminService;
    @Resource
    private WarframeOfficialDataService officialDataService;
    @Resource
    private WarframeOfficialDataQueryService officialDataQueryService;
    @Resource
    private WarframeWorldStateService worldStateService;
    @Resource
    private WarframeWorldStateManagementService worldStateManagementService;
    @Resource
    private AdminTaskService taskService;

    @PostMapping("/items/query")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<PageResult<WarframeItemItem>> items(
            @RequestBody
            WarframeItemQueryRequest request) {
        return ApiResponse.ok(this.service.items(
                request.keyword(),
                request.sourceType(),
                request.current() == null ? 1 : request.current(),
                request.size() == null ? 20 : request.size()));
    }

    @PostMapping("/aliases/query")
    @SaCheckPermission("warframe:community:view")
    public ApiResponse<PageResult<WarframeItemAliasItem>> aliases(
            @RequestBody
            WarframeAliasQueryRequest request) {
        return ApiResponse.ok(this.service.aliases(
                request.keyword(),
                request.itemId(),
                request.sourceType(),
                request.current() == null ? 1 : request.current(),
                request.size() == null ? 20 : request.size()));
    }

    @PostMapping("/aliases")
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<Long> createAlias(
            @Valid
            @RequestBody
            WarframeItemAliasSaveRequest request) {
        return ApiResponse.ok(this.service.createAlias(request));
    }

    @PostMapping("/aliases/{id}")
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<Void> updateAlias(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            WarframeItemAliasSaveRequest request) {
        this.service.updateAlias(id, request);
        return ApiResponse.ok();
    }

    @PostMapping("/aliases/{id}/delete")
    @SaCheckPermission("warframe:community:delete")
    public ApiResponse<Void> deleteAlias(
            @PathVariable
            long id) {
        this.service.deleteAlias(id);
        return ApiResponse.ok();
    }

    @PostMapping("/community/datasets")
    @SaCheckPermission("warframe:community:view")
    public ApiResponse<List<WarframeCommunityDatasetInfo>> communityDatasets() {
        return ApiResponse.ok(communityDatasetService.datasets());
    }

    @PostMapping("/community/{dataset}/query")
    @SaCheckPermission("warframe:community:view")
    public ApiResponse<PageResult<WarframeCommunityDatasetItem>> communityDatasetRecords(
            @PathVariable
            String dataset,
            @RequestBody(required = false)
            WarframeCommunityDatasetQueryRequest request) {
        return ApiResponse.ok(communityDatasetService.query(dataset, request));
    }

    @PostMapping("/community/{dataset}/refresh")
    @SaCheckPermission("warframe:community:sync")
    public ApiResponse<AdminTaskSubmission> refreshCommunityDataset(
            @PathVariable
            String dataset) {
        String taskType = "WARFRAME_COMMUNITY_DATASET_REFRESH_"
                + dataset.trim().toUpperCase(Locale.ROOT);
        return ApiResponse.ok(submitTask(
                taskType,
                "warframe:community:sync",
                reporter -> {
                    WarframeCommunityDatasetPreview preview =
                            communityDatasetService.refresh(dataset, reporter);
                    String label = WarframeCommunityDataset.parse(preview.dataset()).label();
                    return new WarframeCommunityDatasetRefreshSummary(
                            preview.previewId(),
                            preview.dataset(),
                            label,
                            preview.recordCount(),
                            preview.addedCount(),
                            preview.changedCount(),
                            preview.conflictCount());
                }));
    }

    @PostMapping("/community/refresh")
    @SaCheckPermission("warframe:community:sync")
    public ApiResponse<AdminTaskSubmission> refreshAllCommunityDatasets() {
        return ApiResponse.ok(submitTask(
                "WARFRAME_COMMUNITY_DATASETS_REFRESH_ALL",
                "warframe:community:sync",
                communityDatasetService::refreshAll));
    }

    @PostMapping(
            value = "/community/{dataset}/upload-preview",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<WarframeCommunityDatasetPreviewPage> previewCommunityDatasetUpload(
            @PathVariable
            String dataset,
            @RequestPart("file")
            MultipartFile file) {
        WarframeCommunityDatasetPreview preview = communityDatasetService.uploadPreview(dataset, file);
        return ApiResponse.ok(communityDatasetService.previewPage(
                dataset, preview.previewId(), 1, 20));
    }

    @PostMapping("/community/{dataset}/preview/{previewId}")
    @SaCheckPermission("warframe:community:view")
    public ApiResponse<WarframeCommunityDatasetPreviewPage> previewCommunityDataset(
            @PathVariable
            String dataset,
            @PathVariable
            String previewId,
            @RequestParam(defaultValue = "1")
            int current,
            @RequestParam(defaultValue = "20")
            int size,
            @RequestParam(required = false)
            String sourceCode,
            @RequestParam(required = false)
            WarframeCommunityRecordChangeType changeType) {
        return ApiResponse.ok(communityDatasetService.previewPage(
                dataset, previewId, current, size, sourceCode, changeType));
    }

    @PostMapping("/community/{dataset}/merge")
    @SaCheckPermission("warframe:community:apply")
    public ApiResponse<WarframeCommunityDatasetPreview> mergeCommunityDataset(
            @PathVariable
            String dataset,
            @Valid
            @RequestBody
            WarframeCommunityDatasetMergeRequest request) {
        return ApiResponse.ok(communityDatasetService.merge(dataset, request));
    }

    @PostMapping("/arbitration/refresh-preview")
    @SaCheckPermission("warframe:community:sync")
    public ApiResponse<AdminTaskSubmission> refreshArbitrationPreview() {
        return ApiResponse.ok(submitTask(
                "WARFRAME_ARBITRATION_REFRESH_PREVIEW",
                "warframe:community:sync",
                arbitrationAdminService::refreshPreview));
    }

    @PostMapping("/arbitration/preview/{previewId}")
    @SaCheckPermission("warframe:community:view")
    public ApiResponse<WarframeArbitrationPreview> arbitrationPreview(
            @PathVariable
            String previewId) {
        return ApiResponse.ok(arbitrationAdminService.preview(previewId));
    }

    @PostMapping("/arbitration/merge")
    @SaCheckPermission("warframe:community:apply")
    public ApiResponse<WarframeArbitrationPreview> mergeArbitration(
            @Valid
            @RequestBody
            WarframeArbitrationMergeRequest request) {
        return ApiResponse.ok(arbitrationAdminService.merge(request));
    }

    @PostMapping("/community/{dataset}/records/{id}")
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<WarframeCommunityDatasetItem> updateCommunityDatasetRecord(
            @PathVariable
            String dataset,
            @PathVariable
            long id,
            @RequestBody
            String currentJson) {
        return ApiResponse.ok(communityDatasetService.updateManual(dataset, id, currentJson));
    }

    @PostMapping("/community/{dataset}/records/{id}/delete")
    @SaCheckPermission("warframe:community:delete")
    public ApiResponse<Void> deleteCommunityDatasetRecord(
            @PathVariable
            String dataset,
            @PathVariable
            long id) {
        communityDatasetService.deleteManual(dataset, id);
        return ApiResponse.ok();
    }

    @PostMapping("/community/{dataset}/records")
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<WarframeCommunityDatasetItem> createCommunityDatasetRecord(
            @PathVariable
            String dataset,
            @RequestBody
            String currentJson) {
        return ApiResponse.ok(communityDatasetService.createSelfMaintained(dataset, currentJson));
    }

    @PostMapping("/official/refresh")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<AdminTaskSubmission> refreshOfficial() {
        return ApiResponse.ok(submitTask(
                "WARFRAME_OFFICIAL_REFRESH",
                "warframe:game:refresh",
                reporter -> officialDataService.refresh(reporter)));
    }

    @PostMapping("/official/records/query")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<PageResult<WarframeOfficialRecordItem>> officialRecords(
            @RequestBody
            WarframeOfficialRecordQueryRequest request) {
        return ApiResponse.ok(officialDataQueryService.query(request));
    }

    @PostMapping("/world-state/query")
    @SaCheckPermission("warframe:world:view")
    public ApiResponse<WarframeWorldStateItem> worldState() {
        return ApiResponse.ok(currentWorldState());
    }

    @PostMapping("/world-state/raw/query")
    @SaCheckPermission("warframe:world:view")
    public ApiResponse<WarframeWorldStateRawItem> worldStateRaw() {
        return ApiResponse.ok(this.worldStateManagementService.raw());
    }

    @PostMapping("/world-state/refresh")
    @SaCheckPermission("warframe:world:refresh")
    public ApiResponse<AdminTaskSubmission> refreshWorldState() {
        return ApiResponse.ok(submitTask(
                "WARFRAME_WORLD_STATE_REFRESH",
                "warframe:world:refresh",
                reporter -> {
                    try {
                        worldStateService.refresh(reporter);
                        return currentWorldState();
                    } catch (RuntimeException exception) {
                        throw new BusinessException(
                                WarframeManagementErrorCode.WORLD_STATE_REFRESH_FAILED,
                                "Warframe 世界数据刷新失败", exception);
                    }
                }));
    }

    private AdminTaskSubmission submitTask(
            String taskType,
            String permission,
            AdminTaskService.AdminTaskWork work) {
        return taskService.findActive(taskType)
                .map(existing -> new AdminTaskSubmission(existing, taskType))
                .orElseGet(() -> taskService.submit(taskType, permission, work));
    }

    private WarframeWorldStateItem currentWorldState() {
        return this.worldStateManagementService.current();
    }
}
