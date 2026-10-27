package com.nnm.bot.warframe.runtime.pojo.dto.market;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import java.util.List;

/**
 * 一次 Warframe Market 订单查询的完整快照。
 *
 * <p>保留原始 JSON 是为了在上游字段变化时可以排查解析问题，并让后续
 * 统计类命令按需扩展，而不必重新请求上游。</p>
 */
public record WarframeMarketOrderSnapshotDTO(
        String itemName,
        Integer maxRank,
        String platform,
        List<WarframeMarketOrderDTO> orders,
        String rawJson,
        WarframeMarketItemDTO item,
        String apiVersion,
        String error) {

    /**
     * 构造市场订单快照传输对象，并保存初始化参数。
     *
     * @param itemName 物品名称。
     * @param maxRank 最大等级。
     * @param platform 市场或官方接口使用的平台代码。
     * @param orders 订单集合。
     * @param rawJson 用于审核或排障的原始响应文本。
     */
    public WarframeMarketOrderSnapshotDTO(
            String itemName,
            Integer maxRank,
            String platform,
            List<WarframeMarketOrderDTO> orders,
            String rawJson) {
        this(itemName, maxRank, platform, orders, rawJson,
                new WarframeMarketItemDTO(itemName, maxRank), "", "");
    }

    /**
     * 构造市场订单快照传输对象，并保存初始化参数。
     *
     * @param itemName 物品名称。
     * @param maxRank 最大等级。
     * @param platform 市场或官方接口使用的平台代码。
     * @param orders 订单集合。
     * @param rawJson 用于审核或排障的原始响应文本。
     * @param item 当前物品或集合成员。
     * @param apiVersion 接口版本。
     * @param error 错误。
     */
    public WarframeMarketOrderSnapshotDTO {
        itemName = StrUtil.emptyIfNull(itemName).trim();
        platform = StrUtil.emptyIfNull(platform).trim();
        orders = CollUtil.isEmpty(orders) ? List.of() : List.copyOf(orders);
        rawJson = StrUtil.emptyIfNull(rawJson);
        item = item == null ? new WarframeMarketItemDTO(itemName, maxRank) : item;
        apiVersion = StrUtil.emptyIfNull(apiVersion).trim();
        error = StrUtil.emptyIfNull(error).trim();
    }
}
