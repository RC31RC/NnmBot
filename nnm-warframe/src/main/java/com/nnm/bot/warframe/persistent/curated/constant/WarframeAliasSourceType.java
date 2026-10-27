package com.nnm.bot.warframe.persistent.curated.constant;

import cn.hutool.core.util.StrUtil;
import java.util.Locale;

/** Warframe 物品别名的维护来源。 */
public enum WarframeAliasSourceType {
    OFFICIAL,
    COMMUNITY,
    MANUAL;

    /**
     * 将外部输入解析为明确的业务数据对象。
     *
     * @param value 待校验或规范化的文本。
     * @return 游戏资料别名来源类型对象。
     */
    public static WarframeAliasSourceType parse(String value) {
        if (StrUtil.isBlank(value)) {
            return MANUAL;
        }
        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
