package com.nnm.bot.warframe.runtime.constant;

import java.util.Locale;

/** 世界状态 Conquests 中科研任务的官方类型。 */
public enum WarframeConquestType {
    DEEP("CT_LAB", "深层科研"), HEX("CT_HEX", "时光科研");

    private final String code;
    private final String label;

    WarframeConquestType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 判断原始上游科研类型是否属于本类型。
     *
     * @param candidate 上游类型代码。
     * @return 匹配本类型时为真。
     */
    public boolean matches(String candidate) {
        return candidate != null && code.equals(candidate.strip().toUpperCase(Locale.ROOT));
    }

    /**
     * 提供可读名称，未知类型仍保留上游原值。
     *
     * @param code 上游类型代码。
     * @return 展示名称或未识别的原始代码。
     */
    public static String displayName(String code) {
        for (WarframeConquestType type : values()) {
            if (type.matches(code)) {
                return type.label;
            }
        }
        return code;
    }
}
