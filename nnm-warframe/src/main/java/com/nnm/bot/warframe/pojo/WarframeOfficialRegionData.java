package com.nnm.bot.warframe.pojo;

/**
 * 官方星图节点及其 Region 结构化字段。
 */
public record WarframeOfficialRegionData(
        WarframeOfficialDataRecord record,
        String systemName,
        Integer systemIndex,
        Integer nodeType,
        Integer masteryReq,
        Integer missionIndex,
        Integer factionIndex,
        Integer minEnemyLevel,
        Integer maxEnemyLevel
) {
}
