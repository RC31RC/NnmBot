package com.nnm.bot.warframe.runtime.pojo.query.market;

import com.nnm.bot.warframe.runtime.constant.WarframeMarketConstants.OrderFilter;
import java.util.Locale;
import java.util.Objects;

/** 命令层完成名称解析后的订单查询条件；页码从 1 开始。 */
public record WarframeMarketOrderQuery(
        String itemInput, String marketSlug, String platform,
        OrderFilter filter, boolean maxLevel, int page) {

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

    /**
     * 创建附带市场地址标识的新数据对象。
     *
     * @param slug 市场物品的地址标识。
     * @return 游戏资料市场订单查询对象。
     */
    public WarframeMarketOrderQuery withMarketSlug(String slug) {
        return new WarframeMarketOrderQuery(itemInput, slug, platform, filter, maxLevel, page);
    }

    /** 订单买卖方向，与上游 type 字段对应。 */

}
