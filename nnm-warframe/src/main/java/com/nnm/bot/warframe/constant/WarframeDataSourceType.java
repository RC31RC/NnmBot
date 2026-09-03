package com.nnm.bot.warframe.constant;

import cn.hutool.core.util.StrUtil;

import java.util.Locale;

/** Warframe 本地数据的维护来源。 */
public enum WarframeDataSourceType {
    OFFICIAL_EXPORT,
    COMMUNITY_SNAPSHOT,
    MANUAL;

    public static WarframeDataSourceType parse(String value) {
        if (StrUtil.isBlank(value)) {
            return MANUAL;
        }
        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
