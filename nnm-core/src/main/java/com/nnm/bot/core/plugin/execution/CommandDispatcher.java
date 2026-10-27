package com.nnm.bot.core.plugin.execution;

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
import org.springframework.beans.factory.ObjectProvider;

import java.util.Objects;

/**
 * 机器人消息的命令入口。
 *
 * <p>这里只负责校验已匹配命令的最新配置并进入统一执行管线。命令一旦匹配，
 * 执行异常会在此终止，避免同一条消息继续交给其他插件重复处理。</p>
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
    private ObjectProvider<ClusterBotRuntime> clusterProvider;

    /** 同步执行已经匹配的命令；失败保留原始异常，不重新匹配或交给其他插件。 */
    public void dispatch(CommandMatch match) {
        Objects.requireNonNull(match, "match must not be null");
        // 只按稳定命令码读取最新配置，不重新匹配消息；排队期间禁用/删除必须立即生效。
        RuntimeCommand current = registry.command(match.invocation().commandCode()).orElse(null);
        if (current == null) return;
        match = new CommandMatch(current, match.invocation());
        BotMessageContext context = match.invocation().context();
        long started = System.nanoTime();
        String command = match.command().descriptor().code();
        log.info("Command received platform={} account={} scope={} conversation={} messageId={} command={}",
                context.platform(), context.botAccountId(), context.scope(), context.conversationId(), context.messageId(), command);
        try {
            ClusterBotRuntime cluster = clusterProvider.getObject();
            // Dispatch may have waited in a local executor after its socket lost ownership.
            if (!cluster.isLocalOwner(new BotChannelKey(
                    context.platform(), context.botAccountId()))) {
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
                    log.info("Command skipped: cross-channel identity incomplete or binding receipt invalid messageId={} command={}",
                            context.messageId(), command);
                    return;
                }
                match = coordinator.observe(match);
            }
            this.executionPipeline.execute(match);
            log.info("Command completed platform={} account={} messageId={} command={} elapsedMs={}",
                    context.platform(), context.botAccountId(), context.messageId(), command,
                    (System.nanoTime() - started) / 1_000_000);
        } catch (RuntimeException exception) {
            log.error("Plugin [{}] handling failed",
                    match.command().descriptor().code(), exception);
            if (!(exception instanceof ReplyDeliveryException)) {
                this.dispatchFailureNotice(match.invocation().context(), match, exception);
            }
            throw exception;
        }
    }

    private void dispatchFailureNotice(BotMessageContext context, CommandMatch match,
                                      RuntimeException executionFailure) {
        try {
            this.responseDispatcher.dispatchNotice(context, "命令执行失败，请稍后再试");
        } catch (RuntimeException noticeFailure) {
            executionFailure.addSuppressed(noticeFailure);
            log.warn("Plugin [{}] failure notice dispatch failed",
                    match.command().descriptor().code(), noticeFailure);
        }
    }
}
