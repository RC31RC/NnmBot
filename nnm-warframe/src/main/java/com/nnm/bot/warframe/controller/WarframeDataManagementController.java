package com.nnm.bot.warframe.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.task.AdminTaskService;
import com.nnm.bot.common.task.AdminTaskSubmission;
import com.nnm.bot.warframe.application.WarframeDataManagementService;
import com.nnm.bot.warframe.application.WarframeCommunityDatasetService;
import com.nnm.bot.warframe.application.WarframeOfficialDataService;
import com.nnm.bot.warframe.application.WarframeOfficialDataQueryService;
import com.nnm.bot.warframe.application.WarframeWorldStateManagementService;
import com.nnm.bot.warframe.application.WorldStateService;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetInfo;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetItem;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetMergeRequest;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetPreview;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetQueryRequest;
import com.nnm.bot.warframe.dto.WarframeItemAliasItem;
import com.nnm.bot.warframe.dto.WarframeAliasQueryRequest;
import com.nnm.bot.warframe.dto.WarframeItemAliasSaveRequest;
import com.nnm.bot.warframe.dto.WarframeItemItem;
import com.nnm.bot.warframe.dto.WarframeItemQueryRequest;
import com.nnm.bot.warframe.dto.WarframeOfficialRefreshResult;
import com.nnm.bot.warframe.dto.WarframeOfficialRecordItem;
import com.nnm.bot.warframe.dto.WarframeOfficialRecordQueryRequest;
import com.nnm.bot.warframe.dto.WarframeWorldStateItem;
import com.nnm.bot.warframe.dto.WarframeWorldStateRawItem;
import com.nnm.bot.warframe.infrastructure.parser.WorldStateParser;
import com.nnm.bot.warframe.infrastructure.provider.WorldStateProvider;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private WarframeOfficialDataService officialDataService;
    @Resource
    private WarframeOfficialDataQueryService officialDataQueryService;
    @Resource
    private WorldStateService worldStateService;
    @Resource
    private WarframeWorldStateManagementService worldStateManagementService;
    @Resource
    private WorldStateProvider worldStateProvider;
    @Resource
    private WorldStateParser worldStateParser;
    @Resource
    private AdminTaskService taskService;

    @PostMapping("/items/query")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<PageResult<WarframeItemItem>> items(
            @RequestBody WarframeItemQueryRequest request) {
        return ApiResponse.ok(this.service.items(
                request.keyword(),
                request.sourceType(),
                request.current() == null ? 1 : request.current(),
                request.size() == null ? 20 : request.size()));
    }

    @PostMapping("/aliases/query")
    @SaCheckPermission("warframe:community:view")
    public ApiResponse<PageResult<WarframeItemAliasItem>> aliases(
            @RequestBody WarframeAliasQueryRequest request) {
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
            @Valid @RequestBody WarframeItemAliasSaveRequest request) {
        return ApiResponse.ok(this.service.createAlias(request));
    }

    @PutMapping("/aliases/{id}")
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<Void> updateAlias(
            @PathVariable long id,
            @Valid @RequestBody WarframeItemAliasSaveRequest request) {
        this.service.updateAlias(id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/aliases/{id}")
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<Void> deleteAlias(@PathVariable long id) {
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
            @PathVariable String dataset,
            @RequestBody(required = false) WarframeCommunityDatasetQueryRequest request) {
        return ApiResponse.ok(communityDatasetService.query(dataset, request));
    }

    @PostMapping("/community/{dataset}/refresh")
    @SaCheckPermission("warframe:community:sync")
    public ApiResponse<AdminTaskSubmission> refreshCommunityDataset(
            @PathVariable String dataset) {
        String taskType = "WARFRAME_COMMUNITY_DATASET_REFRESH_"
                + dataset.trim().toUpperCase(Locale.ROOT);
        return ApiResponse.ok(submitTask(
                taskType,
                "warframe:community:sync",
                reporter -> communityDatasetService.refresh(dataset, reporter)));
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
    public ApiResponse<WarframeCommunityDatasetPreview> previewCommunityDatasetUpload(
            @PathVariable String dataset,
            @RequestPart("file") MultipartFile file) {
        return ApiResponse.ok(communityDatasetService.uploadPreview(dataset, file));
    }

    @PostMapping("/community/{dataset}/merge")
    @SaCheckPermission("warframe:community:apply")
    public ApiResponse<WarframeCommunityDatasetPreview> mergeCommunityDataset(
            @PathVariable String dataset,
            @Valid @RequestBody WarframeCommunityDatasetMergeRequest request) {
        return ApiResponse.ok(communityDatasetService.merge(dataset, request));
    }

    @PutMapping("/community/{dataset}/records/{id}")
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<WarframeCommunityDatasetItem> updateCommunityDatasetRecord(
            @PathVariable String dataset,
            @PathVariable long id,
            @RequestBody String currentJson) {
        return ApiResponse.ok(communityDatasetService.updateManual(dataset, id, currentJson));
    }

    @PostMapping("/community/{dataset}/records")
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<WarframeCommunityDatasetItem> createCommunityDatasetRecord(
            @PathVariable String dataset,
            @RequestBody String currentJson) {
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
            @RequestBody WarframeOfficialRecordQueryRequest request) {
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
                        worldStateService.refresh(worldStateProvider, worldStateParser, reporter);
                        return currentWorldState();
                    } catch (RuntimeException exception) {
                        throw new com.nnm.bot.common.exception.BusinessException(
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
