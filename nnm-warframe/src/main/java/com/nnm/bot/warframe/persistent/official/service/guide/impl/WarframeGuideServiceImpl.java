package com.nnm.bot.warframe.persistent.official.service.guide.impl;

import com.nnm.bot.warframe.persistent.official.pojo.vo.guide.WarframeGuideVO;
import com.nnm.bot.warframe.persistent.official.service.guide.WarframeGuideService;
import org.springframework.stereotype.Service;
import java.util.List;

/** 当前官方契约没有钓鱼攻略和社区坐标图层；缺失资料保持空结果。 */
@Service
public class WarframeGuideServiceImpl implements WarframeGuideService {
    /**
     * 按鱼名和开放世界筛选钓鱼资料并分页。
     *
     * @param keyword 筛选使用的关键词。
     * @param world 世界。
     * @param page 从一开始的页码。
     * @return 钓鱼或地图资料的分页视图。
     */
    @Override
    public WarframeGuideVO<WarframeGuideVO.FishVO> fish(String keyword, String world, int page) {
        return WarframeGuideVO.page(keyword, world, null, List.of(), page);
    }

    /**
     * 按地点、开放世界及地图层级筛选地图资料并分页。
     *
     * @param keyword 筛选使用的关键词。
     * @param world 世界。
     * @param layer 图层。
     * @param page 从一开始的页码。
     * @return 钓鱼或地图资料的分页视图。
     */
    @Override
    public WarframeGuideVO<WarframeGuideVO.FeatureVO> map(String keyword, String world, String layer, int page) {
        return WarframeGuideVO.page(keyword, world, layer, List.of(), page);
    }
}
