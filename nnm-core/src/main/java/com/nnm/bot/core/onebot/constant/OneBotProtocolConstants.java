package com.nnm.bot.core.onebot.constant;

import cn.hutool.core.text.CharSequenceUtil;

/** OneBot 11 探测协议使用的固定字段和值。 */
public final class OneBotProtocolConstants {

    public static final String ACTION_GET_LOGIN_INFO = "get_login_info";
    public static final String FIELD_ACTION = "action";
    public static final String FIELD_ECHO = "echo";
    public static final String FIELD_PARAMS = "params";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_RETCODE = "retcode";
    public static final String FIELD_DATA = "data";
    public static final String FIELD_USER_ID = "user_id";
    public static final String ECHO_PREFIX = "nnmbot-account-probe-";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String WS_SCHEME = "ws";
    public static final String WSS_SCHEME = "wss";

    private OneBotProtocolConstants() {}

    /** OneBot 动作响应的协议状态，未知值保持失败语义。 */
    public enum ResponseStatus {
        OK("ok"), FAILED("failed"), UNKNOWN("");

        private final String code;

        ResponseStatus(String code) {
            this.code = code;
        }

        /** @param value 上游响应状态；@return 协议状态，未识别值返回 UNKNOWN。 */
        public static ResponseStatus fromCode(String value) {
            if (CharSequenceUtil.isBlank(value))
                return UNKNOWN;
            for (ResponseStatus status : values())
                if (status.code.equalsIgnoreCase(value))
                    return status;
            return UNKNOWN;
        }

        /** @return 响应明确表示成功时为真。 */
        public boolean isSuccess() {
            return this == OK;
        }

        /** @return 响应明确表示失败时为真。 */
        public boolean isFailure() {
            return this == FAILED;
        }
    }
}
