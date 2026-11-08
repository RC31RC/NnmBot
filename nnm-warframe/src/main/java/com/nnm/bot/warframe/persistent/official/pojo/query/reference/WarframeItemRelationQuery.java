package com.nnm.bot.warframe.persistent.official.pojo.query.reference;

import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;

/** 组件/掉落检索的物品类型、关键字和分页；错误由命令层回复。 */
public record WarframeItemRelationQuery(String keyword, Category type, int page, String error) {
}
