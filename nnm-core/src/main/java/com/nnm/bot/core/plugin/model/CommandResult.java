package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandExecutionConstants.CommandResultStatus;

import com.nnm.bot.core.plugin.model.ReplyBlock.File;
import com.nnm.bot.core.plugin.model.ReplyBlock.Image;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

/** 单条命令逻辑消息及业务状态；最终渠道消息由对应适配器编译。 */
public record CommandResult(CommandResultStatus status, List<ReplyBlock> blocks, Map<String, String> actionContext) {
    public CommandResult {
        Objects.requireNonNull(status, "status");
        blocks = List.copyOf(blocks);
        actionContext = Map.copyOf(actionContext);
        if (actionContext.size() > 32 || actionContext.entrySet().stream()
            .anyMatch(entry -> entry.getKey() == null || !entry.getKey().matches("[a-z][a-zA-Z0-9_.]{0,63}")
                || entry.getValue() == null || entry.getValue().length() > 512)) {
            throw new IllegalArgumentException("Invalid related action context");
        }
        if (blocks.isEmpty())
            throw new IllegalArgumentException("A command result requires message content");
        if (blocks.stream().filter(ReplyBlock.HtmlData.class::isInstance).count() > 1)
            throw new IllegalArgumentException("A command result can contain at most one HTML data object");
    }

    public static CommandResult text(String text) {
        return new CommandResult(CommandResultStatus.SUCCESS, List.of(new ReplyBlock.Text(text)), Map.of());
    }

    public static CommandResult html(String title, Object data) {
        ArrayList<ReplyBlock> blocks = new ArrayList<>();
        if (title != null && !title.isBlank())
            blocks.add(new ReplyBlock.Text(title));
        if (hasContent(data))
            blocks.add(new ReplyBlock.HtmlData<>(data));
        if (blocks.isEmpty())
            return text("没有可展示的数据");
        return new CommandResult(CommandResultStatus.SUCCESS, blocks, Map.of());
    }

    public static CommandResult of(List<ReplyBlock> blocks) {
        return new CommandResult(CommandResultStatus.SUCCESS, blocks, Map.of());
    }

    public static CommandResult file(String text, CommandAttachment attachment) {
        ArrayList<ReplyBlock> blocks = new ArrayList<>();
        if (text != null && !text.isBlank())
            blocks.add(new ReplyBlock.Text(text));
        blocks.add(new ReplyBlock.File(attachment));
        return new CommandResult(CommandResultStatus.SUCCESS, blocks, Map.of());
    }

    public static CommandResult rejected(String text) {
        return new CommandResult(CommandResultStatus.REJECTED, List.of(new ReplyBlock.Text(text)), Map.of());
    }

    public boolean successful() {
        return status == CommandResultStatus.SUCCESS;
    }

    public List<CommandAttachment> resources() {
        return blocks.stream().<CommandAttachment>map(block -> switch (block) {
            case Image image -> image.resource();
            case File file -> file.resource();
            default -> null;
        }).filter(Objects::nonNull).distinct().toList();
    }

    public CommandResult withActionContext(Map<String, String> context) {
        return new CommandResult(status, blocks, context);
    }

    public CommandResult withBlocks(List<ReplyBlock> nextBlocks) {
        return new CommandResult(status, nextBlocks, actionContext);
    }

    private static boolean hasContent(Object data) {
        if (data == null)
            return false;
        if (data instanceof Map<?, ?> map)
            return !map.isEmpty();
        if (data instanceof Collection<?> items)
            return !items.isEmpty();
        if (data instanceof CharSequence text)
            return !text.isEmpty();
        return true;
    }
}
