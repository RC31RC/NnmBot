package com.nnm.bot.warframe.pojo;

public record WarframeCommunityRelicDetails(
        String relicName,
        String uniqueName,
        String category,
        String era,
        String icon,
        Boolean codexSecret,
        String descriptionKey,
        String quality,
        String rewardManifest,
        Long introducedAt,
        String marketId,
        String urlName,
        Boolean vaulted
) implements WarframeCommunityDatasetDetails {
}
