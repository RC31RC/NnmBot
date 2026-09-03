package com.nnm.bot.core.plugin.model;

import cn.hutool.core.util.StrUtil;

/**
 * 命令访问责任链的稳定判定结果。
 *
 * @param allowed 是否允许继续执行
 * @param reason 拒绝原因，仅用于日志和调试，不直接作为安全凭证
 */
public record CommandAccessDecision(boolean allowed, String reason) {

    public CommandAccessDecision {
        reason = StrUtil.blankToDefault(reason, "");
    }

    public static CommandAccessDecision permit() {
        return new CommandAccessDecision(true, "");
    }

    public static CommandAccessDecision denied(String reason) {
        return new CommandAccessDecision(false, reason);
    }
}
