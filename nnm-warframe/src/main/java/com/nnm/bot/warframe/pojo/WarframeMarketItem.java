package com.nnm.bot.warframe.pojo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Warframe Market 物品目录的完整结构化投影。
 *
 * <p>订单接口通常只返回物品标识或简化物品对象，完整的目录字段由目录
 * 接口补齐。所有字段都保持可选，避免某类物品缺少字段时丢弃整条数据。</p>
 */
public record WarframeMarketItem(
        String id,
        String slug,
        String gameRef,
        List<String> tags,
        Boolean setRoot,
        List<String> setParts,
        Integer quantityInSet,
        String rarity,
        Boolean bulkTradable,
        List<String> subtypes,
        Integer maxRank,
        Integer maxCharges,
        Integer maxAmberStars,
        Integer maxCyanStars,
        Integer baseEndo,
        Double endoMultiplier,
        Integer ducats,
        Integer vosfor,
        Integer reqMasteryRank,
        Boolean vaulted,
        Integer tradingTax,
        Boolean tradable,
        Map<String, WarframeMarketItemI18n> i18n,
        Map<String, Object> rawFields) {

    public WarframeMarketItem {
        id = trim(id);
        slug = trim(slug);
        gameRef = trim(gameRef);
        tags = immutable(tags);
        setParts = immutable(setParts);
        rarity = trim(rarity);
        subtypes = immutable(subtypes);
        i18n = CollUtil.isEmpty(i18n) ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(i18n));
        rawFields = CollUtil.isEmpty(rawFields) ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(rawFields));
    }

    public WarframeMarketItem(String name, Integer maxRank) {
        this("", "", "", List.of(), null, List.of(), null, "", null, List.of(),
                maxRank, null, null, null, null, null, null, null, null, null, null,
                null, StrUtil.isBlank(name) ? Map.of() : Map.of("en",
                        new WarframeMarketItemI18n(name, "", "", "", "", "")),
                Map.of());
    }

    public String localizedName(String language) {
        WarframeMarketItemI18n localized = i18n.get(language);
        if (localized == null) {
            localized = i18n.get("en");
        }
        return localized == null ? "" : localized.name();
    }

    public record WarframeMarketItemI18n(
            String name,
            String description,
            String wikiLink,
            String icon,
            String thumb,
            String subIcon) {

        public WarframeMarketItemI18n {
            name = trim(name);
            description = trim(description);
            wikiLink = trim(wikiLink);
            icon = trim(icon);
            thumb = trim(thumb);
            subIcon = trim(subIcon);
        }
    }

    private static <T> List<T> immutable(List<T> values) {
        return CollUtil.isEmpty(values) ? List.of() : List.copyOf(values);
    }

    private static String trim(String value) {
        return StrUtil.emptyIfNull(value).trim();
    }
}
