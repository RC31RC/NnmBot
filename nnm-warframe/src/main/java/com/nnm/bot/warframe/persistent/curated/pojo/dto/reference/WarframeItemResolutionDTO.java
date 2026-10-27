package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import java.util.List;

/**
 * Warframe 物品解析结果。
 *
 * @param item 唯一命中的物品；是否允许模糊命中由所调用的解析入口决定
 * @param candidates 同名或匹配分值接近时的候选物品，不可直接取首项作为确认
 */
public record WarframeItemResolutionDTO(
        WarframeItemDTO item,
        List<WarframeItemDTO> candidates
) {

    /**
     * 固定候选快照，防止调用方在后续匹配或异步渲染时修改列表。
     *
     * @param item 当前物品或集合成员。
     * @param candidates 候选集合。
     */
    public WarframeItemResolutionDTO {
        candidates = CollUtil.isEmpty(candidates) ? List.of() : List.copyOf(candidates);
    }

    /**
     * 没有命中，也没有可展示的候选。
     *
     * @return 游戏资料物品解析状态对象。
     */
    public static WarframeItemResolutionDTO notFound() {
        return new WarframeItemResolutionDTO(null, List.of());
    }

    /**
     * 当前解析策略下得到唯一结果；不单独代表 OCR 原文精确确认。
     *
     * @param item 当前物品或集合成员。
     * @return 游戏资料物品解析状态对象。
     */
    public static WarframeItemResolutionDTO resolved(WarframeItemDTO item) {
        return new WarframeItemResolutionDTO(item, List.of());
    }

    /**
     * 保留全部冲突候选，由命令提示用户明确身份。
     *
     * @param candidates 候选集合。
     * @return 游戏资料物品解析状态对象。
     */
    public static WarframeItemResolutionDTO ambiguous(List<WarframeItemDTO> candidates) {
        return new WarframeItemResolutionDTO(null, candidates);
    }

    /**
     * 当前策略是否产生唯一命中，与候选列表非空分开判断。
     *
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    public boolean found() {
        return ObjectUtil.isNotNull(this.item);
    }

    /**
     * 有候选但没有唯一命中，调用方不得静默选择一个来源或变体。
     *
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    public boolean ambiguous() {
        return ObjectUtil.isNull(this.item) && CollUtil.isNotEmpty(this.candidates);
    }
}
