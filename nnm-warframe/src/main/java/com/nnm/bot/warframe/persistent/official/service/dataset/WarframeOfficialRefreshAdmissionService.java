package com.nnm.bot.warframe.persistent.official.service.dataset;

import com.nnm.bot.common.task.AdminTaskSubmission;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialApplyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRefreshConflictDTO;

/** 官方全量与单文件刷新任务、待审核预览的原子准入。 */
public interface WarframeOfficialRefreshAdmissionService {
    /**
     * 查询指定路径的刷新冲突；全量刷新时路径为 null。
     *
     * @param productPath 单文件路径；全量为 null。
     * @return 当前实际执行中的任务与尚未过期的冲突预览。
     */
    WarframeOfficialRefreshConflictDTO conflicts(String productPath);

    /**
     * 无任务、无预览冲突时提交后台刷新任务。
     *
     * @param ref 管理员选定的发布 tag。
     * @param productPath 单文件路径；全量为 null。
     * @return 已登记的后台任务标识。
     */
    AdminTaskSubmission submit(String ref, String productPath);

    /**
     * 在同一准入锁下丢弃冲突预览并重新提交刷新。
     *
     * @param ref 管理员选定的发布 tag。
     * @param productPath 单文件路径；全量为 null。
     * @return 已登记的后台任务标识。
     */
    AdminTaskSubmission discardAndSubmit(String ref, String productPath);

    /**
     * 与刷新共用准入锁，审核中的批次不可被刷新或丢弃抢先处理。
     *
     * @param id 待审批次 ID。
     * @param selection 待采纳选择。
     * @return 后台采纳任务。
     */
    AdminTaskSubmission submitApply(String id, WarframeOfficialApplyDTO selection);

    /**
     * 在共享准入锁内丢弃待审预览，不能越过已排队或执行中的采纳。
     *
     * @param id 待丢弃批次 ID。
     */
    void discardPreview(String id);

    /** 仅重试同一审批中失败的文件，不重新选择或采纳已完成分类。 */
    AdminTaskSubmission retryApply(String id);
}
