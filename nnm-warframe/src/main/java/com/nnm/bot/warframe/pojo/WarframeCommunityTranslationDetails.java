package com.nnm.bot.warframe.pojo;

public record WarframeCommunityTranslationDetails(
        Long sourceId,
        String locale,
        String displayName,
        Boolean primeItem,
        Boolean setItem
) implements WarframeCommunityDatasetDetails {
}
