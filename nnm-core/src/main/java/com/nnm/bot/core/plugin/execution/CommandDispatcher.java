package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.ExceptionCause;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.resource.ResourceScope;
import com.nnm.bot.core.plugin.model.DeliveryReport;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.core.plugin.response.CommandResponseDispatcher;
import com.nnm.bot.core.plugin.response.ReplyDeliveryException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 机器人消息的命令入口。
 *
 * <p>
 * 校验最新命令配置并调用事务管线；事务完成后统一转换拒绝/异常、渲染和投递。 命令一旦匹配，不论成功与否都不继续交给其他插件重复处理。
 * </p>
 */
@Slf4j
@Component
public class CommandDispatcher {

    @Resource
    private CommandRegistry registry;
    @Resource
    private CommandExecutionPipeline executionPipeline;
    @Resource
    private CommandResponseDispatcher responseDispatcher;
    @Resource
    private GroupCommandCoordinator coordinator;
    @Resource
    private ClusterBotRuntime cluster;

    /**
     * 同步处理已匹配命令；正常业务拒绝转换为提示，未知异常保留原始失败。
     *
     * @param match 已匹配命令；不重新匹配或交给其他插件
     */
    public void dispatch(CommandMatch match) {
        Assert.state(!TransactionSynchronizationManager.isActualTransactionActive(), "命令分派入口不能处于调用方事务中，准备和发送必须在写事务之外");
        // 作用域在事务代理之外，最终提交失败也会进入同一资源清理边界。
        try (ResourceScope resources = new ResourceScope()) {
            dispatch(match, resources);
        }
    }

    /** 在当前调用资源作用域中完成准入、准备、事务执行和回复。 */
    private void dispatch(CommandMatch match, ResourceScope resources) {
        Objects.requireNonNull(match, "match must not be null");
        // 只按稳定命令码读取最新配置，不重新匹配消息；排队期间禁用/删除必须立即生效。
        RuntimeCommand current = registry.command(match.invocation().commandCode()).orElse(null);
        if (current == null)
            return;
        match = new CommandMatch(current, match.invocation());
        BotMessageContext context = match.invocation().context();
        long started = System.nanoTime();
        String command = match.command().descriptor().code();
        log.info("Command received platform={} account={} scope={} conversation={} messageId={} command={}",
            context.platform(), context.botAccountId(), context.scope(), context.conversationId(), context.messageId(),
            command);
        CommandExecutionOutcome outcome;
        try {
            // 本地排队期间连接可能失去所有权，开始执行前必须再次校验。
            if (!cluster.isLocalOwner(new BotChannelKey(context.platform(), context.botAccountId()))) {
                log.warn("Command skipped: owner lost platform={} account={} messageId={} command={}",
                    context.platform(), context.botAccountId(), context.messageId(), command);
                return;
            }
            if (coordinator != null) {
                if (coordinator.isReplay(context)) {
                    log.info("Command skipped: replay messageId={} command={}", context.messageId(), command);
                    return;
                }
                match = coordinator.prepare(match);
                if (coordinator.shouldIgnore(match)) {
                    log.info(
                        "Command skipped: cross-channel identity incomplete or binding receipt invalid messageId={} command={}",
                        context.messageId(), command);
                    return;
                }
                match = coordinator.observe(match);
            }
            if (!executionPipeline.admit(match))
                return;
            PreparedCommand<?> prepared = PreparedCommand.prepare(match);
            if (prepared.input() instanceof AutoCloseable resource)
                resources.add(resource);
            // 准备期间连接可能转移；丢失所有权时不进入扣费及业务写事务。
            if (!cluster.isLocalOwner(new BotChannelKey(context.platform(), context.botAccountId())))
                return;
            outcome = this.executionPipeline.execute(prepared, resources);
        } catch (RuntimeException failure) {
            handleExecutionFailure(match, resources, failure);
            return;
        }
        // Pipeline 的事务代理已完成提交；渲染、平台 I/O 和投递补偿不占用业务事务。
        if (outcome != null)
            dispatchResult(match, outcome, resources);
        log.info("Command completed platform={} account={} messageId={} command={} elapsedMs={}", context.platform(),
            context.botAccountId(), context.messageId(), command, (System.nanoTime() - started) / 1_000_000);
    }

    /** 只在命令协议边界分类一次异常，准备和业务服务直接传播有语义的失败。 */
    private void handleExecutionFailure(CommandMatch match, ResourceScope resources, RuntimeException failure) {
        Throwable cause = ExceptionCause.unwrap(failure);
        if (cause instanceof Error fatal)
            throw fatal;
        if (cause instanceof CommandRejectedException rejected) {
            if (rejected.result() != null) {
                responseDispatcher.dispatch(match.invocation().context(), match.command(), rejected.result(),
                    resources);
            }
            return;
        }
        if (cause instanceof BusinessException business) {
            log.info("命令业务拒绝 command={} code={} reason={}", match.invocation().commandCode(), business.getCode(),
                business.getMessage());
            dispatchFailureNotice(match, business.getMessage(), failure);
            return;
        }
        // 已开始的投递不能再发送通用提示，避免部分成功后出现重复回复。
        if (cause instanceof ReplyDeliveryException)
            throw failure;
        log.error("命令执行失败 command={} messageId={}", match.invocation().commandCode(),
            match.invocation().context().messageId(), cause);
        String message = cause instanceof SystemException system ? system.getDescription() : "命令执行失败，请稍后再试";
        dispatchFailureNotice(match, message, failure);
        throw failure;
    }

    private void dispatchResult(CommandMatch match, CommandExecutionOutcome outcome, ResourceScope resources) {
        DeliveryReport delivery;
        try {
            delivery =
                responseDispatcher.dispatch(match.invocation().context(), match.command(), outcome.result(), resources);
        } catch (ReplyDeliveryException failure) {
            if (failure.report().definitelyUnsent()) {
                try {
                    executionPipeline.refundUnsentCommandCost(outcome.reservation(), failure);
                } catch (RuntimeException refundFailure) {
                    failure.addSuppressed(refundFailure);
                }
            }
            // 可能已经发送了部分内容，不再补发通用失败提示或重试整个业务。
            throw failure;
        }
        if (delivery.definitelyUnsent()) {
            executionPipeline.refundUnsentCommandCost(outcome.reservation(), null);
        }
    }

    private void dispatchFailureNotice(CommandMatch match, String message, RuntimeException executionFailure) {
        try {
            this.responseDispatcher.dispatchNotice(match.invocation().context(), message);
        } catch (RuntimeException noticeFailure) {
            executionFailure.addSuppressed(noticeFailure);
            log.warn("Plugin [{}] failure notice dispatch failed", match.command().descriptor().code(), noticeFailure);
        }
    }
}
