package com.nnm.bot.warframe.plugin;

import java.util.List;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import cn.hutool.core.map.MapUtil;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.service.WarframeReferenceProjectionEnrichmentService;
import com.nnm.bot.warframe.application.WarframeWorldStateService;
import com.nnm.bot.warframe.model.WorldState;
import com.nnm.bot.warframe.pojo.WarframeWorldStateDisplay;
import jakarta.annotation.Resource;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * WorldState 命令的公共业务适配层。
 *
 * <p>不使用 WorldState 原始数据表：当前状态由官方接口抓取后只保存在 JVM 快照。展示时的静态引用
 * 由 {@code wf_community_worldstate_node}、{@code wf_community_worldstate_mission_type}、
 * {@code wf_community_worldstate_faction}、{@code wf_community_worldstate_operation_type}、
 * {@code wf_community_worldstate_fissure_modifier}、{@code wf_community_worldstate_sortie}、
 * {@code wf_community_worldstate_syndicate}、
 * {@code wf_community_worldstate_upgrade_type}、{@code wf_community_worldstate_persistent_enemy}
 * 和 {@code wf_community_worldstate_language} 解析为可展示文本。</p>
 */
public abstract class AbstractWorldStateCommandPlugin extends BotPlugin {

    private final Function<WorldState, WorldStateCommandOutput> handler;

    @Resource
    private WarframeWorldStateService worldStateService;

    @Resource
    private WarframeReferenceProjectionEnrichmentService projectionEnricher;

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
        WorldStateCommandOutput output =
                projectionEnricher.enrich(handler.apply(state), invocation.commandCode());
        if (ObjectUtil.isNull(output)) {
            return CommandResult.text("当前没有可用数据");
        }
        // 引用解析后才选择用户展示字段；不要将抓取元数据及订阅匹配标识交给渲染渠道。
        return CommandResult.of(output.text(),
                WarframeWorldStateDisplayMapper.from(
                        invocation.commandCode(), output.data(), output.unresolvedReferences()));
    }

    public record WorldStateCommandOutput(
            String text,
            Map<String, Object> data,
            List<WarframeWorldStateDisplay.UnresolvedReference> unresolvedReferences) {

        public WorldStateCommandOutput(String text, Map<String, Object> data) {
            this(text, data, List.of());
        }

        public WorldStateCommandOutput {
            text = StrUtil.emptyIfNull(text).trim();
            data = MapUtil.isEmpty(data) ? Map.of() : Map.copyOf(data);
            unresolvedReferences = unresolvedReferences == null
                    ? List.of() : List.copyOf(unresolvedReferences);
        }
    }
}
