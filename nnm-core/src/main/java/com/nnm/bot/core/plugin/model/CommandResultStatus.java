package com.nnm.bot.core.plugin.model;

/**
 * 命令业务处理结果。只有 {@link #SUCCESS} 可以提交成功奖励。
 */
public enum CommandResultStatus {

    /** 业务处理完成，可以提交扣费和奖励。 */
    SUCCESS,

    /** 参数校验或业务条件不满足，响应可发送，但不能视为成功执行。 */
    REJECTED
}
