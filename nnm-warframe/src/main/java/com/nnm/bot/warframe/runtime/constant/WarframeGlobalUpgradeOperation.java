package com.nnm.bot.warframe.runtime.constant;

import java.math.BigDecimal;

/** 全局增益数值的已知协议运算，不根据倍数猜测增益内容。 */
public enum WarframeGlobalUpgradeOperation {
    MULTIPLY;

    /** @param code 上游运算代码；@return 已知运算或空。 */
    public static WarframeGlobalUpgradeOperation fromCode(String code) {
        return MULTIPLY.name().equals(code) ? MULTIPLY : null;
    }

    /** @param amount 上游数值；@return 带运算符的可读数值，缺值时为空。 */
    public String format(Double amount) {
        return amount == null || !Double.isFinite(amount) ? null
            : "×" + BigDecimal.valueOf(amount).stripTrailingZeros().toPlainString();
    }
}
