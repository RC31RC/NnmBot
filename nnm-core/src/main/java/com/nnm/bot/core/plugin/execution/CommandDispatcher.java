package com.nnm.bot.core.plugin.execution;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.core.plugin.response.CommandResponseDispatcher;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Objects;

/**
 * 机器人消息的命令入口。
 *
 * <p>这里只负责查找首个匹配命令并进入统一执行管线。命令一旦匹配，
 * 执行异常会在此终止，避免同一条消息继续交给其他插件重复处理。</p>
 */
@Slf4j
@Component
public class CommandDispatcher {

    @Resource
    private CommandRegistry commandRegistry;
    @Resource
    private CommandExecutionPipeline executionPipeline;
    @Resource
    private CommandResponseDispatcher responseDispatcher;
    @Resource
    private ObjectProvider<ClusterMessageClaimService> claimProvider;
    @Resource
    private ObjectProvider<ClusterBotRuntime> clusterProvider;

    /**
     * @return {@code true} 表示消息已匹配命令，即使后续执行失败也不会再次分发
     */
    public boolean dispatch(BotMessageContext context) {
        Objects.requireNonNull(context, "context must not be null");
        CommandMatch match = this.commandRegistry.match(context).orElse(null);
        if (ObjectUtil.isNull(match)) {
            return false;
        }
        try {
            var cluster = clusterProvider == null ? null : clusterProvider.getIfAvailable();
            // Dispatch may have waited in a local executor after its socket lost ownership.
            if (cluster != null && !cluster.isLocalOwner(new BotChannelKey(
                    context.platform(), context.botAccountId()))) return true;
            ClusterMessageClaimService claims = claimProvider == null ? null : claimProvider.getIfAvailable();
            if (claims != null && !claims.claim(context)) return true;
            this.executionPipeline.execute(match);
        } catch (Exception exception) {
            log.error("Plugin [{}] handling failed",
                    match.command().descriptor().code(), exception);
            this.dispatchFailureNotice(context, match, exception);
        }
        return true;
    }

    private void dispatchFailureNotice(BotMessageContext context, CommandMatch match,
                                      Exception executionFailure) {
        try {
            this.responseDispatcher.dispatchNotice(context, "命令执行失败，请稍后再试");
        } catch (Exception noticeFailure) {
            executionFailure.addSuppressed(noticeFailure);
            log.warn("Plugin [{}] failure notice dispatch failed",
                    match.command().descriptor().code(), noticeFailure);
        }
    }
}
