package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.plugin.api.BotPlugin;

import java.util.Objects;

/**
 * 插件实现与数据库配置合并后的不可变运行时命令。
 *
 * @param plugin 实际命令处理插件
 * @param descriptor 插件提供的稳定命令描述
 * @param configuration 数据库加载的运行配置
 */
public record RuntimeCommand(
        BotPlugin plugin,
        CommandDescriptor descriptor,
        CommandExecutionConfiguration configuration
) {

    public RuntimeCommand {
        plugin = Objects.requireNonNull(plugin, "plugin must not be null");
        descriptor = Objects.requireNonNull(descriptor, "descriptor must not be null");
        configuration = Objects.requireNonNull(
                configuration, "configuration must not be null");
    }
}
