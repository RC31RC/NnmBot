package com.nnm.bot.warframe.pojo;

import java.math.BigDecimal;

public record WarframeCommunityRelicRewardDetails(
        String relicRecordKey,
        String rewardName,
        String uniqueName,
        String marketId,
        String urlName,
        String rarity,
        BigDecimal chance,
        Integer rewardOrder
) implements WarframeCommunityDatasetDetails {
}
