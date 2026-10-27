package com.nnm.bot.warframe.controller.persistent.official;

import com.nnm.bot.warframe.persistent.official.mapper.dataset.WarframeOfficialImportViewMapper;
import com.nnm.bot.common.task.AdminTaskService.AdminTaskWork;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.task.AdminTaskService;
import com.nnm.bot.common.task.AdminTaskSubmission;
import com.nnm.bot.warframe.persistent.official.pojo.vo.record.WarframeOfficialRecordVO;
import com.nnm.bot.warframe.persistent.official.pojo.query.WarframeOfficialRecordQuery;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialDataQueryService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialImportService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialMaintenanceService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialVersionVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialPreviewVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialRecordChangeVO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRefreshDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.maintenance.WarframeOfficialRecordSaveDTO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/** 管理端官方静态资料审核与自维护记录的 HTTP 入口。 */
@RestController
@RequestMapping("/api/admin/v1/warframe")
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
    /** 可追踪进度的后台任务服务。 */
    @Resource
    private AdminTaskService taskService;

    /**
     * 查询可选择的官方静态数据版本。
     *
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/official/versions")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<List<WarframeOfficialVersionVO>> officialVersions() {
        return ApiResponse.ok(datasetService.versions().stream().map(WarframeOfficialImportViewMapper::version).toList());
    }

    /**
     * 提交官方静态数据拉取任务，完成后生成待审核预览。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/official/refresh")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<AdminTaskSubmission> refreshOfficial(
            @RequestBody
            WarframeOfficialRefreshDTO request) {
        return ApiResponse.ok(submitTask(
                "WARFRAME_OFFICIAL_REFRESH:" + request.ref() + ":" + request.productPath(),
                "warframe:game:refresh",
                reporter -> datasetService.refresh(request.ref(), request.productPath(), reporter)));
    }

    /**
     * 读取指定官方静态数据预览。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/official/preview/{id}")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<WarframeOfficialPreviewVO> officialPreview(
            @PathVariable
            String id) {
        return ApiResponse.ok(WarframeOfficialImportViewMapper.preview(datasetService.preview(id)));
    }

    /**
     * 分页查询预览中的记录差异。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @param current 从一开始的当前页码。
     * @param size 当前操作允许的数据数量。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/official/preview/{id}/changes")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<PageResult<WarframeOfficialRecordChangeVO>> officialChanges(
            @PathVariable
            String id,
            @RequestParam(defaultValue = "1")
            int current,
            @RequestParam(defaultValue = "20")
            int size) {
        return ApiResponse.ok(WarframeOfficialImportViewMapper.changes(datasetService.changes(id, current, size)));
    }

    /**
     * 提交已审核预览的应用任务，更新对应官方独立数据表。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/official/preview/{id}/apply")
    @SaCheckPermission("warframe:game:refresh")
    public ApiResponse<AdminTaskSubmission> applyOfficial(
            @PathVariable
            String id) {
        return ApiResponse.ok(submitTask(
                "WARFRAME_OFFICIAL_APPLY:" + id, "warframe:game:refresh",
                reporter -> datasetService.apply(id)));
    }

    /**
     * 按数据文件和筛选条件分页查询官方记录。
     *
     * @param request 本次查询的筛选与分页参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/official/records/query")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<PageResult<WarframeOfficialRecordVO>> officialRecords(
            @RequestBody
            WarframeOfficialRecordQuery request) {
        return ApiResponse.ok(officialDataQueryService.query(request));
    }

    /**
     * 读取官方记录详情及其子表内容。
     *
     * @param id 目标记录的本地主键。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/official/records/{id}/detail")
    @SaCheckPermission("warframe:game:view")
    public ApiResponse<WarframeOfficialRecordVO> officialDetail(
            @PathVariable
            long id) {
        return ApiResponse.ok(officialDataQueryService.detail(id));
    }

    /**
     * 在官方模型中创建自维护记录。
     *
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/official/records")
    @SaCheckPermission("warframe:game:update")
    public ApiResponse<Long> createSelfMaintained(
            @RequestBody
            WarframeOfficialRecordSaveDTO request) {
        return ApiResponse.ok(officialMaintenanceService.save(null, request).getId());
    }

    /**
     * 通过统一写入口保存记录及其全部必要子表。
     *
     * @param id 目标记录的本地主键。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/official/records/{id}")
    @SaCheckPermission("warframe:game:update")
    public ApiResponse<Long> saveOfficialTree(
            @PathVariable
            long id,
            @RequestBody
            WarframeOfficialRecordSaveDTO request) {
        return ApiResponse.ok(officialMaintenanceService.save(id, request).getId());
    }

    /**
     * 通过统一维护服务删除记录及其子表。
     *
     * @param id 目标记录的本地主键。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/official/records/{id}/delete")
    @SaCheckPermission("warframe:game:update")
    public ApiResponse<Void> deleteOfficialRecord(
            @PathVariable
            long id) {
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
    @PostMapping("/official/records/{id}/self-maintained")
    @SaCheckPermission("warframe:game:update")
    public ApiResponse<Long> convertOfficialRecord(
            @PathVariable
            long id,
            @RequestParam(defaultValue = "false")
            boolean confirmed,
            @RequestParam(defaultValue = "false")
            boolean replaceExisting) {
        return ApiResponse.ok(officialMaintenanceService
                .convertToSelfMaintained(id, confirmed, replaceExisting).getId());
    }

    /**
     * 复用同类活动任务或提交新任务，返回可供前端订阅的任务标识。
     *
     * @param taskType 任务类型。
     * @param permission 权限。
     * @param work 待执行任务。
     * @return 管理端任务任务提交对象。
     */
    private AdminTaskSubmission submitTask(
            String taskType,
            String permission,
            AdminTaskWork work) {
        return taskService.findActive(taskType)
                .map(existing -> new AdminTaskSubmission(existing, taskType))
                .orElseGet(() -> taskService.submit(taskType, permission, work));
    }

}
