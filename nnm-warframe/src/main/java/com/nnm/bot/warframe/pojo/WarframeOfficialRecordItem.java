package com.nnm.bot.warframe.pojo;

import java.time.LocalDateTime;
import java.util.Map;

/** 官方 PublicExport 的领域化管理端列表项。 */
public record WarframeOfficialRecordItem(
        Long id,
        String domain,
        String uniqueName,
        String exportFile,
        String exportType,
        String recordType,
        String officialName,
        String localizedName,
        String parentName,
        String productCategory,
        Boolean excludeFromCodex,
        String description,
        String longDescription,
        String wikiSlug,
        String marketSlug,
        boolean enabled,
        String rawJson,
        String localizedRawJson,
        Map<String, Object> attributes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
