package com.nnm.bot.warframe.pojo;

import java.math.BigDecimal;

public record WarframeCommunityRelicDropDetails(
        String relicName,
        String relicRecordKey,
        String locationText,
        String rarity,
        BigDecimal chance,
        Integer dropOrder
) implements WarframeCommunityDatasetDetails {
}
