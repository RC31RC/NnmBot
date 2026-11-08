package com.nnm.bot.warframe.persistent.official.pojo.query.crafting;

/** 配方的种类、检索词和分页。 */
public record WarframeRecipeQuery(String keyword, String kind, int page, String error) {
}
