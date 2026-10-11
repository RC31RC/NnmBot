package com.nnm.bot.warframe.pojo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;

/**
 * Warframe Market 套装接口的结构化快照。
 *
 * <p>套装接口会同时返回根物品和所有部件；保留完整响应便于后续命令使用
 * 尚未建模的上游字段。</p>
 */
public record WarframeMarketItemSet(
        String id,
        List<WarframeMarketItem> items,
        String apiVersion,
        String error,
        String rawJson) {

    public WarframeMarketItemSet {
        id = StrUtil.emptyIfNull(id).trim();
        items = CollUtil.isEmpty(items) ? List.of() : List.copyOf(items);
        apiVersion = StrUtil.emptyIfNull(apiVersion).trim();
        error = StrUtil.emptyIfNull(error).trim();
        rawJson = StrUtil.emptyIfNull(rawJson);
    }
}
