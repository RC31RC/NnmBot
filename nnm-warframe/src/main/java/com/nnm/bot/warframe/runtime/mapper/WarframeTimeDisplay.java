package com.nnm.bot.warframe.runtime.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** 为 Warframe 动态数据和命令生成北京时间展示文本。 */
public final class WarframeTimeDisplay {
    private static final DateTimeFormatter TIMESTAMP =
        DateTimeFormatter.ofPattern("MM-dd HH:mm", Locale.ROOT).withZone(ZoneId.of("Asia/Shanghai"));

    private WarframeTimeDisplay() {}

    /**
     * 将绝对时刻转换为月日和 24 小时制的时间。
     *
     * @param instant 绝对时刻，允许为空。
     * @return 北京时间展示文本，缺少时刻时返回空字符串。
     */
    public static String format(Instant instant) {
        return instant == null ? "" : TIMESTAMP.format(instant);
    }
}
