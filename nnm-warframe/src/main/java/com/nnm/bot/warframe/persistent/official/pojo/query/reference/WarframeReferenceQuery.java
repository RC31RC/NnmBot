package com.nnm.bot.warframe.persistent.official.pojo.query.reference;

import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;

/**
 * 按字段所属资料类型查找引用，同一短码在不同资料类型中可以有独立含义。
 *
 * @param reference 字段原始引用。
 * @param type 所属资料类型。
 */
public record WarframeReferenceQuery(String reference, WarframeReferenceType type) {
}
