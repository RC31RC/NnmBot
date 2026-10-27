package com.nnm.bot.warframe.persistent.official.pojo.vo.record;

import java.time.LocalDateTime;
import java.util.Map;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;

/** 官方 PublicExport 的领域化管理端列表项。 */
public record WarframeOfficialRecordVO(
        Long id,
        String domain,
        String bkey,
        String productPath,
        String domainCode,
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
        LocalDateTime updatedAt,
        MaintenanceType maintenanceType
) {
}
