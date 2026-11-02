package com.nnm.bot.warframe.persistent.official.service.dataset;

import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRawRecordDTO;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialPreviewDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordSelectionDTO;
import java.util.List;

public interface WarframeOfficialImportService {
    /**
     * 列出可选的官方数据版本。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeOfficialVersionDTO> versions();

    /**
     * 获取指定官方 JSON 并生成待审核预览。
     *
     * @param ref 明确的官方数据 tag。
     * @param productPath 官方 JSON 文件在发布包内的相对路径。
     * @param progress 进度。
     * @return 游戏资料数据文件预览对象。
     */
    WarframeOfficialPreviewDTO refresh(String ref, String productPath, TaskProgressReporter progress);

    /**
     * 刷新选定版本中的全部官方 JSON 文件。
     *
     * @param ref 明确的官方数据 tag。
     * @param progress 向管理端任务提交进度的回调。
     * @return 游戏资料数据文件预览对象。
     */
    WarframeOfficialPreviewDTO refreshAll(String ref, TaskProgressReporter progress);

    /**
     * 生成变更预览，供后续确认采纳。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 游戏资料数据文件预览对象。
     */
    WarframeOfficialPreviewDTO preview(String id);

    /**
     * 列出当前仍可审核的官方数据预览。
     *
     * @return 按创建时间倒序排列的待审核预览。
     */
    List<WarframeOfficialPreviewDTO> pendingPreviews();

    /**
     * 丢弃指定的待审核官方数据预览。
     *
     * @param id 审核批次标识。
     */
    void discard(String id);

    /**
     * 对比当前数据并生成待采纳变更。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @param current 从一开始的当前页码。
     * @param size 当前操作允许的数据数量。
     * @return 包含当前页记录及总数的分页结果。
     */
    PageResult<WarframeOfficialRecordChangeDTO> changes(String id, int current, int size, String changeType);

    /**
     * 按版本快照和稳定业务键读取单条记录原文。
     *
     * @param id 审核批次标识。
     * @param path 文件相对路径。
     * @param recordKey 记录稳定业务键。
     * @return 两个版本的原始记录。
     */
    WarframeOfficialRawRecordDTO record(String id, String path, String recordKey);

    /**
     * 采纳已经确认的业务变更。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 游戏资料数据文件预览对象。
     */
    WarframeOfficialPreviewDTO apply(String id, List<WarframeOfficialRecordSelectionDTO> changes);
}
