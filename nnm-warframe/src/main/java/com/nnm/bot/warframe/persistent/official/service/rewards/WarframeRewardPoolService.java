package com.nnm.bot.warframe.persistent.official.service.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRewardPoolVO;
import java.util.List;

public interface WarframeRewardPoolService {
    /**
     * 按给定业务条件查询奖励池。
     *
     * @param keyword 筛选使用的关键词。
     * @param stage 阶段。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeRewardPoolVO.EntryVO> find(String keyword, Integer stage);
}
