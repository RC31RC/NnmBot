package com.nnm.bot.warframe.controller.persistent.official;

import com.nnm.bot.warframe.persistent.official.mapper.dataset.WarframeOfficialImportViewMapper;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.task.AdminTaskSubmission;
import com.nnm.bot.warframe.persistent.official.pojo.vo.record.WarframeOfficialRecordVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.record.WarframeOfficialDatasetOverviewVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.record.WarframeOfficialFormFieldVO;
import com.nnm.bot.warframe.persistent.official.pojo.query.WarframeOfficialRecordQuery;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialDataQueryService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialImportService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialRefreshAdmissionService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialRefreshConflictVO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRefreshConflictDTO;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialMaintenanceService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialVersionVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialPreviewVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialApplyFileVO;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApplyWorkService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialRecordChangeVO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRefreshDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRefreshAllDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialApplyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.maintenance.WarframeOfficialRecordSaveDTO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialRawRecordVO;

/** 管理端官方静态资料审核与自维护记录的 HTTP 入口。 */
@RestController
@RequestMapping("/api/admin/warframe/official")
public class WarframeDataManagementController {

    /** 官方静态资料下载、预览与应用服务。 */
    @Resource
    private WarframeOfficialImportService datasetService;
    /** 官方独立数据表的管理查询服务。 */
    @Resource
    private WarframeOfficialDataQueryService officialDataQueryService;
    /** 官方模型自维护记录的整树写入服务。 */
    @Resource
    private WarframeOfficialMaintenanceService officialMaintenanceService;
    @Resource
    private WarframeOfficialRefreshAdmissionService admission;
    @Resource
    private WarframeOfficialApplyWorkService applyWork;

    /**
     * 查询可选择的官方静态数据版本。
     *
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/versions")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<List<WarframeOfficialVersionVO>> officialVersions() {
        return ApiResponse
            .ok(datasetService.versions().stream().map(WarframeOfficialImportViewMapper::version).toList());
    }

    /**
     * 提交单个官方 JSON 拉取任务，完成后生成待审核预览。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/refresh")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<AdminTaskSubmission> refreshOfficial(@Valid @RequestBody WarframeOfficialRefreshDTO request) {
        return ApiResponse.ok(admission.submit(request.ref(), request.productPath()));
    }

    /**
     * 提交全部官方 JSON 文件的固定 tag 刷新任务。
     *
     * @param request 本次选择的固定版本。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/refresh-all")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<AdminTaskSubmission> refreshAllOfficial(@RequestBody WarframeOfficialRefreshAllDTO request) {
        return ApiResponse.ok(admission.submit(request.ref(), null));
    }

    /** 查询官方全量或单文件刷新的正在执行任务与有效预览。 */
    @PostMapping("/refresh/conflicts")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<WarframeOfficialRefreshConflictVO>
        refreshConflicts(@RequestBody WarframeOfficialRefreshDTO request) {
        return ApiResponse.ok(conflictVO(admission.conflicts(request.productPath())));
    }

    /** 管理员确认后丢弃覆盖范围内的预览并重新提交；写时仍原子复核任务。 */
    @PostMapping("/refresh/discard-and-restart")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<AdminTaskSubmission> discardAndRestart(@RequestBody WarframeOfficialRefreshDTO request) {
        return ApiResponse.ok(admission.discardAndSubmit(request.ref(), request.productPath()));
    }

    private static WarframeOfficialRefreshConflictVO conflictVO(WarframeOfficialRefreshConflictDTO value) {
        return new WarframeOfficialRefreshConflictVO(
            value.tasks().stream()
                .map(task -> new WarframeOfficialRefreshConflictVO.TaskVO(task.taskId(), task.productPath(), task.ref(),
                    task.operation(), task.status(), task.updatedAt()))
                .toList(),
            value
                .previews().stream().map(preview -> new WarframeOfficialRefreshConflictVO.PreviewVO(preview.id(),
                    preview.productPath(), preview.ref(), preview.status(), preview.createdAt(), preview.expiresAt()))
                .toList());
    }

    /**
     * 读取指定官方静态数据预览。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/preview/{id}")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<WarframeOfficialPreviewVO> officialPreview(@PathVariable String id) {
        return ApiResponse.ok(WarframeOfficialImportViewMapper.preview(datasetService.preview(id)));
    }

    /**
     * 列出当前仍有效的官方数据待审核预览。
     *
     * @return 按创建时间倒序排列的预览列表。
     */
    @PostMapping("/previews/pending")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<List<WarframeOfficialPreviewVO>> pendingOfficialPreviews() {
        return ApiResponse
            .ok(datasetService.pendingPreviews().stream().map(WarframeOfficialImportViewMapper::preview).toList());
    }

    /**
     * 丢弃官方数据待审核预览。
     *
     * @param id 审核批次标识。
     * @return 空操作结果。
     */
    @PostMapping("/preview/{id}/discard")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<Void> discardOfficialPreview(@PathVariable String id) {
        admission.discardPreview(id);
        return ApiResponse.ok();
    }

    /** 返回本次审批逐文件以及逐类型的已提交进度。 */
    @PostMapping("/preview/{id}/apply-files")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<List<WarframeOfficialApplyFileVO>> applyFiles(@PathVariable String id) {
        return ApiResponse
            .ok(applyWork.files(id).stream().map(file -> new WarframeOfficialApplyFileVO(file.path(), file.status(),
                file.addedDone(), file.changedDone(), file.removedDone(), file.attempts(), file.error())).toList());
    }

    /** 仅恢复同一次审批中失败的文件和未提交分类，不接受第二次选择。 */
    @PostMapping("/preview/{id}/apply-retry")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<AdminTaskSubmission> retryApply(@PathVariable String id) {
        return ApiResponse.ok(admission.retryApply(id));
    }

    /**
     * 分页查询预览中的记录差异。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @param current 从一开始的当前页码。
     * @param size 当前操作允许的数据数量。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/preview/{id}/changes")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<PageResult<WarframeOfficialRecordChangeVO>> officialChanges(@PathVariable String id,
        @RequestParam String path, @RequestParam(defaultValue = "1") int current,
        @RequestParam(defaultValue = "20") int size, @RequestParam String changeType) {
        return ApiResponse
            .ok(WarframeOfficialImportViewMapper.changes(datasetService.changes(id, path, current, size, changeType)));
    }

    /**
     * 按需读取待审核候选文件的指定业务记录原文。
     *
     * @param id 审核批次标识。
     * @param path 官方 JSON 文件路径。
     * @param recordKey 记录稳定业务键。
     * @return 对应版本的记录原文。
     */
    @PostMapping("/preview/{id}/record")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<WarframeOfficialRawRecordVO> officialRecord(@PathVariable String id, @RequestParam String path,
        @RequestParam String recordKey) {
        return ApiResponse.ok(WarframeOfficialImportViewMapper.record(datasetService.record(id, path, recordKey)));
    }

    /**
     * 提交已审核预览中选中的记录差异。
     *
     * @param id 审核批次标识。
     * @param request 本次选择采纳的记录差异。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/preview/{id}/apply")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<AdminTaskSubmission> applyOfficial(@PathVariable String id,
        @RequestBody WarframeOfficialApplyDTO request) {
        return ApiResponse.ok(admission.submitApply(id, request));
    }

    /**
     * 按数据文件和筛选条件分页查询官方记录。
     *
     * @param request 本次查询的筛选与分页参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/records/query")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<PageResult<WarframeOfficialRecordVO>>
        officialRecords(@RequestBody WarframeOfficialRecordQuery request) {
        return ApiResponse.ok(officialDataQueryService.query(request));
    }

    /**
     * 读取单个官方文件的版本、业务数量与只读包头。
     *
     * @param domain 官方数据领域代码。
     * @return 文件概览。
     */
    @PostMapping("/records/overview/{domain}")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<WarframeOfficialDatasetOverviewVO> officialOverview(@PathVariable String domain) {
        return ApiResponse.ok(officialDataQueryService.overview(domain));
    }

    /**
     * 读取官方记录详情及其子表内容。
     *
     * @param id 目标记录的本地主键。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/records/{id}/detail")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<WarframeOfficialRecordVO> officialDetail(@PathVariable long id) {
        return ApiResponse.ok(officialDataQueryService.detail(id));
    }

    /**
     * 读取指定官方数据分类的自维护表单字段合同。
     *
     * @param domain 官方数据文件所属的业务领域代码。
     * @return 以统一接口响应包装的字段合同列表。
     */
    @PostMapping("/records/form-schema/{domain}")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<List<WarframeOfficialFormFieldVO>> officialRecordFormSchema(@PathVariable String domain) {
        return ApiResponse.ok(officialDataQueryService.formSchema(domain));
    }

    /**
     * 在官方模型中创建自维护记录。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/records")
    @SaCheckPermission("warframe:game:update")
    public ApiResponse<Long> createSelfMaintained(@RequestBody WarframeOfficialRecordSaveDTO request) {
        return ApiResponse.ok(officialMaintenanceService.save(null, request).getId());
    }

    /**
     * 通过统一写入口保存记录及其全部必要子表。
     *
     * @param id 目标记录的本地主键。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/records/{id}")
    @SaCheckPermission("warframe:game:update")
    public ApiResponse<Long> saveOfficialTree(@PathVariable long id,
        @RequestBody WarframeOfficialRecordSaveDTO request) {
        return ApiResponse.ok(officialMaintenanceService.save(id, request).getId());
    }

    /**
     * 通过统一维护服务删除记录及其子表。
     *
     * @param id 目标记录的本地主键。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/records/{id}/delete")
    @SaCheckPermission("warframe:game:update")
    public ApiResponse<Void> deleteOfficialRecord(@PathVariable long id) {
        officialMaintenanceService.delete(id);
        return ApiResponse.ok();
    }

    /**
     * 将指定官方记录转换为自维护状态。
     *
     * @param id 目标记录的本地主键。
     * @param confirmed 调用方是否已确认当前变更。
     * @param replaceExisting 是否允许替换同身份已有数据。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/records/{id}/self-maintained")
    @SaCheckPermission("warframe:game:update")
    public ApiResponse<Long> convertOfficialRecord(@PathVariable long id,
        @RequestParam(defaultValue = "false") boolean confirmed,
        @RequestParam(defaultValue = "false") boolean replaceExisting) {
        return ApiResponse
            .ok(officialMaintenanceService.convertToSelfMaintained(id, confirmed, replaceExisting).getId());
    }

}
