package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.core.plugin.model.RelatedParameter;

import java.util.List;
import java.util.Set;

/**
 * 机器人命令实现基类。
 *
 * <p>插件只负责处理业务逻辑。稳定元数据通过
 * {@code @BotPluginCommand} 声明，可运行时修改的配置由数据库维护。</p>
 */
public abstract class BotPlugin {

    public List<CommandView> views() {
        return List.of();
    }

    public Set<String> actionContextKeys() {
        return Set.of();
    }

    /** 按顺序拼接的显式参数契约；空列表不赋予带参关联能力。 */
    public List<RelatedParameter> relatedParameters() {
        return List.of();
    }

    public abstract CommandResult handle(CommandInvocation invocation);
}
