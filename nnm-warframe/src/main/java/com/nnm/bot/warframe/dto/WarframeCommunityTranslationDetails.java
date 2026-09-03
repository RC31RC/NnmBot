package com.nnm.bot.warframe.dto;

public record WarframeCommunityTranslationDetails(
        Long sourceId,
        String chineseName,
        String englishName,
        Boolean primeItem,
        Boolean setItem
) implements WarframeCommunityDatasetDetails {
}
