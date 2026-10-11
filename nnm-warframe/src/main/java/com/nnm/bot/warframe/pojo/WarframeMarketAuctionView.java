package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;

/** 拍卖展示记录，同时承载紫卡词条及玄骸/姐妹武器的元素、幻纹信息。 */
public record WarframeMarketAuctionView(
        @JsonPropertyDescription("拍卖编号")
        String id,
        @JsonPropertyDescription("玩家")
        String userName,
        @JsonPropertyDescription("在线状态代码")
        String status,
        @JsonPropertyDescription("在线状态")
        String statusText,
        @JsonPropertyDescription("起拍价")
        Integer startingPrice,
        @JsonPropertyDescription("一口价")
        Integer buyoutPrice,
        @JsonPropertyDescription("最高出价")
        Integer topBid,
        @JsonPropertyDescription("直接出售")
        boolean directSell,
        @JsonPropertyDescription("发布时间")
        String createdAt,
        @JsonPropertyDescription("拍卖物品")
        AuctionItem item) {

    /** 拍卖物品的业务属性，不同拍卖类型未提供的字段保持为空。 */
    public record AuctionItem(
            @JsonPropertyDescription("类型")
            String type,
            @JsonPropertyDescription("武器标识")
            String weaponUrlName,
            @JsonPropertyDescription("名称")
            String name,
            @JsonPropertyDescription("等级")
            Integer modRank,
            @JsonPropertyDescription("洗练次数")
            Integer reRolls,
            @JsonPropertyDescription("段位要求")
            Integer masteryLevel,
            @JsonPropertyDescription("极性")
            String polarity,
            @JsonPropertyDescription("元素")
            String element,
            @JsonPropertyDescription("元素加成")
            Integer damage,
            @JsonPropertyDescription("携带幻纹")
            Boolean havingEphemera,
            @JsonPropertyDescription("怪癖")
            String quirk,
            @JsonPropertyDescription("词条")
            List<Attribute> attributes) {
        public AuctionItem {
            attributes = attributes == null ? List.of() : List.copyOf(attributes);
        }
    }

    /** 紫卡属性标识和值，保留正负向标记供同一展示对象的不同输出渠道使用。 */
    public record Attribute(
            @JsonPropertyDescription("词条标识")
            String urlName,
            @JsonPropertyDescription("数值")
            double value,
            @JsonPropertyDescription("正面词条")
            boolean positive) {}
}
