package com.nnm.bot.warframe.pojo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;

/**
 * 一次 Warframe Market 订单查询的完整快照。
 *
 * <p>保留原始 JSON 是为了在上游字段变化时可以排查解析问题，并让后续
 * 统计类命令按需扩展，而不必重新请求上游。</p>
 */
public record WarframeMarketOrderSnapshot(
        String itemName,
        Integer maxRank,
        String platform,
        List<WarframeMarketOrder> orders,
        String rawJson,
        WarframeMarketItem item,
        String apiVersion,
        String error) {

    public WarframeMarketOrderSnapshot(
            String itemName,
            Integer maxRank,
            String platform,
            List<WarframeMarketOrder> orders,
            String rawJson) {
        this(itemName, maxRank, platform, orders, rawJson,
                new WarframeMarketItem(itemName, maxRank), "", "");
    }

    public WarframeMarketOrderSnapshot {
        itemName = StrUtil.emptyIfNull(itemName).trim();
        platform = StrUtil.emptyIfNull(platform).trim();
        orders = CollUtil.isEmpty(orders) ? List.of() : List.copyOf(orders);
        rawJson = StrUtil.emptyIfNull(rawJson);
        item = item == null ? new WarframeMarketItem(itemName, maxRank) : item;
        apiVersion = StrUtil.emptyIfNull(apiVersion).trim();
        error = StrUtil.emptyIfNull(error).trim();
    }
}
