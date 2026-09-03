package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** 保存命令积分规则的请求。 */
public record BotCommandCostSaveRequest(
        /** 单次执行消耗的积分。 */
        @Min(0) long costPoints,
        /** 是否启用积分消耗。 */
        @NotNull Boolean enabled,
        /** 单次成功执行奖励的积分。 */
        @Min(0) long rewardPoints,
        /** 是否启用成功奖励。 */
        @NotNull Boolean rewardEnabled) {
}
