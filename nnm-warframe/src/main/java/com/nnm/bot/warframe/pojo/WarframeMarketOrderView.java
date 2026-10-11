package com.nnm.bot.warframe.pojo;

import java.util.Locale;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/** 单条可展示订单，不将上游原始 JSON、内部用户字段混入命令输出。 */
public record WarframeMarketOrderView(
        @JsonPropertyDescription("订单编号")
        String id,
        @JsonPropertyDescription("订单方向")
        String orderType,
        @JsonPropertyDescription("玩家")
        String userName,
        @JsonPropertyDescription("信誉")
        String reputation,
        @JsonPropertyDescription("白金单价")
        int platinum,
        @JsonPropertyDescription("数量")
        int quantity,
        @JsonPropertyDescription("每次交易数量")
        Integer perTrade,
        @JsonPropertyDescription("物品变体")
        String subtype,
        @JsonPropertyDescription("等级")
        Integer rank,
        @JsonPropertyDescription("剩余次数")
        Integer charges,
        @JsonPropertyDescription("琥珀星数量")
        Integer amberStars,
        @JsonPropertyDescription("青蓝星数量")
        Integer cyanStars,
        @JsonPropertyDescription("在线状态代码")
        String status,
        @JsonPropertyDescription("在线状态")
        String statusText) {

    public static WarframeMarketOrderView from(WarframeMarketOrder order) {
        String statusText = switch (order.status().toLowerCase(Locale.ROOT)) {
            case "online" -> "在线";
            case "ingame" -> "游戏中";
            case "offline" -> "离线";
            default -> order.status();
        };
        return new WarframeMarketOrderView(order.id(), order.orderType(), order.userName(),
                order.reputation(), order.platinum(), order.quantity(), order.perTrade(),
                order.subtype(), order.rank(), order.charges(), order.amberStars(),
                order.cyanStars(), order.status(), statusText);
    }
}
