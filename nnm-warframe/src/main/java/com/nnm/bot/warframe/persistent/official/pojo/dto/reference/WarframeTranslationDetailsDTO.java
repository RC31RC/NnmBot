package com.nnm.bot.warframe.persistent.official.pojo.dto.reference;

public record WarframeTranslationDetailsDTO(
        Long sourceId,
        String locale,
        String displayName,
        Boolean primeItem,
        Boolean setItem
) {
}
