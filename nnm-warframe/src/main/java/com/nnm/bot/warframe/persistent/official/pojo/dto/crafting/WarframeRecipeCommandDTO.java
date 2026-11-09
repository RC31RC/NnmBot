package com.nnm.bot.warframe.persistent.official.pojo.dto.crafting;

/** 配方的种类、检索词和分页。 */
public record WarframeRecipeCommandDTO(String keyword, String kind, int page, String error) {
}
