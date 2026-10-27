package com.nnm.bot.warframe.persistent.curated.pojo.query.reference;

/** 社区资料列表筛选和分页参数；JSON 字段与管理端合同保持一致。 */
public record WarframeCommunityQuery(String keyword, Integer current, Integer size) {
}
