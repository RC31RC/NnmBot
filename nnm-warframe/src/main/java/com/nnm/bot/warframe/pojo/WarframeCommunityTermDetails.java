package com.nnm.bot.warframe.pojo;

public record WarframeCommunityTermDetails(
        String termGroup, String englishText, String chineseText)
        implements WarframeCommunityDatasetDetails {
}
