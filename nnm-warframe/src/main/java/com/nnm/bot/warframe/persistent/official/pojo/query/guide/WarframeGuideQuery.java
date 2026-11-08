package com.nnm.bot.warframe.persistent.official.pojo.query.guide;

/** 鱼类或地图攻略的查询字段与命令语法错误。 */
public record WarframeGuideQuery(String keyword, String world, String layer, int page, String error) {
}
