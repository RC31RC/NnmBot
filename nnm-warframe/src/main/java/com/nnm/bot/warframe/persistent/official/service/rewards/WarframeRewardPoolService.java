package com.nnm.bot.warframe.persistent.official.service.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRewardPoolVO;
import java.util.Collection;
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

    /**
     * 按动态资料实际引用的奖励池批量读取，不装载其余奖励池。
     *
     * @param poolKeys 本次奖励池引用。
     * @return 这些奖励池的阶段奖励。
     */
    List<WarframeRewardPoolVO.EntryVO> findAll(Collection<String> poolKeys);
}
