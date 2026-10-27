package com.nnm.bot.warframe.persistent.official.service.dataset;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialPreviewDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import java.util.List;

public interface WarframeOfficialImportService {
    /**
     * 列出可选的官方数据版本。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeOfficialVersionDTO> versions();

    /**
     * 获取当前上游数据并更新本地有效状态。
     *
     * @param ref 明确的标签名称或完整提交哈希。
     * @param productPath 官方 JSON 文件在发布包内的相对路径。
     * @param progress 进度。
     * @return 游戏资料数据文件预览对象。
     */
    WarframeOfficialPreviewDTO refresh(String ref, String productPath, TaskProgressReporter progress);

    /**
     * 生成变更预览，供后续确认采纳。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 游戏资料数据文件预览对象。
     */
    WarframeOfficialPreviewDTO preview(String id);

    /**
     * 对比当前数据并生成待采纳变更。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @param current 从一开始的当前页码。
     * @param size 当前操作允许的数据数量。
     * @return 包含当前页记录及总数的分页结果。
     */
    PageResult<WarframeOfficialRecordChangeDTO> changes(String id, int current, int size);

    /**
     * 采纳已经确认的业务变更。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @return 游戏资料数据文件预览对象。
     */
    WarframeOfficialPreviewDTO apply(String id);
}
