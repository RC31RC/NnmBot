package com.nnm.bot.warframe.pojo;

import com.nnm.bot.warframe.constant.WarframeOfficialDataDomain;

/**
 * 命令侧可读取的官方 PublicExport 公共记录。
 */
public record WarframeOfficialDataRecord(
        Long databaseId,
        WarframeOfficialDataDomain domain,
        String uniqueName,
        String exportFile,
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
        String rawJson,
        String localizedRawJson
) {
}
