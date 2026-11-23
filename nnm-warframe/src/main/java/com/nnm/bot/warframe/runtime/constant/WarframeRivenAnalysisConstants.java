package com.nnm.bot.warframe.runtime.constant;

/** 紫卡分析使用的领域状态。 */
public final class WarframeRivenAnalysisConstants {
    private WarframeRivenAnalysisConstants() {}

    /** 仅表示观测值与已确认参考区间的关系，不表示紫卡品质。 */
    public enum ReferenceRangeStatus {
        IN_RANGE, OUT_OF_RANGE, UNAVAILABLE
    }
}
