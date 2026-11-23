package com.nnm.bot.warframe.persistent.official.pojo.dto.world;

import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;

/**
 * 官方星图节点及其 Region 结构化字段。
 *
 * @param record 已采纳的官方 Region 记录摘要。
 * @param systemName 上游系统英文名。
 * @param localizedSystemName 官方表中维护的系统中文名。
 * @param systemIndex 星系序号。
 * @param nodeType 节点类型。
 * @param masteryReq 解锁所需段位。
 * @param minEnemyLevel 最低敌人等级。
 * @param maxEnemyLevel 最高敌人等级。
 */
public record WarframeOfficialRegionDTO(WarframeOfficialRecordReadDTO record, String systemName,
    String localizedSystemName, Integer systemIndex, Integer nodeType, Integer masteryReq, Integer minEnemyLevel,
    Integer maxEnemyLevel) {
}
