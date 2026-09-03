package com.nnm.bot.warframe.dto;

/** 社区数据集的字段级差异。 */
public record WarframeCommunityFieldDiff(
        String dataset,
        String recordKey,
        String field,
        Object currentValue,
        Object upstreamValue,
        Object manualValue,
        boolean conflict,
        String defaultDecision
) {
}
