package com.nnm.bot.core.plugin.system;

import java.util.Set;

/**
 * 所有机器人实例默认具备的系统命令编码。
 */
public final class SystemCommandCodes {

    public static final String HELP = "help";
    public static final String STATUS = "status";
    public static final String SIGN_IN = "sign-in";
    public static final String BIND = "bind";
    public static final String JM = "jm";

    public static final Set<String> ALL = Set.of(HELP, STATUS, SIGN_IN, BIND, JM);

    private SystemCommandCodes() {
    }
}
