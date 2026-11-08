package com.nnm.bot.warframe.persistent.official.pojo.query.crafting;

/** 材料命令的检索词和页码，语法错误由 Plugin 转换成拒绝回复。 */
public record WarframeMaterialQuery(String keyword, int page, String error) {
}
