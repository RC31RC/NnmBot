package com.nnm.bot.warframe.runtime.pojo.dto.market;

import java.util.List;

/**
 * Warframe Market 统计结果。
 *
 * <p>统计接口的日粒度字段可能随上游版本增加，因此保留归一化后的记录
 * 列表和原始响应，避免因非核心字段变化导致命令不可用。</p>
 */
public record WarframeMarketStatisticsDTO(
        String itemUrlName,
        List<MarketStatisticDTO> entries,
        String rawJson) {

    /**
     * 构造市场统计传输对象，并保存初始化参数。
     *
     * @param itemUrlName 物品地址名称。
     * @param entries 条目集合。
     * @param rawJson 用于审核或排障的原始响应文本。
     */
    public WarframeMarketStatisticsDTO {
        entries = entries == null ? List.of() : List.copyOf(entries);
        rawJson = rawJson == null ? "" : rawJson;
    }
}
