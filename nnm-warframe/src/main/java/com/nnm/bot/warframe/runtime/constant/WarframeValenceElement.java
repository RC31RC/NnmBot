package com.nnm.bot.warframe.runtime.constant;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 玄骸武器元素加成的稳定输入代码与展示名称。 */
@Getter
@RequiredArgsConstructor
public enum WarframeValenceElement {
    IMPACT("冲击"), HEAT("火焰"), COLD("冰冻"), ELECTRICITY("电击"), TOXIN("毒素"), MAGNETIC("磁力"), RADIATION("辐射");

    private final String label;

    /** @param code 前端元素代码；@return 可录入的元素或空。 */
    public static WarframeValenceElement find(String code) {
        return Arrays.stream(values()).filter(element -> element.name().equals(code)).findFirst().orElse(null);
    }
}
