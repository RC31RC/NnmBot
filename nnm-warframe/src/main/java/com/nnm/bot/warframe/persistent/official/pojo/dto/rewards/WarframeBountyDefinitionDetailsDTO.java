package com.nnm.bot.warframe.persistent.official.pojo.dto.rewards;

import java.util.List;

public record WarframeBountyDefinitionDetailsDTO(
        String jobType, String nameKey, String descriptionKey, String icon,
        List<StageOptionDTO> stages) {
    public record StageOptionDTO(int stage, int option, String jobReference) {
    }
}
