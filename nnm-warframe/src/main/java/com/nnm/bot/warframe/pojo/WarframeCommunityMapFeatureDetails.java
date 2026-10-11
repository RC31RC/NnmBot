package com.nnm.bot.warframe.pojo;

public record WarframeCommunityMapFeatureDetails(
        String world,
        String featureType,
        String name,
        String geometryType
) implements WarframeCommunityDatasetDetails {
}
