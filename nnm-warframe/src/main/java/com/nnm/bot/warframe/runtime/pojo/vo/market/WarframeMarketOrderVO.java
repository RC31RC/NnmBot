package com.nnm.bot.warframe.runtime.pojo.vo.market;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderEntryDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderPageDTO;
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
public record WarframeMarketOrderVO(
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
        List<OrderVO> orders,
        @JsonPropertyDescription("提示")
        String notice) {

    /**
     * 命令边界选择展示字段，不改变 Service 的过滤、排序和分页结果。
     *
     * @param result 当前正在组装的业务结果。
     * @return 市场买卖订单与价格汇总的展示结果。
     */
    public static WarframeMarketOrderVO from(WarframeMarketOrderPageDTO result) {
        String direction = switch (result.filter()) {
            case "BUY" -> "买家";
            case "SELL" -> "卖家";
            default -> "买家及卖家";
        };
        String filter = direction + (result.maxRankRequested() ? "，满级" : "");
        return new WarframeMarketOrderVO(result.itemName(), result.platform(), filter,
                result.page() + "/" + Math.max(1, result.totalPages())
                        + " 页，共 " + result.totalOrders() + " 单",
                result.lowestSellPrice(), result.highestBuyPrice(),
                result.orders().stream().map(OrderVO::from).toList(),
                CollUtil.isEmpty(result.orders()) ? (result.totalOrders() == 0
                        ? "没有符合条件的在线订单" : "此页没有订单，请查询前面的页码") : null);
    }

    /** 一单一行，只保留交易决策字段；不存在的等级、星星等不产生空占位。 */
    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record OrderVO(
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

        /**
         * 将输入对象转换为当前业务数据对象。
         *
         * @param order 成员在输入集合中的位置。
         * @return 单个市场订单的展示内容。
         */
        private static OrderVO from(WarframeMarketOrderEntryDTO order) {
            List<String> specs = new ArrayList<>();
            if (order.rank() != null) specs.add("等级 " + order.rank());
            if (StrUtil.isNotBlank(order.subtype())) specs.add(order.subtype());
            if (order.charges() != null) specs.add("剩余 " + order.charges() + " 次");
            if (order.amberStars() != null) specs.add("琥珀星 " + order.amberStars());
            if (order.cyanStars() != null) specs.add("青蓝星 " + order.cyanStars());
            if (order.perTrade() != null && order.perTrade() > 1) {
                specs.add("每次交易 " + order.perTrade() + " 个");
            }
            return new OrderVO("buy".equals(order.orderType()) ? "收购" : "出售",
                    order.userName(), order.platinum() + " 白金", order.quantity(),
                    order.statusText(), String.join("，", specs));
        }
    }
}
