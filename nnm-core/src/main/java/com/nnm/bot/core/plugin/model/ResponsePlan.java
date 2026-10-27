package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;

import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;

import com.nnm.bot.core.constant.CommandDeliveryConstants.Presentation;

import com.nnm.bot.core.message.model.BotMessagePart.Button;
import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.plugin.model.ReplyBlock.File;
import com.nnm.bot.core.plugin.model.ReplyBlock.Image;
import com.nnm.bot.core.plugin.model.ReplyBlock.Markdown;
import com.nnm.bot.core.plugin.model.ReplyBlock.Text;
import java.util.List;
import java.util.ArrayList;

/** 单条逻辑消息的渠道编译结果，不含互斥的命令返回模式。 */
public record ResponsePlan(int logicalMessage, int part, List<ReplyBlock> blocks,
                           List<PlannedReplyAction> actions, String mentionUserId,
                           CompositionPolicy composition, ViewFormat viewFormat, boolean plainMarkdown) {
    public ResponsePlan {
        if (logicalMessage < 0 || part < 0) throw new IllegalArgumentException("message indexes must not be negative");
        blocks = List.copyOf(blocks);
        actions = List.copyOf(actions);
        if (blocks.isEmpty()) throw new IllegalArgumentException("A platform message requires content");
    }

    public List<BotMessagePart> parts() {
        List<BotMessagePart> parts = new ArrayList<>(blocks.stream().map(block -> switch (block) {
            case Text text -> (BotMessagePart) new BotMessagePart.Text(text.content());
            case Image image -> new BotMessagePart.Image(image.resource().toBotFile());
            case File file -> new BotMessagePart.File(file.resource().toBotFile());
            case Markdown markdown -> new BotMessagePart.Markdown(markdown.message());
            default -> throw new IllegalStateException("Uncompiled content block");
        }).toList());
        List<Button> buttons = actions.stream().filter(action -> action.presentation() == Presentation.NATIVE)
                .map(action -> new BotMessagePart.Button(action.action().label(), action.action().value(),
                        action.action() instanceof ReplyAction.Link)).toList();
        if (!buttons.isEmpty()) parts.add(new BotMessagePart.Keyboard(buttons));
        return List.copyOf(parts);
    }

    public String text() {
        return String.join("\n", blocks.stream()
                .filter(ReplyBlock.Text.class::isInstance)
                .map(ReplyBlock.Text.class::cast)
                .map(ReplyBlock.Text::content)
                .toList());
    }

    public List<CommandAttachment> files() {
        return blocks.stream().filter(ReplyBlock.File.class::isInstance)
                .map(ReplyBlock.File.class::cast).map(ReplyBlock.File::resource).toList();
    }
}
