package com.nnm.bot.warframe.runtime.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class WarframeMarketConstants {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeMarketConstants() {
    }

    public enum OrderFilter {
        ALL, BUY, SELL;

        /**
         * 判断输入是否属于当前处理器允许的业务范围。
         *
         * @param type 目标业务类型。
         * @return 满足输入是否属于当前处理器允许的业务范围时为真，否则为假。
         */
        public boolean accepts(String type) {
            return ("buy".equals(type) || "sell".equals(type))
                    && (this == ALL || name().equalsIgnoreCase(type));
        }
    }
}
