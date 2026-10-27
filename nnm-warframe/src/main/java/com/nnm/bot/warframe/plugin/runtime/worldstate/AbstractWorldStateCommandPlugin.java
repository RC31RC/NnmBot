package com.nnm.bot.warframe.plugin.runtime.worldstate;

import cn.hutool.core.util.ObjectUtil;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeViewKeys;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateDisplayMapper;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeReferenceProjectionEnrichmentService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * WorldStateDTO 命令的公共业务适配层。
 *
 * <p>当前状态来自官方运行时快照；展示时使用已采纳静态官方关系树作代码簿。
 * 没有对应定义的引用保留为未解析值，不从旧社区投影推测。</p>
 */
public abstract class AbstractWorldStateCommandPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown(WarframeViewKeys.WORLD_STATE));
    }

    private final Function<WorldStateDTO, WarframeWorldStateCommandVO> handler;

    @Resource
    private WarframeWorldStateService worldStateService;

    @Resource
    private WarframeReferenceProjectionEnrichmentService projectionEnricher;

    /**
     * 构造基础世界状态命令插件，并保存初始化参数。
     *
     * @param handler 处理器。
     */
    protected AbstractWorldStateCommandPlugin(
            Function<WorldStateDTO, WarframeWorldStateCommandVO> handler) {
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        WorldStateDTO state = worldStateService.getCurrentState();
        if (ObjectUtil.isNull(state)) {
            return CommandResult.text("Warframe 世界状态暂时还没有加载，请稍后再试");
        }
        WarframeWorldStateCommandVO output =
                projectionEnricher.enrich(handler.apply(state), invocation.commandCode());
        if (ObjectUtil.isNull(output)) {
            return CommandResult.text("当前没有可用数据");
        }
        // 引用解析后才选择用户展示字段；不要将抓取元数据及订阅匹配标识交给渲染渠道。
        return CommandResult.view(WarframeViewKeys.WORLD_STATE, output.text(),
                WarframeWorldStateDisplayMapper.from(
                        invocation.commandCode(), output.data(), output.unresolvedReferences()));
    }

}
