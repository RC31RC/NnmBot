package com.nnm.bot.warframe.runtime.pojo.query.market;

import cn.hutool.core.util.StrUtil;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** 已完成武器和正负词条解析的拍卖查询，服务层负责分页。 */
public record WarframeMarketAuctionQuery(
        String type, String weaponName, String weaponSlug,
        List<String> positiveStats, List<String> negativeStats, int page,
        boolean requireNegative) {

    /**
     * 构造市场拍卖查询，并保存初始化参数。
     *
     * @param type 目标业务类型。
     * @param weaponName 武器名称。
     * @param weaponSlug 武器地址标识。
     * @param positiveStats 正面词条统计集合。
     * @param negativeStats 负面词条统计集合。
     * @param page 从一开始的页码。
     */
    public WarframeMarketAuctionQuery(String type, String weaponName, String weaponSlug,
                                      List<String> positiveStats, List<String> negativeStats, int page) {
        this(type, weaponName, weaponSlug, positiveStats, negativeStats, page, false);
    }

    /**
     * 构造市场拍卖查询，并保存初始化参数。
     *
     * @param type 目标业务类型。
     * @param weaponName 武器名称。
     * @param weaponSlug 武器地址标识。
     * @param positiveStats 正面词条统计集合。
     * @param negativeStats 负面词条统计集合。
     * @param page 从一开始的页码。
     * @param requireNegative 取得负面。
     */
    public WarframeMarketAuctionQuery {
        type = Objects.requireNonNullElse(type, "riven").trim().toLowerCase(Locale.ROOT);
        if (!List.of("riven", "lich", "sister").contains(type)) {
            throw new IllegalArgumentException("Unsupported auction type: " + type);
        }
        weaponName = Objects.requireNonNullElse(weaponName, "").trim();
        weaponSlug = Objects.requireNonNullElse(weaponSlug, "").trim().toLowerCase(Locale.ROOT);
        positiveStats = normalized(positiveStats);
        negativeStats = normalized(negativeStats);
        page = Math.max(1, page);
    }

    /**
     * 规范化拍卖属性筛选值，去除空白和重复项。
     * @param values 待处理的值集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> normalized(List<String> values) {
        return values == null ? List.of() : values.stream()
                .filter(value -> StrUtil.isNotBlank(value))
                .map(value -> value.trim().toLowerCase(Locale.ROOT)).distinct().sorted().toList();
    }
}
