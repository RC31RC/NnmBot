package com.nnm.bot.warframe.pojo;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** 已完成武器和正负词条解析的拍卖查询，服务层负责分页。 */
public record WarframeMarketAuctionQuery(
        String type, String weaponName, String weaponSlug,
        List<String> positiveStats, List<String> negativeStats, int page,
        boolean requireNegative) {

    public WarframeMarketAuctionQuery(String type, String weaponName, String weaponSlug,
                                      List<String> positiveStats, List<String> negativeStats, int page) {
        this(type, weaponName, weaponSlug, positiveStats, negativeStats, page, false);
    }

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

    private static List<String> normalized(List<String> values) {
        return values == null ? List.of() : values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(value -> value.trim().toLowerCase(Locale.ROOT)).distinct().sorted().toList();
    }
}
