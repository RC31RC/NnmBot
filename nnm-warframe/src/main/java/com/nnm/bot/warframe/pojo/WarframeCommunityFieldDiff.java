package com.nnm.bot.warframe.pojo;

/** 社区数据集的字段级差异。 */
public record WarframeCommunityFieldDiff(
        String dataset,
        String recordKey,
        String identityKey,
        String field,
        Object currentValue,
        Object upstreamValue,
        Object manualValue,
        boolean conflict,
        String defaultDecision
) {
    public WarframeCommunityFieldDiff(
            String dataset,
            String recordKey,
            String field,
            Object currentValue,
            Object upstreamValue,
            Object manualValue,
            boolean conflict,
            String defaultDecision) {
        this(dataset, recordKey, recordKey, field, currentValue, upstreamValue,
                manualValue, conflict, defaultDecision);
    }
}
