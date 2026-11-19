package com.nnm.bot.warframe.runtime.constant;

/** 数据源 25 号降级匹配的边界，只用于展示，不写入官方静态身份。 */
public final class WarframeDescentMatchPolicy {
    public static final double MIN_SCORE = 0.82;
    public static final double MIN_WINNING_MARGIN = 0.1;
    public static final double EXACT_NORMALIZED_SCORE = 0.98;
    public static final double UNIQUE_SUFFIX_SCORE = 0.88;
    public static final double MIN_SUFFIX_RATIO = 0.55;
    public static final int MIN_SUFFIX_LENGTH = 6;
    public static final int MIN_TOKEN_LENGTH = 4;
    public static final int SCORE_SCALE = 1000;

    private WarframeDescentMatchPolicy() {}
}
