package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.plugin.model.CommandCostReservation;
import com.nnm.bot.core.plugin.model.CommandResult;

/** 已完成业务结算的执行结果，不携带事务、锁作用域或 JPA 实体。 */
public record CommandExecutionOutcome(CommandResult result, CommandCostReservation reservation) {
}
