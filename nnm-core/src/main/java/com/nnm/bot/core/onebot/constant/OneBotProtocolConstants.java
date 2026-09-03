package com.nnm.bot.core.onebot.constant;

/** OneBot 11 探测协议使用的固定字段和值。 */
public final class OneBotProtocolConstants {

    public static final String ACTION_GET_LOGIN_INFO = "get_login_info";
    public static final String STATUS_OK = "ok";
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

    private OneBotProtocolConstants() {
    }
}
