package com.nnm.bot.warframe.persistent.official.service.upgrades;

import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeUpgradeVO;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;

public interface WarframeUpgradeService {
    /**
     * 按给定业务条件查询振幅晶体及升级定义。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keyword 筛选使用的关键词。
     * @param rank 等级。
     * @return 单个振幅晶体或升级的展示条目组成的列表。
     */
    List<WarframeUpgradeVO.EntryVO> find(Category dataset, String keyword, Integer rank);

    /**
     * 读取本业务直接使用的已采纳振幅晶体及升级定义。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    List<WarframeOfficialRecordReadDTO> selectedRecords(Category dataset);
}
