package com.nnm.bot.warframe.dto;

public record WarframeCommunityMarketRivenTionDetails(
        Long sourceId,
        String effect,
        String attributeGroup,
        Boolean negativeOnly,
        Boolean positiveIsNegative,
        String prefix,
        Boolean searchOnly,
        String suffix,
        String units,
        String urlName,
        String exclusiveToJson
) implements WarframeCommunityDatasetDetails {
}
