package com.nnm.bot.warframe.persistent.official.pojo.dto.record;

import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;

/**
 * 跨官方 JSON 查询使用的通用读取投影；业务字段保留为各文件的具体 DTO。
 */
public record WarframeOfficialRecordReadDTO(
        Long databaseId,
        WarframeOfficialDataDomain domain,
        String bkey,
        String productPath,
        String domainCode,
        String officialName,
        String localizedName,
        String parentName,
        String productCategory,
        String description,
        String longDescription,
        String wikiSlug,
        String marketSlug,
        OfficialObjectDTO data
) {
}
