package com.nnm.bot.warframe.application.plugin;

import cn.hutool.core.util.ObjectUtil;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;

import java.util.List;
import java.util.Map;

/**
 * 已登记但尚未接入可靠数据服务的命令基类。
 */
public abstract class DataSourceCommandPlugin extends BotPlugin {

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return CommandResult.of(
                "该功能已登记，但真实数据服务尚未接入，当前不返回演示数据",
                Map.of(
                        "available", false,
                        "commandCode", invocation.commandCode(),
                        "arguments", invocation.arguments(),
                        "items", List.of()));
    }
}
