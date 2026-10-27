package com.nnm.bot.warframe.runtime.pojo.dto.market;

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
public record WarframeMarketItemDTO(
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
        Map<String, WarframeMarketItemI18nDTO> i18n,
        Map<String, Object> rawFields) {

    /**
     * 构造市场物品传输对象，并保存初始化参数。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @param slug 市场物品的地址标识。
     * @param gameRef 游戏版本引用。
     * @param tags 标签集合。
     * @param setRoot 集合主记录。
     * @param setParts 集合部件集合。
     * @param quantityInSet 数量包含集合。
     * @param rarity 稀有度。
     * @param bulkTradable 批量可交易。
     * @param subtypes 子类型集合。
     * @param maxRank 最大等级。
     * @param maxCharges 最大充能。
     * @param maxAmberStars 最大琥珀星槽。
     * @param maxCyanStars 最大青蓝星槽。
     * @param baseEndo 基础内融核心。
     * @param endoMultiplier 内融核心倍率。
     * @param ducats 杜卡德金币。
     * @param vosfor 赋能精华。
     * @param reqMasteryRank 要求段位等级。
     * @param vaulted 已入库。
     * @param tradingTax 交易税额。
     * @param tradable 可交易。
     * @param i18n 下标负面词条。
     * @param rawFields 原始字段。
     */
    public WarframeMarketItemDTO {
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

    /**
     * 构造市场物品传输对象，并保存初始化参数。
     *
     * @param name 当前业务对象的名称。
     * @param maxRank 最大等级。
     */
    public WarframeMarketItemDTO(String name, Integer maxRank) {
        this("", "", "", List.of(), null, List.of(), null, "", null, List.of(),
                maxRank, null, null, null, null, null, null, null, null, null, null,
                null, StrUtil.isBlank(name) ? Map.of() : Map.of("en",
                        new WarframeMarketItemI18nDTO(name, "", "", "", "", "")),
                Map.of());
    }

    /**
     * 按请求语言优先选择物品名称，缺省时按实际语言资料回退。
     * @param language 本地化语言代码。
     * @return 按语言优先顺序取得的物品名称。
     */
    public String localizedName(String language) {
        WarframeMarketItemI18nDTO localized = i18n.get(language);
        if (localized == null) {
            localized = i18n.get("en");
        }
        return localized == null ? "" : localized.name();
    }

    public record WarframeMarketItemI18nDTO(
            String name,
            String description,
            String wikiLink,
            String icon,
            String thumb,
            String subIcon) {

        /**
         * 构造市场物品负面词条传输对象，并保存初始化参数。
         *
         * @param name 当前业务对象的名称。
         * @param description 说明。
         * @param wikiLink 百科链接。
         * @param icon 图标。
         * @param thumb 缩略图。
         * @param subIcon 附属图标。
         */
        public WarframeMarketItemI18nDTO {
            name = trim(name);
            description = trim(description);
            wikiLink = trim(wikiLink);
            icon = trim(icon);
            thumb = trim(thumb);
            subIcon = trim(subIcon);
        }
    }

    /**
     * 复制当前集合，防止调用方修改共享数据。
     *
     * @param values 待处理的值集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static <T> List<T> immutable(List<T> values) {
        return CollUtil.isEmpty(values) ? List.of() : List.copyOf(values);
    }

    /**
     * 裁剪首尾空白并统一缺省文本。
     *
     * @param value 待校验或规范化的文本。
     * @return 去除首尾空白的文本；输入缺省时返回空文本。
     */
    private static String trim(String value) {
        return StrUtil.emptyIfNull(value).trim();
    }
}
