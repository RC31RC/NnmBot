package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.common.response.PageResult;

import java.util.List;

/**
 * Service 返回的完整订单查询结果，包含详情和分页，供命令按用途选择展示字段。
 * WM handler 将其投影为 WarframeMarketOrderDisplay，输出渠道不再筛选业务字段。
 */
public record WarframeMarketOrderResult(
        @JsonPropertyDescription("物品")
        String itemName,

        @JsonPropertyDescription("查询内容")
        String query,

        @JsonPropertyDescription("市场物品标识")
        String marketSlug,

        @JsonPropertyDescription("平台")
        String platform,

        @JsonPropertyDescription("筛选条件")
        String filter,

        @JsonPropertyDescription("仅查询满级")
        boolean maxRankRequested,

        @JsonPropertyDescription("最高等级")
        Integer maxRank,

        @JsonIgnore
        PageResult<WarframeMarketOrderView> orderPage,

        @JsonPropertyDescription("最低售价")
        String lowestSellPrice,

        @JsonPropertyDescription("最高收购价")
        String highestBuyPrice,

        @JsonPropertyDescription("物品详情")
        WarframeMarketItemView item,

        @JsonPropertyDescription("套装部件")
        List<WarframeMarketItemView> setItems) {

    public WarframeMarketOrderResult {
        setItems = setItems == null ? List.of() : List.copyOf(setItems);
    }

    @JsonProperty
    @JsonPropertyDescription("订单")
    public List<WarframeMarketOrderView> orders() {
        return orderPage.records();
    }

    @JsonProperty
    @JsonPropertyDescription("当前页")
    public int page() {
        return orderPage.current();
    }

    @JsonProperty
    @JsonPropertyDescription("每页数量")
    public int pageSize() {
        return orderPage.size();
    }

    @JsonProperty
    @JsonPropertyDescription("订单总数")
    public long totalOrders() {
        return orderPage.total();
    }

    @JsonProperty
    @JsonPropertyDescription("总页数")
    public long totalPages() {
        return (totalOrders() + pageSize() - 1) / pageSize();
    }
    @JsonProperty
    @JsonPropertyDescription("还有下一页")
    public boolean hasNextPage() {
        return page() < totalPages();
    }

    @JsonProperty
    @JsonPropertyDescription("当前页卖单")
    public List<WarframeMarketOrderView> sellers() {
        return orders().stream().filter(order -> "sell".equals(order.orderType())).toList();
    }
    @JsonProperty
    @JsonPropertyDescription("当前页买单")
    public List<WarframeMarketOrderView> buyers() {
        return orders().stream().filter(order -> "buy".equals(order.orderType())).toList();
    }
    @JsonProperty
    @JsonPropertyDescription("当前页卖单数")
    public int sellerCount() {
        return sellers().size();
    }

    @JsonProperty
    @JsonPropertyDescription("当前页买单数")
    public int buyerCount() {
        return buyers().size();
    }
}
