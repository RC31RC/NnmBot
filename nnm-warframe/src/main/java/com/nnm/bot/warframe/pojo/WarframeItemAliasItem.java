package com.nnm.bot.warframe.pojo;

import java.time.LocalDateTime;

/** Warframe 物品别名管理列表项。 */
public record WarframeItemAliasItem(
        Long id,
        Long itemId,
        String officialName,
        String localizedName,
        String aliasName,
        String normalizedAlias,
        String sourceType,
        LocalDateTime createdAt,
        String targetSourceKey,
        String targetTranslationId,
        String targetUniqueName,
        String resolutionStatus,
        String resolutionReason,
        String resolutionCandidates
) {

    public WarframeItemAliasItem(
            Long id,
            Long itemId,
            String officialName,
            String localizedName,
            String aliasName,
            String normalizedAlias,
            String sourceType,
            LocalDateTime createdAt) {
        this(
                id,
                itemId,
                officialName,
                localizedName,
                aliasName,
                normalizedAlias,
                sourceType,
                createdAt,
                null,
                null,
                null,
                null,
                null,
                null);
    }
}
