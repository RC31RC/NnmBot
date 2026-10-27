package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.ReplyMessage;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 将关联命令作为已完成业务结果的可选增强追加到最后一条逻辑消息。
 * 关联查询失败时保留业务结果；动作本身仍必须在模型边界内完成校验。
 */
@Slf4j
@Component
public class RelatedActionAppender {
    @Resource
    private ObjectProvider<CommandRelatedActionProvider> provider;
    @Resource
    private ReplyProperties limits;

    RelatedActionAppender() {
    }

    RelatedActionAppender(ObjectProvider<CommandRelatedActionProvider> provider, ReplyProperties limits) {
        this.provider = provider;
        this.limits = limits;
    }

    CommandResult append(BotMessageContext origin, RuntimeCommand command, CommandResult result) {
        if (command == null || !result.successful() || result.replies().isEmpty()) return result;
        CommandRelatedActionProvider actionProvider = provider.getIfAvailable();
        if (actionProvider == null) return result;

        List<ReplyAction> actions;
        try {
            actions = actionProvider.actions(origin, command, result);
        } catch (RuntimeException exception) {
            // 关联导航是可选增强；查询失败不能抹掉已经完成的业务回复。
            log.warn("Related command actions were omitted command={}", command.descriptor().code(), exception);
            return result;
        }
        if (actions == null || actions.isEmpty()) return result;

        int last = result.replies().size() - 1;
        ReplyMessage reply = result.replies().get(last);
        List<ReplyAction> combined = new ArrayList<>(reply.actions());
        for (ReplyAction action : actions) {
            if (action == null || combined.size() >= limits.getMaxActions()) break;
            if (combined.stream().noneMatch(existing -> existing.getClass() == action.getClass()
                    && existing.value().equals(action.value()))) combined.add(action);
        }
        if (combined.equals(reply.actions())) return result;
        List<ReplyMessage> replies = new ArrayList<>(result.replies());
        replies.set(last, new ReplyMessage(reply.blocks(), combined, reply.composition(), true));
        return result.withReplies(replies);
    }
}
