package com.nnm.bot.warframe.plugin.runtime.arbitration;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeViewKeys;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;
import jakarta.annotation.Resource;
import java.util.List;

/** 四种仲裁排期视图共用命令处理、数据来源与展示合同。 */
public abstract class AbstractArbitrationCommandPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown(WarframeViewKeys.WORLD_STATE));
    }

    private final QueryType query;
    @Resource
    private ArbitrationCommandHandler handler;

    /**
     * 构造基础仲裁命令插件，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     */
    protected AbstractArbitrationCommandPlugin(QueryType query) {
        this.query = query;
    }

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return handler.handle(query);
    }
}
