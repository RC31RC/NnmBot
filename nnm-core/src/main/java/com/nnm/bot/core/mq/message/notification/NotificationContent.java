package com.nnm.bot.core.mq.message.notification;

import com.nnm.bot.core.constant.NotificationConstants.BlockType;

import com.nnm.bot.core.constant.NotificationConstants.ActionType;

import com.nnm.bot.core.message.model.BotMarkdownMessage;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;
import com.nnm.bot.core.plugin.model.PresentationPolicy;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ReplyMessage;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/** 队列只承载有限的描述与资源 ID，不承载本地路径、图片字节或任意多态对象。 */
public record NotificationContent(List<Message> messages, CompositionPolicy composition,
                                   InteractionPolicy interaction) {
    public NotificationContent {
        messages = List.copyOf(messages);
        if (messages.isEmpty()) throw new IllegalArgumentException("通知至少需要一条逻辑消息");
        Objects.requireNonNull(composition, "composition");
        Objects.requireNonNull(interaction, "interaction");
        if (interaction == InteractionPolicy.DISABLED && messages.stream().flatMap(message -> message.actions().stream())
                .anyMatch(Action::required)) throw new IllegalArgumentException("必要动作不能关闭");
    }

    /** Validate request/queue content against the effective deploy-time reply budget. */
    public void validate(ReplyProperties limits) {
        Objects.requireNonNull(limits, "limits");
        if (messages.size() > limits.getMaxLogicalMessages()) {
            throw new IllegalArgumentException("通知逻辑消息数量超过限制");
        }
        int blocks = messages.stream().mapToInt(message -> message.blocks().size()).sum();
        if (blocks > limits.getMaxBlocks()) throw new IllegalArgumentException("通知内容块不能超过限制");
        int actions = messages.stream().mapToInt(message -> message.actions().size()).sum();
        if (actions > limits.getMaxActions()) throw new IllegalArgumentException("通知动作数量超过限制");
        long characters = messages.stream().flatMap(message -> message.blocks().stream())
                .mapToLong(block -> length(block.text()) + length(block.plainText())).sum();
        if (characters > limits.getMaxTextCharacters()) throw new IllegalArgumentException("通知文字总量超过限制");
    }

    public PresentationPolicy presentation() {
        return new PresentationPolicy(List.of(ViewFormat.TEXT), composition, interaction, List.of());
    }

    public List<UUID> resourceIds() {
        return messages.stream().flatMap(message -> message.blocks().stream()).map(Block::resourceId)
                .filter(Objects::nonNull).distinct().toList();
    }

    public List<ReplyMessage> replies(Function<UUID, CommandAttachment> resources) {
        return messages.stream().map(message -> new ReplyMessage(message.blocks().stream().map(block -> switch (block.type()) {
            case TEXT -> (ReplyBlock) new ReplyBlock.Text(block.text());
            case MARKDOWN -> new ReplyBlock.Markdown(BotMarkdownMessage.content(block.text()), block.plainText());
            case IMAGE -> new ReplyBlock.Image(resources.apply(block.resourceId()));
        }).toList(), message.actions().stream().map(Action::replyAction).toList(), message.composition(), false)).toList();
    }

    private static int length(String value) { return value == null ? 0 : value.length(); }

    public record Message(List<Block> blocks, List<Action> actions, CompositionPolicy composition) {
        public Message {
            blocks = List.copyOf(blocks);
            actions = List.copyOf(actions);
            if (blocks.isEmpty()) throw new IllegalArgumentException("通知至少需要一个内容块");
        }
    }

    public record Block(BlockType type, String text, UUID resourceId, String plainText) {
        public Block {
            Objects.requireNonNull(type, "type");
            if (type == BlockType.IMAGE) {
                if (resourceId == null || text != null || plainText != null) throw new IllegalArgumentException("图片仅引用上传资源 ID");
            } else {
                if (text == null || text.isBlank() || resourceId != null || type == BlockType.TEXT && plainText != null) {
                    throw new IllegalArgumentException("无效通知文字块");
                }
                if (plainText != null && plainText.isBlank()) throw new IllegalArgumentException("Markdown 文字替代不能为空");
            }
        }
    }

    public record Action(ActionType type, String label, String value, boolean required) {
        public Action {
            Objects.requireNonNull(type, "type");
            // 共用动作边界校验，禁止平台 JSON、控制字符与非 HTTP(S) 链接。
            if (type == ActionType.LINK) new ReplyAction.Link(label, value, required);
            else new ReplyAction.Command(label, value, required);
        }

        public ReplyAction replyAction() {
            return type == ActionType.LINK ? new ReplyAction.Link(label, value, required)
                    : new ReplyAction.Command(label, value, required);
        }
    }
}
