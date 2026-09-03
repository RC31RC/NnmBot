package com.nnm.bot.warframe.dto;

public record WarframeCommunityAliasDetails(
        Long sourceId,
        String aliasName,
        String targetName
) implements WarframeCommunityDatasetDetails {
}
