package com.nnm.bot.warframe.pojo;

public record WarframeCommunityStateTranslationDetails(
        String uniqueName,
        String displayName,
        String description
) implements WarframeCommunityDatasetDetails {
}
