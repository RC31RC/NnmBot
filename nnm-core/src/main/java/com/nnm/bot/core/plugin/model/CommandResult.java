package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandExecutionConstants.CommandResultStatus;

import com.nnm.bot.core.plugin.model.ReplyBlock.File;
import com.nnm.bot.core.plugin.model.ReplyBlock.Image;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** 有序逻辑回复与业务状态；投递结果由响应分发器单独产生。 */
public record CommandResult(CommandResultStatus status, List<ReplyMessage> replies,
                            Map<String, String> actionContext) {
    public CommandResult {
        Objects.requireNonNull(status, "status");
        replies = List.copyOf(replies);
        actionContext = Map.copyOf(actionContext);
        if (actionContext.size() > 32 || actionContext.entrySet().stream().anyMatch(entry ->
                !entry.getKey().matches("[a-z][a-zA-Z0-9_.]{0,63}") || entry.getValue().length() > 512)) {
            throw new IllegalArgumentException("Invalid related action context");
        }
    }

    public static CommandResult text(String text) {
        return new CommandResult(CommandResultStatus.SUCCESS, List.of(message(new ReplyBlock.Text(text))), Map.of());
    }

    public static CommandResult view(String viewKey, String title, Object data) {
        List<ReplyBlock> blocks = new ArrayList<>();
        if (title != null && !title.isBlank()) blocks.add(new ReplyBlock.Text(title));
        if (hasContent(data)) blocks.add(new ReplyBlock.DataView<>(viewKey, data, null));
        return new CommandResult(CommandResultStatus.SUCCESS,
                blocks.isEmpty() ? List.of() : List.of(ReplyMessage.content(blocks)), Map.of());
    }

    public static CommandResult viewWithSummary(String viewKey, String title, String summary, Object data) {
        List<ReplyBlock> blocks = new ArrayList<>();
        if (title != null && !title.isBlank()) blocks.add(new ReplyBlock.Text(title));
        if (hasContent(data)) blocks.add(new ReplyBlock.DataView<>(viewKey, data, summary));
        return new CommandResult(CommandResultStatus.SUCCESS,
                blocks.isEmpty() ? List.of() : List.of(ReplyMessage.content(blocks)), Map.of());
    }

    public static CommandResult file(String text, CommandAttachment attachment) {
        List<ReplyBlock> blocks = new ArrayList<>();
        if (text != null && !text.isBlank()) blocks.add(new ReplyBlock.Text(text));
        blocks.add(new ReplyBlock.File(attachment));
        return new CommandResult(CommandResultStatus.SUCCESS, List.of(ReplyMessage.content(blocks)), Map.of());
    }

    public static CommandResult rejected(String text) {
        return new CommandResult(CommandResultStatus.REJECTED, text(text).replies(), Map.of());
    }

    public boolean successful() {
        return status == CommandResultStatus.SUCCESS;
    }

    public List<CommandAttachment> resources() {
        return replies.stream().flatMap(reply -> reply.blocks().stream())
                .<CommandAttachment>map(block -> switch (block) {
                    case Image image -> image.resource();
                    case File file -> file.resource();
                    default -> null;
                }).filter(Objects::nonNull).distinct().toList();
    }

    public CommandResult withActionContext(Map<String, String> context) {
        return new CommandResult(status, replies, context);
    }

    public CommandResult withReplies(List<ReplyMessage> nextReplies) {
        return new CommandResult(status, nextReplies, actionContext);
    }

    private static ReplyMessage message(ReplyBlock block) {
        return ReplyMessage.content(List.of(block));
    }

    private static boolean hasContent(Object data) {
        if (data == null) return false;
        if (data instanceof Map<?, ?> map) return !map.isEmpty();
        if (data instanceof Collection<?> items) return !items.isEmpty();
        if (data instanceof CharSequence text) return !text.isEmpty();
        return true;
    }
}
