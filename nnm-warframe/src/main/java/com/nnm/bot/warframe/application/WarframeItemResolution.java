package com.nnm.bot.warframe.application;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.warframe.domain.entity.WarframeItem;

import java.util.List;

/**
 * Warframe 物品解析结果。
 *
 * @param item 唯一命中的物品
 * @param candidates 同名歧义时的候选物品
 */
public record WarframeItemResolution(
        WarframeItem item,
        List<WarframeItem> candidates
) {

    public WarframeItemResolution {
        candidates = CollUtil.isEmpty(candidates) ? List.of() : List.copyOf(candidates);
    }

    public static WarframeItemResolution notFound() {
        return new WarframeItemResolution(null, List.of());
    }

    public static WarframeItemResolution resolved(WarframeItem item) {
        return new WarframeItemResolution(item, List.of());
    }

    public static WarframeItemResolution ambiguous(List<WarframeItem> candidates) {
        return new WarframeItemResolution(null, candidates);
    }

    public boolean found() {
        return ObjectUtil.isNotNull(this.item);
    }

    public boolean ambiguous() {
        return ObjectUtil.isNull(this.item) && CollUtil.isNotEmpty(this.candidates);
    }
}
