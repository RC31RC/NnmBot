package com.nnm.bot.warframe.pojo;

import java.time.LocalDateTime;
import java.util.Map;

/** Warframe 物品管理列表项。 */
public record WarframeItemItem(
        Long id,
        String sourceKey,
        String exportType,
        String recordType,
        String officialName,
        String localizedName,
        String parentName,
        String productCategory,
        String description,
        String longDescription,
        String wikiSlug,
        String marketSlug,
        String sourceType,
        String sourceVersion,
        boolean enabled,
        long aliasCount,
        String rawJson,
        String localizedRawJson,
        Map<String, Object> attributes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
