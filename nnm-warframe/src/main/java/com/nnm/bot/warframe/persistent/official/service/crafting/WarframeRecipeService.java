package com.nnm.bot.warframe.persistent.official.service.crafting;

import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeRecipeVO;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;

public interface WarframeRecipeService {
    /**
     * 读取本业务直接使用的已采纳制作配方。
     *
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    List<WarframeOfficialRecordReadDTO> selectedRecords();

    /**
     * 按给定业务条件查询制作配方。
     *
     * @param keyword 筛选使用的关键词。
     * @param kind 字段在当前记录中的实际类型。
     * @return 单个配方及材料的展示条目组成的列表。
     */
    List<WarframeRecipeVO.EntryVO> find(String keyword, String kind);

    /**
     * 按给定筛选条件分页读取制作配方。
     *
     * @param keyword 筛选使用的关键词。
     * @param kind 字段在当前记录中的实际类型。
     * @param page 从一开始的页码。
     * @return 制作配方的分页视图。
     */
    WarframeRecipeVO page(String keyword, String kind, int page);
}
