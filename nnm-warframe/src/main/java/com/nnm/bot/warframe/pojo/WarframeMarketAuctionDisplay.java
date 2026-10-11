package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** 拍卖命令只展示报价、玩家和武器规格，不重复返回目录标识及完整接口对象。 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WarframeMarketAuctionDisplay(
        @JsonPropertyDescription("武器")
        String weapon,
        @JsonPropertyDescription("分页")
        String pagination,
        @JsonPropertyDescription("筛选")
        String filter,
        @JsonPropertyDescription("拍卖")
        List<Auction> auctions,
        @JsonPropertyDescription("提示")
        String notice) {

    public static WarframeMarketAuctionDisplay from(
            WarframeMarketAuctionResult result, Function<String, String> attributeName) {
        var page = result.auctions();
        List<String> filters = new ArrayList<>();
        result.positiveStats().forEach(stat -> filters.add("正面 " + attributeName.apply(stat)));
        result.negativeStats().forEach(stat -> filters.add("负面 " + attributeName.apply(stat)));
        if (result.requireNegative()) filters.add("必须有负面词条");
        return new WarframeMarketAuctionDisplay(result.weapon(),
                page.current() + "/" + Math.max(1, (int) Math.ceil((double) page.total()
                        / Math.max(1, page.size()))) + " 页，共 " + page.total() + " 单",
                String.join("，", filters),
                page.records().stream().map(auction -> Auction.from(auction, attributeName)).toList(),
                page.records().isEmpty() ? "当前页没有符合条件的拍卖" : null);
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Auction(
            @JsonPropertyDescription("玩家")
            String userName,
            @JsonPropertyDescription("状态")
            String status,
            @JsonPropertyDescription("起拍")
            String startingPrice,
            @JsonPropertyDescription("一口价")
            String buyoutPrice,
            @JsonPropertyDescription("最高出价")
            String topBid,
            @JsonPropertyDescription("名称")
            String name,
            @JsonPropertyDescription("规格")
            String specifications,
            @JsonPropertyDescription("词条")
            List<String> attributes) {

        private static Auction from(WarframeMarketAuctionView auction,
                                    Function<String, String> attributeName) {
            var item = auction.item();
            List<String> specs = new ArrayList<>();
            List<String> stats = new ArrayList<>();
            if (item != null) {
                if (item.modRank() != null) specs.add("等级 " + item.modRank());
                if (item.reRolls() != null) specs.add("洗练 " + item.reRolls());
                if (item.masteryLevel() != null) specs.add("段位 " + item.masteryLevel());
                if (item.polarity() != null && !item.polarity().isBlank()) specs.add("极性 " + item.polarity());
                if (item.element() != null && !item.element().isBlank()) specs.add("元素 " + item.element());
                if (item.damage() != null) specs.add("加成 " + item.damage() + "%");
                if (item.havingEphemera() != null) specs.add(item.havingEphemera() ? "有幻纹" : "无幻纹");
                if (item.quirk() != null && !item.quirk().isBlank()) specs.add("怪癖 " + item.quirk());
                item.attributes().forEach(attribute -> stats.add(
                        (attribute.positive() ? "正面 " : "负面 ") + attributeName.apply(attribute.urlName())
                                + " " + attribute.value()));
            }
            return new Auction(auction.userName(), auction.statusText(), price(auction.startingPrice()),
                    price(auction.buyoutPrice()), price(auction.topBid()), item == null ? null : item.name(),
                    String.join("，", specs), stats);
        }

        private static String price(Integer price) {
            return price == null ? null : price + " 白金";
        }
    }
}
