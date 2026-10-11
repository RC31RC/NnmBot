package com.nnm.bot.warframe.pojo;

public record WarframeCommunityMarketElementDetails(
        Long sourceId,
        String elementCode,
        String displayName
) implements WarframeCommunityDatasetDetails {
}
