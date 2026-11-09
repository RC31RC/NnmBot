package com.nnm.bot.warframe.runtime.pojo.dto.market;

import com.nnm.bot.warframe.runtime.constant.WarframeMarketConstants.OrderFilter;

/** 机器人市场命令语法参数；不携带解析后才确定的市场物品标识。 */
public record WarframeMarketCommandDTO(String itemInput, String platform, OrderFilter filter, boolean maxLevel,
    int page) {
}
