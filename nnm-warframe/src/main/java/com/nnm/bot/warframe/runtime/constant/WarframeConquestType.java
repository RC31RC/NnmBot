package com.nnm.bot.warframe.runtime.constant;

import cn.hutool.core.util.StrUtil;

/** 世界状态 Conquests 中科研任务的官方类型。 */
public enum WarframeConquestType {
    DEEP("CT_LAB", "LabConquest"), HEX("CT_HEX", "HexConquest");

    private final String code;
    private final String variantGroup;

    WarframeConquestType(String code, String variantGroup) {
        this.code = code;
        this.variantGroup = variantGroup;
    }

    /**
     * 判断原始上游科研类型是否属于本类型。
     *
     * @param candidate 上游类型代码。
     * @return 匹配本类型时为真。
     */
    public boolean matches(String candidate) {
        return StrUtil.equalsIgnoreCase(code, StrUtil.trim(candidate));
    }

    /**
     * 选择活动表中与当前科研协议对应的变体分组。
     *
     * @param code 上游类型代码。
     * @return 已知分组，未知类型为空。
     */
    public static String variantGroup(String code) {
        for (WarframeConquestType type : values()) {
            if (type.matches(code)) {
                return type.variantGroup;
            }
        }
        return null;
    }
}
