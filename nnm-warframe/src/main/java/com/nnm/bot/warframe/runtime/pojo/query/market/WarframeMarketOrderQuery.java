package com.nnm.bot.warframe.runtime.pojo.query.market;

import com.nnm.bot.warframe.runtime.constant.WarframeMarketConstants.OrderFilter;
import java.util.Locale;
import java.util.Objects;

/** 业务服务使用的订单查询条件；页码从 1 开始。 */
public record WarframeMarketOrderQuery(String itemInput, String marketSlug, String platform, OrderFilter filter,
    boolean maxLevel, int page) {

    /**
     * 构造市场订单查询，并保存初始化参数。
     *
     * @param itemInput 物品输入。
     * @param marketSlug 市场地址标识。
     * @param platform 市场或官方接口使用的平台代码。
     * @param filter 筛选。
     * @param maxLevel 最大等级。
     * @param page 从一开始的页码。
     */
    public WarframeMarketOrderQuery {
        itemInput = Objects.requireNonNullElse(itemInput, "").trim();
        marketSlug = Objects.requireNonNullElse(marketSlug, "").trim().toLowerCase(Locale.ROOT);
        platform = Objects.requireNonNullElse(platform, "").trim().toLowerCase(Locale.ROOT);
        filter = Objects.requireNonNullElse(filter, OrderFilter.ALL);
        page = Math.max(1, page);
    }

}
