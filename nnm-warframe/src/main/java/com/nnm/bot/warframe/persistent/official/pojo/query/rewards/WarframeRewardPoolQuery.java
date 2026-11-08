package com.nnm.bot.warframe.persistent.official.pojo.query.rewards;

/** 奖励池筛选条件和阶段。 */
public record WarframeRewardPoolQuery(String keyword, Integer stage, int page, String error) {
}
