package com.nnm.bot.warframe.pojo;

public record WarframeWorldStateCodebookDetails(
        String locale,
        String recordType,
        String displayName,
        String displayText,
        String displayTextZh,
        String displayTextEn,
        String factionReference,
        String missionTypeReference,
        Integer tierNumber,
        String description
) implements WarframeCommunityDatasetDetails {
}
