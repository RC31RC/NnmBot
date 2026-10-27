package com.nnm.bot.warframe.persistent.official.service.world;

import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeSynthTargetVO;
import java.util.List;

public interface WarframeSynthTargetService {
    /**
     * 按给定业务条件查询结合仪式目标。
     *
     * @param keyword 筛选使用的关键词。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeSynthTargetVO.EntryVO> find(String keyword);

    /**
     * 按给定筛选条件分页读取结合仪式目标。
     *
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 结合仪式目标的分页视图。
     */
    WarframeSynthTargetVO page(String keyword, int page);
}
