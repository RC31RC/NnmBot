package com.nnm.bot.warframe.runtime.pojo.dto.market;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.common.response.PageResult;
import java.util.List;

/**
 * Service 返回的完整订单查询结果，包含详情和分页，供命令按用途选择展示字段。
 * WM handler 将其投影为 WarframeMarketOrderVO，输出渠道不再筛选业务字段。
 */
public record WarframeMarketOrderPageDTO(
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
        PageResult<WarframeMarketOrderEntryDTO> orderPage,

        @JsonPropertyDescription("最低售价")
        String lowestSellPrice,

        @JsonPropertyDescription("最高收购价")
        String highestBuyPrice,

        @JsonPropertyDescription("物品详情")
        WarframeMarketItemSummaryDTO item,

        @JsonPropertyDescription("套装部件")
        List<WarframeMarketItemSummaryDTO> setItems) {

    /**
     * 构造市场订单分页传输对象，并保存初始化参数。
     *
     * @param itemName 物品名称。
     * @param query 当前业务的查询条件。
     * @param marketSlug 市场地址标识。
     * @param platform 市场或官方接口使用的平台代码。
     * @param filter 筛选。
     * @param maxRankRequested 最大等级请求的。
     * @param maxRank 最大等级。
     * @param orderPage 订单分页。
     * @param lowestSellPrice 最低卖出价格。
     * @param highestBuyPrice 最高买入价格。
     * @param item 当前物品或集合成员。
     * @param setItems 集合物品集合。
     */
    public WarframeMarketOrderPageDTO {
        setItems = setItems == null ? List.of() : List.copyOf(setItems);
    }

    /**
     * 读取当前查询结果中的有效订单。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @JsonProperty
    @JsonPropertyDescription("订单")
    public List<WarframeMarketOrderEntryDTO> orders() {
        return orderPage.records();
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @return 已规范化的当前页码。
     */
    @JsonProperty
    @JsonPropertyDescription("当前页")
    public int page() {
        return orderPage.current();
    }

    /**
     * 读取当前订单页面使用的每页条目上限。
     * @return 当前允许的每页条目上限。
     */
    @JsonProperty
    @JsonPropertyDescription("每页数量")
    public int pageSize() {
        return orderPage.size();
    }

    /**
     * 计算符合当前条件的订单总数。
     *
     * @return 当前操作计算或读取的数值。
     */
    @JsonProperty
    @JsonPropertyDescription("订单总数")
    public long totalOrders() {
        return orderPage.total();
    }

    /**
     * 计算当前分页条件下的总页数。
     *
     * @return 当前操作计算或读取的数值。
     */
    @JsonProperty
    @JsonPropertyDescription("总页数")
    public long totalPages() {
        return (totalOrders() + pageSize() - 1) / pageSize();
    }
    /**
     * 判断查询结果后面是否仍有下一页。
     *
     * @return 满足查询结果后面是否仍有下一页时为真，否则为假。
     */
    @JsonProperty
    @JsonPropertyDescription("还有下一页")
    public boolean hasNextPage() {
        return page() < totalPages();
    }

    /**
     * 取得满足筛选条件的卖方订单。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @JsonProperty
    @JsonPropertyDescription("当前页卖单")
    public List<WarframeMarketOrderEntryDTO> sellers() {
        return orders().stream().filter(order -> "sell".equals(order.orderType())).toList();
    }
    /**
     * 取得满足筛选条件的买方订单。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @JsonProperty
    @JsonPropertyDescription("当前页买单")
    public List<WarframeMarketOrderEntryDTO> buyers() {
        return orders().stream().filter(order -> "buy".equals(order.orderType())).toList();
    }
    /**
     * 统计当前筛选结果中的卖方订单数量。
     * @return 当前操作计算或读取的数值。
     */
    @JsonProperty
    @JsonPropertyDescription("当前页卖单数")
    public int sellerCount() {
        return sellers().size();
    }

    /**
     * 统计当前筛选结果中的买方订单数量。
     * @return 当前操作计算或读取的数值。
     */
    @JsonProperty
    @JsonPropertyDescription("当前页买单数")
    public int buyerCount() {
        return buyers().size();
    }
}
