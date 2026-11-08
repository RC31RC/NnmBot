package com.nnm.bot.warframe.persistent.official.pojo.query.catalog;

/** 官方资料分页命令的结构化检索条件；语法错误由 Plugin 回复。 */
public record WarframeCatalogPageQuery(String keyword, int page, String error) {
}
