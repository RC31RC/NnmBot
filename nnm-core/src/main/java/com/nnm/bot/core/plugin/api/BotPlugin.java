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
 * <p>
 * 插件转换命令参数、调用业务 Service 并组装 CommandResult，不承担命令顶层事务。 稳定元数据通过 {@code @BotPluginCommand} 声明，可运行时修改的配置由数据库维护。
 * </p>
 *
 * @param <P> 本命令准备输入的类型；无需准备时使用 Void
 */
public abstract class BotPlugin<P> {

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

    /**
     * 在写事务外准备本命令输入；普通命令沿用默认实现即可。
     *
     * @param invocation 当前命令及本条附件、引用元数据
     * @return 准备结果；Void 命令的 null 仅表示无额外输入，不表示业务拒绝
     */
    public P prepare(CommandInvocation invocation) {
        return null;
    }

    /**
     * 在统一命令业务事务内同步调用业务 Service，返回尚未渲染或发送的结果。
     *
     * <p>
     * 异常必须传播以回滚数据库写入；REJECTED 或 null 也由管线转为回滚信号。 不在插件中直接发送消息，不将事务、锁或业务身份隐式传播到异步线程。
     * </p>
     *
     * @param invocation 命令调用上下文及已匹配参数
     * @param prepared 本 Plugin 在事务外准备的输入，Void 命令为 null
     * @return 成功/拒绝结果；null 表示不回复且回滚本次业务
     */
    public abstract CommandResult handle(CommandInvocation invocation, P prepared);
}
