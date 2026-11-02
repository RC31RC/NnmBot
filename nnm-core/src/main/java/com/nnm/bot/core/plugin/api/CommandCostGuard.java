package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandCostReservation;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandResult;

/**
 * 群组积分执行前置条件的 SPI。core 提供固定实现，是否扣费或奖励由命令积分配置决定。
 */
public interface CommandCostGuard {

    CommandCostReservation reserve(BotMessageContext context, CommandDescriptor descriptor);

    default CommandResult commit(BotMessageContext context, CommandDescriptor descriptor,
        CommandCostReservation reservation, CommandResult result) {
        return result;
    }

    /** 仅补偿已提交且必要回复确定未发送的消耗；业务失败由外层事务回滚。 */
    default void refund(CommandCostReservation reservation, Throwable cause) {}
}
