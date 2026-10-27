package com.nnm.bot.warframe.runtime.pojo.dto.market;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemDTO.WarframeMarketItemI18nDTO;

/**
 * 物品详情的展示投影：选择简体中文、英文兜底，不将原始 JSON 和多语言字典传入输出渠道。
 */
public record WarframeMarketItemSummaryDTO(
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

    /**
     * 将输入对象转换为当前业务数据对象。
     *
     * @param item 当前物品或集合成员。
     * @return 游戏资料市场物品摘要对象。
     */
    public static WarframeMarketItemSummaryDTO from(WarframeMarketItemDTO item) {
        WarframeMarketItemI18nDTO zh = item.i18n().get("zh-hans");
        WarframeMarketItemI18nDTO en = item.i18n().get("en");
        return new WarframeMarketItemSummaryDTO(
                choose(zh == null ? "" : zh.name(), en == null ? "" : en.name()),
                item.slug(),
                choose(zh == null ? "" : zh.description(), en == null ? "" : en.description()),
                choose(zh == null ? "" : zh.wikiLink(), en == null ? "" : en.wikiLink()),
                choose(zh == null ? "" : zh.icon(), en == null ? "" : en.icon()),
                item.quantityInSet(), item.ducats(), item.tradingTax(),
                item.maxRank(), item.reqMasteryRank(), item.vaulted());
    }

    /**
     * 按既定优先级选择可用候选值。
     *
     * @param localized 本地化。
     * @param fallback 缺省。
     * @return 按既定优先级选择可用候选值对应的文本。
     */
    private static String choose(String localized, String fallback) {
        return StrUtil.isBlank(localized) ? fallback : localized;
    }
}
