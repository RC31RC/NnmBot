package com.nnm.bot.warframe.persistent.official.pojo.query.items;

/** 商店物品分页命令的检索词与校验结果。 */
public record WarframeItemPageQuery(String keyword, int page, String error) {
}
