package com.nnm.bot.warframe.pojo;

public record WarframeCommunityNodeDetails(
        String uniqueName,
        String displayName,
        Integer systemIndex,
        String systemName,
        Integer nodeType,
        Integer masteryReq,
        Integer missionIndex,
        Integer factionIndex,
        Integer minEnemyLevel,
        Integer maxEnemyLevel
) implements WarframeCommunityDatasetDetails {
}
