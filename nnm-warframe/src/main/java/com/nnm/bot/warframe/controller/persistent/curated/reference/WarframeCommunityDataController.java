package com.nnm.bot.warframe.controller.persistent.curated.reference;

import com.nnm.bot.warframe.persistent.curated.mapper.reference.WarframeCommunityViewMapper;
import com.nnm.bot.warframe.persistent.curated.pojo.query.reference.WarframeCommunityQuery;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityDatasetVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityRecordVO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPreviewDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityPreviewPageVO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityApplyDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityApplyVO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunitySourceDTO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityDataService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/admin/v1/warframe/community")
public class WarframeCommunityDataController {
    @Resource
    private WarframeCommunityDataService service;

    /**
     * 列出当前支持的数据文件。
     *
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/datasets")
    @SaCheckPermission("warframe:community:view")
    public ApiResponse<List<WarframeCommunityDatasetVO>> datasets() {
        return ApiResponse.ok(service.datasets());
    }

    /**
     * 执行当前业务域的查询并组织结果。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param query 当前业务的查询条件。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/{dataset}/query")
    @SaCheckPermission("warframe:community:view")
    public ApiResponse<PageResult<WarframeCommunityRecordVO>> query(
            @PathVariable
            String dataset,
            @RequestBody(required = false)
            WarframeCommunityQuery query) {
        return ApiResponse.ok(service.query(dataset, query));
    }

    /**
     * 生成变更预览，供后续确认采纳。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param input 待解析或校验的输入。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/{dataset}/preview")
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<WarframeCommunityPreviewPageVO> preview(
            @PathVariable
            String dataset,
            @RequestBody
            String input) {
        WarframeCommunityPreviewDTO preview = service.preview(dataset, input);
        return ApiResponse.ok(WarframeCommunityViewMapper.preview(service.previewPage(dataset, preview.id(), 1, 20)));
    }

    /**
     * 接收上传内容并交给对应业务服务处理。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param file 本次上传或读取的文件。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/{dataset}/upload-preview")
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<WarframeCommunityPreviewPageVO> upload(
            @PathVariable
            String dataset,
            @RequestPart("file")
            MultipartFile file) {
        WarframeCommunityPreviewDTO preview = service.uploadPreview(dataset, file);
        return ApiResponse.ok(WarframeCommunityViewMapper.preview(service.previewPage(dataset, preview.id(), 1, 20)));
    }

    /**
     * 获取当前配置的外部输入。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/{dataset}/pull-preview")
    @SaCheckPermission("warframe:community:sync")
    public ApiResponse<WarframeCommunityPreviewPageVO> pull(
            @PathVariable
            String dataset) {
        WarframeCommunityPreviewDTO preview = service.pullPreview(dataset);
        return ApiResponse.ok(WarframeCommunityViewMapper.preview(service.previewPage(dataset, preview.id(), 1, 20)));
    }

    /**
     * 读取已冻结预览中的一页变更。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param id 目标记录的本地主键或协议标识。
     * @param current 从一开始的当前页码。
     * @param size 当前操作允许的数据数量。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/{dataset}/preview/{id}")
    @SaCheckPermission("warframe:community:view")
    public ApiResponse<WarframeCommunityPreviewPageVO> previewPage(
            @PathVariable
            String dataset,
            @PathVariable
            String id,
            @RequestParam(defaultValue = "1")
            int current,
            @RequestParam(defaultValue = "20")
            int size) {
        return ApiResponse.ok(WarframeCommunityViewMapper.preview(service.previewPage(dataset, id, current, size)));
    }

    /**
     * 采纳已经确认的业务变更。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/{dataset}/apply")
    @SaCheckPermission("warframe:community:apply")
    public ApiResponse<WarframeCommunityApplyVO> apply(
            @PathVariable
            String dataset,
            @RequestBody
            WarframeCommunityApplyDTO request) {
        return ApiResponse.ok(service.apply(dataset, request));
    }

    /**
     * 删除指定业务记录并维护相关数据。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param id 目标记录的本地主键。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/{dataset}/records/{id}/delete")
    @SaCheckPermission("warframe:community:delete")
    public ApiResponse<Void> delete(
            @PathVariable
            String dataset,
            @PathVariable
            long id) {
        service.delete(dataset, id);
        return ApiResponse.ok();
    }

    /**
     * 取得或更新当前业务使用的数据来源。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping("/{dataset}/source")
    @SaCheckPermission("warframe:community:update")
    public ApiResponse<Void> source(
            @PathVariable
            String dataset,
            @RequestBody
            WarframeCommunitySourceDTO request) {
        service.source(dataset, request.rawUrl());
        return ApiResponse.ok();
    }
}
