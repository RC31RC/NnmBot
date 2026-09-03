package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;

/**
 * 机器人命令实现基类。
 *
 * <p>插件只负责处理业务逻辑。稳定元数据通过
 * {@code @BotPluginCommand} 声明，可运行时修改的配置由数据库维护。</p>
 */
public abstract class BotPlugin {

    public abstract CommandResult handle(CommandInvocation invocation);
}
