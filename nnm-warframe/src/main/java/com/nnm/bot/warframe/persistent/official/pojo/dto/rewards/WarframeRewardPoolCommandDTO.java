package com.nnm.bot.warframe.persistent.official.pojo.dto.rewards;

/** 奖励池筛选条件和阶段。 */
public record WarframeRewardPoolCommandDTO(String keyword, Integer stage, int page, String error) {
}
