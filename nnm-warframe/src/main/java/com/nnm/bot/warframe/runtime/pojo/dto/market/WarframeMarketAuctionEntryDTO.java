package com.nnm.bot.warframe.runtime.pojo.dto.market;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;

/** 拍卖展示记录，同时承载紫卡词条及玄骸/姐妹武器的元素、幻纹信息。 */
public record WarframeMarketAuctionEntryDTO(
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
        AuctionItemDTO item) {

    /** 拍卖物品的业务属性，不同拍卖类型未提供的字段保持为空。 */
    public record AuctionItemDTO(
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
            List<AttributeDTO> attributes) {
        /**
         * 构造拍卖物品传输对象，并保存初始化参数。
         *
         * @param type 目标业务类型。
         * @param weaponUrlName 武器地址名称。
         * @param name 当前业务对象的名称。
         * @param modRank 振幅晶体等级。
         * @param reRolls 重新抽取。
         * @param masteryLevel 段位等级。
         * @param polarity 极性。
         * @param element 元素。
         * @param damage 伤害。
         * @param havingEphemera 携带幻纹。
         * @param quirk 性格。
         * @param attributes 属性集合。
         */
        public AuctionItemDTO {
            attributes = attributes == null ? List.of() : List.copyOf(attributes);
        }
    }

    /** 紫卡属性标识和值，保留正负向标记供同一展示对象的不同输出渠道使用。 */
    public record AttributeDTO(
            @JsonPropertyDescription("词条标识")
            String urlName,
            @JsonPropertyDescription("数值")
            double value,
            @JsonPropertyDescription("正面词条")
            boolean positive) {}
}
