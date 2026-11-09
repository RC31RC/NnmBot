package com.nnm.bot.warframe.persistent.official.pojo.dto.record;

/** 独立数据集的不可变名称投影，不持有 JPA 实体或完整资料树。 */
public record WarframeOfficialNameDTO(Long databaseId, String bkey, String domainCode, String officialName,
    String localizedName, String parentName, String productCategory, String wikiSlug, String marketSlug) {
}
