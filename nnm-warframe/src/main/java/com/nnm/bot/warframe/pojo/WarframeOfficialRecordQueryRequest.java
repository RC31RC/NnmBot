package com.nnm.bot.warframe.pojo;

/** Warframe 官方 PublicExport 领域分页查询参数。 */
public record WarframeOfficialRecordQueryRequest(
        String domain,
        String keyword,
        Integer current,
        Integer size
) {
}
