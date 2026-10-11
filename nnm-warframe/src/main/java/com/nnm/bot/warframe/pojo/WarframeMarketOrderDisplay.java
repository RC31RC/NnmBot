package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;

import java.util.ArrayList;
import java.util.List;

/**
 * WM 命令的精简展示对象，与 Service 的完整查询结果分离。
 * 图片和文字只展示该对象，不包含物品百科、套装部件或重复的买卖订单列表。
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WarframeMarketOrderDisplay(
        @JsonPropertyDescription("物品")
        String itemName,
        @JsonPropertyDescription("平台")
        String platform,
        @JsonPropertyDescription("条件")
        String filter,
        @JsonPropertyDescription("分页")
        String pagination,
        @JsonPropertyDescription("最低售价")
        String lowestSellPrice,
        @JsonPropertyDescription("最高收购价")
        String highestBuyPrice,
        @JsonPropertyDescription("订单")
        List<Order> orders,
        @JsonPropertyDescription("提示")
        String notice) {

    /** 命令边界选择展示字段，不改变 Service 的过滤、排序和分页结果。 */
    public static WarframeMarketOrderDisplay from(WarframeMarketOrderResult result) {
        String direction = switch (result.filter()) {
            case "BUY" -> "买家";
            case "SELL" -> "卖家";
            default -> "买家及卖家";
        };
        String filter = direction + (result.maxRankRequested() ? "，满级" : "");
        return new WarframeMarketOrderDisplay(result.itemName(), result.platform(), filter,
                result.page() + "/" + Math.max(1, result.totalPages())
                        + " 页，共 " + result.totalOrders() + " 单",
                result.lowestSellPrice(), result.highestBuyPrice(),
                result.orders().stream().map(Order::from).toList(),
                result.orders().isEmpty() ? (result.totalOrders() == 0
                        ? "没有符合条件的在线订单" : "此页没有订单，请查询前面的页码") : null);
    }

    /** 一单一行，只保留交易决策字段；不存在的等级、星星等不产生空占位。 */
    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Order(
            @JsonPropertyDescription("方向")
            String direction,
            @JsonPropertyDescription("玩家")
            String userName,
            @JsonPropertyDescription("单价")
            String price,
            @JsonPropertyDescription("数量")
            int quantity,
            @JsonPropertyDescription("状态")
            String status,
            @JsonPropertyDescription("规格")
            String specifications) {

        private static Order from(WarframeMarketOrderView order) {
            List<String> specs = new ArrayList<>();
            if (order.rank() != null) specs.add("等级 " + order.rank());
            if (order.subtype() != null && !order.subtype().isBlank()) specs.add(order.subtype());
            if (order.charges() != null) specs.add("剩余 " + order.charges() + " 次");
            if (order.amberStars() != null) specs.add("琥珀星 " + order.amberStars());
            if (order.cyanStars() != null) specs.add("青蓝星 " + order.cyanStars());
            if (order.perTrade() != null && order.perTrade() > 1) {
                specs.add("每次交易 " + order.perTrade() + " 个");
            }
            return new Order("buy".equals(order.orderType()) ? "收购" : "出售",
                    order.userName(), order.platinum() + " 白金", order.quantity(),
                    order.statusText(), String.join("，", specs));
        }
    }
}
