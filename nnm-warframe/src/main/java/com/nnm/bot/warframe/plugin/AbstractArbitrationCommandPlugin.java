package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.model.ArbitrationQuery;
import jakarta.annotation.Resource;

/** Four schedule views share the same handler, data source and rendering contract. */
public abstract class AbstractArbitrationCommandPlugin extends BotPlugin {
    private final ArbitrationQuery query;
    @Resource
    private ArbitrationCommandHandler handler;

    protected AbstractArbitrationCommandPlugin(ArbitrationQuery query) {
        this.query = query;
    }

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return handler.handle(query);
    }
}
