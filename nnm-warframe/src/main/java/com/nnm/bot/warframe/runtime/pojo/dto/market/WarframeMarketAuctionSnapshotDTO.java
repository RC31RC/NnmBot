package com.nnm.bot.warframe.runtime.pojo.dto.market;

import java.util.List;

/** Warframe Market v1 拍卖搜索结果，不持久化到数据库。 */
public record WarframeMarketAuctionSnapshotDTO(
        String type,
        String weaponUrlName,
        List<WarframeMarketAuctionEntryDTO> auctions,
        List<String> positiveStats,
        List<String> negativeStats,
        String rawJson) {

    /**
     * 构造市场拍卖快照传输对象，并保存初始化参数。
     *
     * @param type 目标业务类型。
     * @param weaponUrlName 武器地址名称。
     * @param auctions 拍卖集合。
     * @param positiveStats 正面词条统计集合。
     * @param negativeStats 负面词条统计集合。
     * @param rawJson 用于审核或排障的原始响应文本。
     */
    public WarframeMarketAuctionSnapshotDTO {
        auctions = auctions == null ? List.of() : List.copyOf(auctions);
        positiveStats = positiveStats == null ? List.of() : List.copyOf(positiveStats);
        negativeStats = negativeStats == null ? List.of() : List.copyOf(negativeStats);
        rawJson = rawJson == null ? "" : rawJson;
    }
}
