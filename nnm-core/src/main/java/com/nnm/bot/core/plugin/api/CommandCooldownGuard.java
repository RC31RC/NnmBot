package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandDescriptor;

/**
 * 命令冷却的运行时边界。实现必须保证同一键的并发抢占不会重复放行。
 */
public interface CommandCooldownGuard {

    boolean tryAcquire(BotMessageContext context, CommandDescriptor descriptor,
                       long cooldownSeconds);
}
