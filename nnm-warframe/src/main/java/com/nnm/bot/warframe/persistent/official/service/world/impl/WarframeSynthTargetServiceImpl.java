package com.nnm.bot.warframe.persistent.official.service.world.impl;

import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeSynthTargetVO;
import com.nnm.bot.warframe.persistent.official.service.world.WarframeSynthTargetService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WarframeSynthTargetServiceImpl implements WarframeSynthTargetService {
    /**
     * 按给定业务条件查询结合仪式目标。
     *
     * @param keyword 筛选使用的关键词。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeSynthTargetVO.EntryVO> find(String keyword) {
        // The published enemy catalog has no synthesis-target/location definition.
        // Do not infer guide data from unrelated enemy records or revive the retired community source.
        return List.of();
    }

    /**
     * 按给定筛选条件分页读取结合仪式目标。
     *
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 结合仪式目标的分页视图。
     */
    @Override
    public WarframeSynthTargetVO page(String keyword, int page) {
        return WarframeSynthTargetVO.page(keyword, find(keyword), page);
    }
}
