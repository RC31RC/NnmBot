package com.nnm.bot.warframe.pojo;

import java.util.Locale;
import java.util.Objects;

/** 命令层完成名称解析后的订单查询条件；页码从 1 开始。 */
public record WarframeMarketOrderQuery(
        String itemInput, String marketSlug, String platform,
        Filter filter, boolean maxLevel, int page) {

    public WarframeMarketOrderQuery {
        itemInput = Objects.requireNonNullElse(itemInput, "").trim();
        marketSlug = Objects.requireNonNullElse(marketSlug, "").trim().toLowerCase(Locale.ROOT);
        platform = Objects.requireNonNullElse(platform, "").trim().toLowerCase(Locale.ROOT);
        filter = Objects.requireNonNullElse(filter, Filter.ALL);
        page = Math.max(1, page);
    }

    public WarframeMarketOrderQuery withMarketSlug(String slug) {
        return new WarframeMarketOrderQuery(itemInput, slug, platform, filter, maxLevel, page);
    }

    /** 订单买卖方向，与上游 type 字段对应。 */
    public enum Filter {
        ALL, BUY, SELL;

        public boolean accepts(String type) {
            return ("buy".equals(type) || "sell".equals(type))
                    && (this == ALL || name().equalsIgnoreCase(type));
        }
    }
}
