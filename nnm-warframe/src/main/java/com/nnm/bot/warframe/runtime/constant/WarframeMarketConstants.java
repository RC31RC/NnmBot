package com.nnm.bot.warframe.runtime.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class WarframeMarketConstants {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeMarketConstants() {}

    public enum OrderFilter {
        ALL, BUY, SELL;

        /**
         * 判断输入是否属于当前处理器允许的业务范围。
         *
         * @param type 目标业务类型。
         * @return 满足输入是否属于当前处理器允许的业务范围时为真，否则为假。
         */
        public boolean accepts(String type) {
            return ("buy".equals(type) || "sell".equals(type)) && (this == ALL || name().equalsIgnoreCase(type));
        }
    }

    /** 市场模块的上游协议故障。 */
    @Getter
    @RequiredArgsConstructor
    public enum MarketErrorCode implements ErrorCode {
        RESPONSE_INVALID(502, "市场上游数据格式异常，请稍后重试", HttpStatus.BAD_GATEWAY);

        private final int code;
        private final String message;
        private final HttpStatus status;
    }
}
