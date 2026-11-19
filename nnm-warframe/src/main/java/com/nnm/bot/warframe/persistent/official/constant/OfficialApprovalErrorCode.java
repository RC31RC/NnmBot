package com.nnm.bot.warframe.persistent.official.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 官方文件采纳的持久状态错误，与上游协议故障及内部不变量区分。 */
@Getter
@RequiredArgsConstructor
public enum OfficialApprovalErrorCode implements ErrorCode {
    PARTIAL_APPLY_FAILED(40932, "官方审批部分文件入库失败；已完成的文件保持可见，请查看文件进度", HttpStatus.CONFLICT),
    APPLY_STATE_INCONSISTENT(50011, "官方审批状态不完整，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    SAVED_SNAPSHOT_CORRUPTED(50012, "官方审批快照无法读取，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    PREPARATION_RETRYABLE(50330, "官方审批准备未完成，后台将重试", HttpStatus.SERVICE_UNAVAILABLE),
    APPLY_WAIT_INTERRUPTED(50331, "官方审批等待已中断，入库任务仍在后台继续处理", HttpStatus.SERVICE_UNAVAILABLE),
    FIELD_DIFF_SERIALIZATION_FAILED(50013, "官方审批字段差异序列化失败，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_CATEGORY_NOT_SELECTED(400181, "所选文件分类不属于待审核预览", HttpStatus.BAD_REQUEST),
    SELECTION_DUPLICATE(400182, "预览选择包含重复或相互冲突的记录", HttpStatus.BAD_REQUEST),
    RECORD_NOT_SELECTED(400183, "所选记录不属于待审核预览", HttpStatus.BAD_REQUEST),
    EXCLUDED_RECORD_INVALID(400184, "排除记录不属于已选文件分类", HttpStatus.BAD_REQUEST),
    SELECTED_RECORD_CHANGED(400185, "所选记录已不属于待审核预览", HttpStatus.BAD_REQUEST),
    FILE_NOT_IN_PREVIEW(400186, "所选文件不属于待审核预览", HttpStatus.BAD_REQUEST),
    CHANGE_CATEGORY_INVALID(400187, "预览差异分类不合法", HttpStatus.BAD_REQUEST),
    SAVED_PREVIEW_CORRUPTED(50014, "已保存预览损坏，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    SAVED_MANIFEST_CORRUPTED(50015, "已保存清单损坏，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    ARTIFACT_READ_FAILED(502120, "官方成品暂存或读取失败；未完成的批次不会成为生效数据", HttpStatus.BAD_GATEWAY),
    REFRESH_BUSY(409231, "官方数据正在刷新或采纳，请等待当前任务结束", HttpStatus.CONFLICT),
    DISCARD_WHILE_APPLYING(409232, "官方数据正在采纳，不能丢弃审核批次", HttpStatus.CONFLICT),
    PREVIEW_REQUIRES_REVIEW(409233, "已有未过期的官方预览，须先审核或丢弃", HttpStatus.CONFLICT),
    APPLY_IN_PROGRESS(409234, "该预览正在采纳，请等待任务结束", HttpStatus.CONFLICT),
    OTHER_APPLY_IN_PROGRESS(409235, "另一批官方预览正在采纳", HttpStatus.CONFLICT),
    FILE_PATH_UNKNOWN(400231, "未登记的官方 JSON 文件路径", HttpStatus.BAD_REQUEST),
    RECORD_DETAILS_MISSING(409236, "数据库中缺少官方记录详情", HttpStatus.CONFLICT),
    FILE_SELECTION_INVALID(400232, "解析结果含未选定文件", HttpStatus.BAD_REQUEST),
    DELETED_KEY_STILL_PRESENT(409237, "待删除键仍在候选文件中", HttpStatus.CONFLICT),
    APPROVED_KEY_MISSING(409238, "获准的业务键不在候选文件中", HttpStatus.CONFLICT),
    FILE_OWNER_EXPIRED(409239, "文件认领已过期，不能继续提交", HttpStatus.CONFLICT),
    FILE_CONTRACT_MISSING(400233, "没有对应的官方 JSON 文件契约", HttpStatus.BAD_REQUEST),
    RIVEN_LISTINGS_MISSING(400234, "裂罅武器商品列表不能为空", HttpStatus.BAD_REQUEST),
    RIVEN_LISTING_ID_INVALID(400235, "裂罅商品稳定 id 缺失或重复", HttpStatus.BAD_REQUEST),
    SELECTION_REQUIRED(400236, "请至少选择一条待审核差异", HttpStatus.BAD_REQUEST),
    SELECTION_FILE_INVALID(400237, "选择中包含不属于此预览的文件", HttpStatus.BAD_REQUEST),
    STATIC_PATH_UNKNOWN(400238, "未登记的静态文件路径", HttpStatus.BAD_REQUEST),
    CHANGE_TYPE_INVALID(400239, "变更分类不合法", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
