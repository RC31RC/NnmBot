package com.nnm.bot.warframe.dto;

public record WarframeCommunityStateTranslationDetails(
        String uniqueName,
        String displayName,
        String description
) implements WarframeCommunityDatasetDetails {
}
