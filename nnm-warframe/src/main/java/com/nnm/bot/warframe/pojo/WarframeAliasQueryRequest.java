package com.nnm.bot.warframe.pojo;

/** Warframe 物品别名分页查询参数。 */
public record WarframeAliasQueryRequest(
        String keyword,
        Long itemId,
        String sourceType,
        Integer current,
        Integer size
) {
}
