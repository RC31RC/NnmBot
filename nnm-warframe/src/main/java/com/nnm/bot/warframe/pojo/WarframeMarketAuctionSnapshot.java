package com.nnm.bot.warframe.pojo;

import java.util.List;
import java.util.Map;

/** Warframe Market v1 拍卖搜索结果，不持久化到数据库。 */
public record WarframeMarketAuctionSnapshot(
        String type,
        String weaponUrlName,
        List<Map<String, Object>> auctions,
        List<String> positiveStats,
        List<String> negativeStats,
        String rawJson) {

    public WarframeMarketAuctionSnapshot {
        auctions = auctions == null ? List.of() : List.copyOf(auctions);
        positiveStats = positiveStats == null ? List.of() : List.copyOf(positiveStats);
        negativeStats = negativeStats == null ? List.of() : List.copyOf(negativeStats);
        rawJson = rawJson == null ? "" : rawJson;
    }
}
