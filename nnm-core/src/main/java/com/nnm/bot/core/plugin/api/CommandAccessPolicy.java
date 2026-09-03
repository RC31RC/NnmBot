package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandAccessDecision;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;

/**
 * 命令执行前置过滤 SPI。核心只定义边界，具体 RBAC 数据由管理模块提供。比如有一些命令是只有管理员才能调用
 */
public interface CommandAccessPolicy {

    CommandAccessDecision check(BotMessageContext context,
                                 CommandDescriptor descriptor,
                                 CommandExecutionConfiguration configuration);

    default boolean allowed(BotMessageContext context,
                            CommandDescriptor descriptor,
                            CommandExecutionConfiguration configuration) {
        return this.check(context, descriptor, configuration).allowed();
    }
}
