package com.nnm.bot.warframe.pojo;

public record WarframeCommunityRivenTrendDetails(
        Long sourceId,
        String trendTime,
        String newDot,
        Double newNum,
        String oldDot,
        Double oldNum,
        String trendName,
        String weaponType
) implements WarframeCommunityDatasetDetails {
}
