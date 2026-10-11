package com.nnm.bot.warframe.pojo;

public record WarframeCommunityRivenAnalyseTrendDetails(
        Long sourceId,
        String displayName,
        String prefix,
        String suffix,
        Double archwing,
        Double meleeValue,
        Double pistol,
        Double rifle,
        Double shotgun
) implements WarframeCommunityDatasetDetails {
}
