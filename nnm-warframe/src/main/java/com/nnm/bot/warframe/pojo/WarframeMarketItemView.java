package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * 物品详情的展示投影：选择简体中文、英文兜底，不将原始 JSON 和多语言字典传入输出渠道。
 */
public record WarframeMarketItemView(
        @JsonPropertyDescription("名称")
        String name,
        @JsonPropertyDescription("市场标识")
        String slug,
        @JsonPropertyDescription("说明")
        String description,
        @JsonPropertyDescription("Wiki 链接")
        String wikiLink,
        @JsonPropertyDescription("图标")
        String icon,
        @JsonPropertyDescription("套装所需数量")
        Integer quantityInSet,
        @JsonPropertyDescription("杜卡德金币")
        Integer ducats,
        @JsonPropertyDescription("交易税")
        Integer tradingTax,
        @JsonPropertyDescription("最高等级")
        Integer maxRank,
        @JsonPropertyDescription("段位要求")
        Integer masteryRank,
        @JsonPropertyDescription("已入库")
        Boolean vaulted) {

    public static WarframeMarketItemView from(WarframeMarketItem item) {
        var zh = item.i18n().get("zh-hans");
        var en = item.i18n().get("en");
        return new WarframeMarketItemView(
                choose(zh == null ? "" : zh.name(), en == null ? "" : en.name()),
                item.slug(),
                choose(zh == null ? "" : zh.description(), en == null ? "" : en.description()),
                choose(zh == null ? "" : zh.wikiLink(), en == null ? "" : en.wikiLink()),
                choose(zh == null ? "" : zh.icon(), en == null ? "" : en.icon()),
                item.quantityInSet(), item.ducats(), item.tradingTax(),
                item.maxRank(), item.reqMasteryRank(), item.vaulted());
    }

    private static String choose(String localized, String fallback) {
        return localized.isBlank() ? fallback : localized;
    }
}
