package com.nnm.bot.warframe.pojo;

import java.util.List;

public record WarframeBountyDefinitionDetails(
        String jobType, String nameKey, String descriptionKey, String icon,
        List<StageOption> stages) implements WarframeCommunityDatasetDetails {
    public record StageOption(int stage, int option, String jobReference) {
    }
}
