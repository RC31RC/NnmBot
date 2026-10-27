package com.nnm.bot.warframe.persistent.official.pojo.query;

/** Warframe 官方 PublicExport 领域分页查询参数。 */
public record WarframeOfficialRecordQuery(
        String domain,
        String keyword,
        Integer current,
        Integer size,
        String maintenanceType
) {
}
