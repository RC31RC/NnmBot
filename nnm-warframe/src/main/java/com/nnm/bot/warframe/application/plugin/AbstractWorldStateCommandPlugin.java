package com.nnm.bot.warframe.application.plugin;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import cn.hutool.core.map.MapUtil;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.application.WarframeReferenceProjectionEnricher;
import com.nnm.bot.warframe.application.WorldStateService;
import com.nnm.bot.warframe.domain.model.WorldState;
import jakarta.annotation.Resource;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * WorldState 命令的公共业务适配层。
 */
public abstract class AbstractWorldStateCommandPlugin extends BotPlugin {

    private final Function<WorldState, WorldStateCommandOutput> handler;

    @Resource
    private WorldStateService worldStateService;
    @Resource
    private WarframeReferenceProjectionEnricher projectionEnricher;

    protected AbstractWorldStateCommandPlugin(
            Function<WorldState, WorldStateCommandOutput> handler) {
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        WorldState state = worldStateService.getCurrentState();
        if (ObjectUtil.isNull(state)) {
            return CommandResult.text("Warframe 世界状态暂时还没有加载，请稍后再试");
        }
        WorldStateCommandOutput output = projectionEnricher.enrich(handler.apply(state));
        if (ObjectUtil.isNull(output)) {
            return CommandResult.text("当前没有可用数据");
        }
        return CommandResult.of(output.text(), output.data());
    }

    public record WorldStateCommandOutput(String text, Map<String, Object> data) {

        public WorldStateCommandOutput {
            text = StrUtil.emptyIfNull(text).trim();
            data = MapUtil.isEmpty(data) ? Map.of() : Map.copyOf(data);
        }
    }
}
