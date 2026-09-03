package com.nnm.bot.admin.dto.bot;

/** 命令积分规则展示对象。 */
public record BotCommandCostItem(
        /** 命令编码。 */
        String commandCode,
        /** 单次消耗积分。 */
        long costPoints,
        /** 是否启用积分消耗。 */
        boolean enabled,
        /** 单次成功执行奖励积分。 */
        long rewardPoints,
        /** 是否启用成功奖励。 */
        boolean rewardEnabled) {
}
