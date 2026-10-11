package com.nnm.bot.warframe.pojo;

import java.math.BigDecimal;

/** 奖励池投影；概率和稀有度相互独立，缺失时保留 null。 */
public record WarframeCommunityRewardItem(
        int stage,
        String itemKey,
        String displayName,
        int count,
        BigDecimal probability,
        String rarity) {
}
