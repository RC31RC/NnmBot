package com.nnm.bot.warframe.pojo;

/** Warframe 物品分页查询参数。 */
public record WarframeItemQueryRequest(
        String keyword,
        String sourceType,
        Integer current,
        Integer size
) {
}
