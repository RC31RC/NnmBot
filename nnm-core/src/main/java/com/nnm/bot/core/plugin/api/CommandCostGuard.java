package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandCostReservation;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandResult;

/**
 * 群组积分等可选执行前置条件的 SPI。没有实现时命令不进入积分链路。
 */
public interface CommandCostGuard {

    CommandCostReservation reserve(BotMessageContext context, CommandDescriptor descriptor);

    default CommandResult commit(BotMessageContext context,
                                 CommandDescriptor descriptor,
                                 CommandCostReservation reservation,
                                 CommandResult result) {
        return result;
    }

    default void refund(CommandCostReservation reservation, Throwable cause) {
    }
}
