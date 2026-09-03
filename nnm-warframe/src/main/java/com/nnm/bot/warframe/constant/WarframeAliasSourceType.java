package com.nnm.bot.warframe.constant;

import cn.hutool.core.util.StrUtil;

import java.util.Locale;

/** Warframe 物品别名的维护来源。 */
public enum WarframeAliasSourceType {
    OFFICIAL,
    COMMUNITY,
    MANUAL;

    public static WarframeAliasSourceType parse(String value) {
        if (StrUtil.isBlank(value)) {
            return MANUAL;
        }
        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
