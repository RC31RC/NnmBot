package com.nnm.bot.warframe.persistent.official.service.dataset;

import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialApplyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialApplyFileDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialPreviewDTO;
import java.util.List;

/** 一次审批冻结后，多个实例逐文件认领并顺序提交分类事务。 */
public interface WarframeOfficialApplyWorkService {
    /**
     * 冻结本次审批选择；同批次不得再次提交不同选择。
     *
     * @param batchId 待审批次标识。
     * @param selection 本次审核的选择。
     */
    void freeze(String batchId, WarframeOfficialApplyDTO selection);

    /** 当前实例串行处理所有可认领文件，空闲后返回；不同实例可领取不同文件。 */
    void processAvailable();

    /**
     * 保留现有管理任务回显，业务执行与崩溃恢复均以数据库为准。
     *
     * @param batchId 审批批次标识。
     * @param reporter 管理端任务进度接收器。
     * @return 审批结束后的预览状态。
     */
    WarframeOfficialPreviewDTO await(String batchId, TaskProgressReporter reporter);

    /**
     * 只重置同一次审批已失败的文件，不接受第二次选择。
     *
     * @param batchId 审批批次标识。
     */
    void retry(String batchId);

    /**
     * 返回已冻结审批各文件的分类进度。
     *
     * @param batchId 审批批次标识。
     * @return 各文件的认领状态和已提交分类。
     */
    List<WarframeOfficialApplyFileDTO> files(String batchId);
}
