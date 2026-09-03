package com.nnm.bot.core.plugin.model;

/**
 * 命令积分规则投影。核心执行链只感知配置，不依赖具体存储模型。
 *
 * @param costEnabled 是否启用积分扣除
 * @param costPoints 单次执行扣除的积分数量
 * @param rewardEnabled 是否启用成功奖励
 * @param rewardPoints 单次成功执行奖励的积分数量
 */
public record CommandCostConfiguration(boolean costEnabled,
                                       long costPoints,
                                       boolean rewardEnabled,
                                       long rewardPoints) {

    public CommandCostConfiguration {
        if (costPoints < 0) {
            throw new IllegalArgumentException("Command cost points must not be negative");
        }
        if (rewardPoints < 0) {
            throw new IllegalArgumentException("Command reward points must not be negative");
        }
    }

    public static CommandCostConfiguration free() {
        return new CommandCostConfiguration(false, 0, false, 0);
    }

    public boolean chargeable() {
        return costEnabled && costPoints > 0;
    }

    public boolean rewardable() {
        return rewardEnabled && rewardPoints > 0;
    }

    public boolean active() {
        return chargeable() || rewardable();
    }
}
