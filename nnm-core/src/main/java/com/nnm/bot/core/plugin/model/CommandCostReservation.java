package com.nnm.bot.core.plugin.model;

/**
 * 命令积分预处理结果只携带业务标识，不泄漏 JPA 实体或事务上下文。
 *
 * @param allowed 是否允许继续执行
 * @param reserved 是否已完成预扣
 * @param reference 预扣记录引用
 * @param rewardPoints 命令成功后应奖励的积分
 * @param rewardReference 奖励流水幂等引用
 * @param message 拒绝或提示信息
 */
public record CommandCostReservation(boolean allowed,
                                     boolean reserved,
                                     String reference,
                                     long rewardPoints,
                                     String rewardReference,
                                     String message) {

    public CommandCostReservation {
        if (rewardPoints < 0) {
            throw new IllegalArgumentException("Command reward points must not be negative");
        }
    }

    public static CommandCostReservation none() {
        return new CommandCostReservation(true, false, null, 0, null, null);
    }

    public static CommandCostReservation denied(String message) {
        return new CommandCostReservation(false, false, null, 0, null, message);
    }

    public static CommandCostReservation approved(boolean reserved,
                                                  String reference,
                                                  long rewardPoints,
                                                  String rewardReference) {
        return new CommandCostReservation(
                true, reserved, reference, rewardPoints, rewardReference, null);
    }

    public boolean rewarding() {
        return rewardPoints > 0 && rewardReference != null && !rewardReference.isBlank();
    }
}
