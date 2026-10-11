package com.nnm.bot.warframe.pojo;

import java.util.List;
import java.util.Map;

/**
 * Warframe Market 统计结果。
 *
 * <p>统计接口的日粒度字段可能随上游版本增加，因此保留归一化后的记录
 * 列表和原始响应，避免因非核心字段变化导致命令不可用。</p>
 */
public record WarframeMarketStatistics(
        String itemUrlName,
        List<Map<String, Object>> entries,
        String rawJson) {

    public WarframeMarketStatistics {
        entries = entries == null ? List.of() : List.copyOf(entries);
        rawJson = rawJson == null ? "" : rawJson;
    }
}
