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

    /** 在线市场状态的代码与玩家可读文案。 */
    public enum PresenceStatus {
        ONLINE("online", "在线", true), IN_GAME("ingame", "游戏中", true), OFFLINE("offline", "离线", false),
        UNKNOWN("", null, true);

        private final String code;
        private final String label;
        private final boolean visible;

        PresenceStatus(String code, String label, boolean visible) {
            this.code = code;
            this.label = label;
            this.visible = visible;
        }

        /** @param value 市场上游状态代码；@return 对应状态，未知代码返回 UNKNOWN。 */
        public static PresenceStatus fromCode(String value) {
            if (value != null)
                for (PresenceStatus status : values())
                    if (status.code.equalsIgnoreCase(value))
                        return status;
            return UNKNOWN;
        }

        /** @param raw 上游原始状态；@return 中文状态或未识别的原始值。 */
        public String displayText(String raw) {
            return label == null ? raw : label;
        }

        /** @return 是否保留该状态的订单。 */
        public boolean isVisible() {
            return visible;
        }
    }

    /** 市场拍卖目录支持的上游类型；保留协议小写值。 */
    @Getter
    @RequiredArgsConstructor
    public enum AuctionType {
        RIVEN("riven"), LICH("lich"), SISTER("sister");

        private final String code;

        /**
         * 判断输入是否为本项目支持的拍卖类型。
         *
         * @param value 上游拍卖类型代码。
         * @return 属于受支持的拍卖类型时为真。
         */
        public static boolean supports(String value) {
            for (AuctionType type : values()) {
                if (type.code.equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }

    /** 市场模块的上游协议故障。 */
    @Getter
    @RequiredArgsConstructor
    public enum MarketErrorCode implements ErrorCode {
        RESPONSE_INVALID(502, "市场上游数据格式异常，请稍后重试", HttpStatus.BAD_GATEWAY),
        UNSUPPORTED_AUCTION_TYPE(400160, "不支持的拍卖类型", HttpStatus.BAD_REQUEST);

        private final int code;
        private final String message;
        private final HttpStatus status;
    }
}
