package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;

import com.nnm.bot.core.constant.CommandDeliveryConstants.Presentation;

import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.message.model.BotMessagePart.Button;
import com.nnm.bot.core.plugin.model.ReplyBlock.File;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandErrorCode;
import com.nnm.bot.core.plugin.model.ReplyBlock.Image;
import com.nnm.bot.core.plugin.model.ReplyBlock.Markdown;
import com.nnm.bot.core.plugin.model.ReplyBlock.Text;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/** 单条逻辑消息的渠道编译结果，不含互斥的命令返回模式。 */
public record ResponsePlan(int logicalMessage, int part, List<ReplyBlock> blocks, List<PlannedReplyAction> actions,
    String mentionUserId, CompositionPolicy composition, boolean plainMarkdown) {
    public ResponsePlan {
        if (logicalMessage < 0 || part < 0)
            throw new IllegalArgumentException("message indexes must not be negative");
        blocks = List.copyOf(blocks);
        actions = List.copyOf(actions);
        if (blocks.isEmpty())
            throw new IllegalArgumentException("A platform message requires content");
    }

    public List<BotMessagePart> parts() {
        List<BotMessagePart> parts = new ArrayList<>(blocks.stream().map(block -> switch (block) {
            case Text text -> (BotMessagePart)new BotMessagePart.Text(text.content());
            case Image image -> new BotMessagePart.Image(image.resource().toBotFile());
            case File file -> new BotMessagePart.File(file.resource().toBotFile());
            case Markdown markdown -> plainMarkdown
                ? new BotMessagePart.Text(markdown.plainTextContent() + markdown.message().buttons().stream()
                    .map(button -> "\n" + button.label() + "：" + button.value()).collect(Collectors.joining()))
                : new BotMessagePart.Markdown(markdown.message(), markdown.plainTextContent());
            default -> throw new SystemException(CommandErrorCode.DELIVERY_PLAN_INVALID, "Uncompiled content block");
        }).toList());
        List<Button> buttons = actions.stream().filter(action -> action.presentation() == Presentation.NATIVE)
            .map(action -> new BotMessagePart.Button(action.action().label(), action.action().value(),
                action.action() instanceof ReplyAction.Link, 0))
            .toList();
        if (!buttons.isEmpty()) {
            List<BotMessagePart.Button> rows = new ArrayList<>();
            for (int index = 0; index < buttons.size(); index++) {
                BotMessagePart.Button button = buttons.get(index);
                rows.add(new BotMessagePart.Button(button.label(), button.value(), button.link(), index / 2));
            }
            parts.add(new BotMessagePart.Keyboard(rows));
        }
        return List.copyOf(parts);
    }

    public String text() {
        return String.join("\n", blocks.stream().filter(ReplyBlock.Text.class::isInstance)
            .map(ReplyBlock.Text.class::cast).map(ReplyBlock.Text::content).toList());
    }

    public List<CommandAttachment> files() {
        return blocks.stream().filter(ReplyBlock.File.class::isInstance).map(ReplyBlock.File.class::cast)
            .map(ReplyBlock.File::resource).toList();
    }
}
