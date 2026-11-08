package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandInput;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandInputMode;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.RelatedCommand;

import java.util.List;

/**
 * 机器人命令实现基类。
 *
 * <p>
 * 插件转换命令参数、调用业务 Service 并组装 CommandResult，不承担命令顶层事务。 稳定元数据通过 {@code @BotPluginCommand} 声明，可运行时修改的配置由数据库维护。
 * </p>
 *
 * @param <P> 事务外准备结果的类型；无需准备时使用 Void
 * @param <A> 类型化命令参数；无参数时使用 Void
 */
public abstract class BotPlugin<P, A> {

    /**
     * 声明成功业务结果中的可选关联命令；默认没有导航。源插件负责选择目标及校验入参， 回复链负责目标权限、触发词和渠道能力，不在 handle 中组装平台按钮。
     *
     * @param result 已完成的业务回复及其规范化业务上下文
     * @return 按展示顺序排列的关联命令
     */
    public List<RelatedCommand> relatedCommands(CommandResult result) {
        return List.of();
    }

    /**
     * 在写事务外准备机器人消息或附件；结构化 DTO 输入跳过此步骤，普通命令沿用默认实现即可。
     *
     * @param invocation 当前命令及本条附件、引用元数据
     * @return 准备结果；Void 命令的 null 仅表示无额外输入，不表示业务拒绝
     */
    public P prepare(CommandInvocation invocation) {
        return null;
    }

    /**
     * 解析机器人命令参数；无需解析的命令沿用空结果。
     *
     * @param invocation 当前命令调用
     * @param prepared 事务外准备的输入
     * @return 类型化参数，无参数时为 null
     */
    protected A parse(CommandInvocation invocation, P prepared) {
        return null;
    }

    /**
     * 在统一命令事务中执行；输入标记只决定是否解析文本，两种输入都经过相同校验和业务处理。 由 Dispatcher 经事务 Pipeline 调用，不作为独立协议入口。
     *
     * @param invocation 可信的命令调用上下文
     * @param prepared 事务外准备的输入
     * @param input 输入模式及已绑定的 DTO
     * @return 命令处理结果
     */
    public final CommandResult invoke(CommandInvocation invocation, P prepared, CommandInput<A> input) {
        A parsed = input.mode() == CommandInputMode.TEXT ? parse(invocation, prepared) : input.parameters();
        validate(invocation, prepared, parsed);
        return handle(invocation, prepared, parsed);
    }

    /**
     * 对文本解析与未来结构化输入共同执行的业务校验；仅需校验时覆盖。 对外开放某个结构化命令前，须将其原先仅在 parse 中完成的业务校验迁到此处或 handle。
     *
     * @param invocation 命令调用上下文
     * @param prepared 事务外准备的输入
     * @param parsed 类型化参数
     */
    protected void validate(CommandInvocation invocation, P prepared, A parsed) {}

    /**
     * 在统一命令业务事务内调用业务 Service，组装尚未发送的结果。异常须传播以回滚数据库写入。
     *
     * @param invocation 命令调用上下文
     * @param prepared 事务外准备的输入
     * @param parsed 类型化参数解析结果
     * @return 命令处理结果
     */
    public abstract CommandResult handle(CommandInvocation invocation, P prepared, A parsed);
}
