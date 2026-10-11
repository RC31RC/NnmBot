package com.nnm.bot.warframe.pojo;

import java.util.List;

public record WarframeCommunityRewardPoolDetails(
        String poolKey,
        List<WarframeCommunityRewardItem> rewards
) implements WarframeCommunityDatasetDetails {
}
