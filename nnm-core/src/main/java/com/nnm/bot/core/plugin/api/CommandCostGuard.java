package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.messaging.model.BotMessageContext;

/**
 * 群组积分执行前置条件的 SPI。core 提供固定实现，是否扣费或奖励由命令积分配置决定。
 */
public interface CommandCostGuard {

    /**
     * 在命令处理前预留配置要求的群组积分。
     *
     * @param context 当前调用上下文
     * @param descriptor 命令元数据
     * @return 可在提交或补偿阶段使用的预留凭据
     */
    CommandCostReservationDTO reserve(BotMessageContext context, CommandDescriptor descriptor);

    /**
     * 根据业务结果结算积分，并返回可能附带结算说明的结果。
     *
     * @param context 当前调用上下文
     * @param descriptor 命令元数据
     * @param reservation 预留凭据
     * @param result 命令业务结果
     * @return 交由回复链处理的结果
     */
    default CommandResult commit(BotMessageContext context, CommandDescriptor descriptor,
        CommandCostReservationDTO reservation, CommandResult result) {
        return result;
    }

    /**
     * 仅补偿已提交且必要回复确定未发送的消耗；业务失败由外层事务回滚。
     *
     * @param reservation 预留凭据
     * @param cause 触发补偿的投递失败
     */
    default void refund(CommandCostReservationDTO reservation, Throwable cause) {}
}
