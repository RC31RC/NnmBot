package com.nnm.bot.core.constant;

import cn.hutool.core.util.StrUtil;

/**
 * 前缀命令触发词与参数之间的边界策略。
 */
public enum CommandBoundaryPolicy {

    /** 触发词后存在参数时必须以空白分隔。 */
    WHITESPACE_REQUIRED,

    /** 允许空白或 CJK 字符直接跟在触发词后。 */
    CJK_OR_WHITESPACE,

    /** 不限制触发词与参数之间的边界。 */
    NONE;

    public static CommandBoundaryPolicy defaultFor(String trigger) {
        if (StrUtil.isBlank(trigger)) {
            return WHITESPACE_REQUIRED;
        }
        int last = trigger.codePointBefore(trigger.length());
        return last < 128 && Character.isLetterOrDigit(last)
                ? WHITESPACE_REQUIRED
                : NONE;
    }
}
