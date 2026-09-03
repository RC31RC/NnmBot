package com.nnm.bot.warframe.dto;

public record WarframeCommunityMarketElementDetails(
        Long sourceId,
        String elementCode,
        String displayName
) implements WarframeCommunityDatasetDetails {
}
