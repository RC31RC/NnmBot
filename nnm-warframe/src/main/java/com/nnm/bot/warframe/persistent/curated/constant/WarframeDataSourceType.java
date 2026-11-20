package com.nnm.bot.warframe.persistent.curated.constant;

import cn.hutool.core.text.CharSequenceUtil;

import java.util.Locale;

/** Warframe 本地数据的维护来源。 */
public enum WarframeDataSourceType {
    OFFICIAL_DATASET, COMMUNITY_SNAPSHOT, MANUAL;

    /**
     * 将外部输入解析为明确的业务数据对象。
     *
     * @param value 待校验或规范化的文本。
     * @return 游戏资料数据来源类型对象。
     */
    public static WarframeDataSourceType parse(String value) {
        if (CharSequenceUtil.isBlank(value)) {
            return MANUAL;
        }
        return valueOf(CharSequenceUtil.trim(value).toUpperCase(Locale.ROOT));
    }
}
