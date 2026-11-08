package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotMarkdownMessage;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 将关联命令作为已完成业务结果的可选增强追加到最后一个 Markdown 块。 关联查询失败时保留业务结果；动作本身仍必须在模型边界内完成校验。
 */
@Slf4j
@Component
public class RelatedActionAppender {
    @Resource
    private ObjectProvider<CommandRelatedActionProvider> provider;
    @Resource
    private ReplyProperties limits;

    RelatedActionAppender() {}

    RelatedActionAppender(ObjectProvider<CommandRelatedActionProvider> provider, ReplyProperties limits) {
        this.provider = provider;
        this.limits = limits;
    }

    CommandResult append(BotMessageContext origin, RuntimeCommand command, CommandResult result) {
        if (command == null || !result.successful())
            return result;
        CommandRelatedActionProvider actionProvider = provider.getIfAvailable();
        if (actionProvider == null)
            return result;

        List<ReplyAction> actions;
        try {
            actions = actionProvider.actions(origin, command, result);
        } catch (RuntimeException exception) {
            // 关联导航是可选增强；查询失败不能抹掉已经完成的业务回复。
            log.warn("Related command actions were omitted command={}", command.descriptor().code(), exception);
            return result;
        }
        if (actions == null || actions.isEmpty())
            return result;

        List<ReplyBlock> blocks = new ArrayList<>(result.blocks());
        int markdownIndex = -1;
        for (int index = blocks.size() - 1; index >= 0; index--) {
            if (blocks.get(index) instanceof ReplyBlock.Markdown) {
                markdownIndex = index;
                break;
            }
        }
        BotMarkdownMessage message;
        String plainText;
        if (markdownIndex >= 0) {
            ReplyBlock.Markdown markdown = (ReplyBlock.Markdown)blocks.get(markdownIndex);
            message = markdown.message();
            plainText = markdown.plainText();
        } else {
            message = BotMarkdownMessage.content("相关查询");
            plainText = null;
            markdownIndex = blocks.size();
        }
        List<BotMarkdownMessage.Button> buttons = new ArrayList<>(message.buttons());
        int existingButtons = result.blocks().stream().filter(ReplyBlock.Markdown.class::isInstance)
            .map(ReplyBlock.Markdown.class::cast).mapToInt(block -> block.message().buttons().size()).sum();
        if (existingButtons >= limits.getMaxActions())
            return result;
        // 关联导航单独成行，保留业务分页按钮的分组含义。
        int row = buttons.isEmpty() ? 0 : buttons.getLast().row() + 1;
        int otherRows =
            result.blocks().stream().filter(ReplyBlock.Markdown.class::isInstance).map(ReplyBlock.Markdown.class::cast)
                .mapToInt(
                    block -> block.message().buttons().isEmpty() ? 0 : block.message().buttons().getLast().row() + 1)
                .sum() - (buttons.isEmpty() ? 0 : buttons.getLast().row() + 1);
        long currentRowSize = 0;
        for (ReplyAction action : actions) {
            if (action == null || existingButtons + buttons.size() - message.buttons().size() >= limits.getMaxActions()
                || buttons.size() >= 25)
                break;
            if (buttons.stream().anyMatch(button -> button.value().equals(action.value())))
                continue;
            if (currentRowSize == 5) {
                row++;
                currentRowSize = 0;
            }
            if (otherRows + row >= 5)
                break;
            buttons.add(
                new BotMarkdownMessage.Button(action.label(), action.value(), action instanceof ReplyAction.Link, row));
            currentRowSize++;
        }
        if (buttons.equals(message.buttons()))
            return result;
        ReplyBlock.Markdown markdown = new ReplyBlock.Markdown(message.withButtons(buttons), plainText);
        if (markdownIndex == blocks.size())
            blocks.add(markdown);
        else
            blocks.set(markdownIndex, markdown);
        return result.withBlocks(blocks);
    }
}
