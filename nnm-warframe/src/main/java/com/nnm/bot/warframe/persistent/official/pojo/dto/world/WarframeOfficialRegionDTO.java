package com.nnm.bot.warframe.persistent.official.pojo.dto.world;

import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;

/**
 * 官方星图节点及其 Region 结构化字段。
 */
public record WarframeOfficialRegionDTO(
        WarframeOfficialRecordReadDTO record,
        String systemName,
        Integer systemIndex,
        Integer nodeType,
        Integer masteryReq,
        Integer minEnemyLevel,
        Integer maxEnemyLevel
) {
}
