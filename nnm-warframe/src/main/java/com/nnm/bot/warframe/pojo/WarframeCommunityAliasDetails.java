package com.nnm.bot.warframe.pojo;

public record WarframeCommunityAliasDetails(
        Long sourceId,
        String aliasName,
        String targetName
) implements WarframeCommunityDatasetDetails {
}
