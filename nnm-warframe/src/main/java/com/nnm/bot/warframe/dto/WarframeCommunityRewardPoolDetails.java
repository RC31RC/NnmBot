package com.nnm.bot.warframe.dto;

public record WarframeCommunityRewardPoolDetails(
        String uniqueName,
        Integer rewardCount,
        String rewardsJson
) implements WarframeCommunityDatasetDetails {
}
