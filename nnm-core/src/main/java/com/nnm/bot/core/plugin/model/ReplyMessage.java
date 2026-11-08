package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;

import java.util.List;

/** 通知等多逻辑消息使用的渠道内容；命令结果不经过此多消息模型。 */
public record ReplyMessage(List<ReplyBlock> blocks, List<ReplyAction> actions, CompositionPolicy composition) {
    public ReplyMessage {
        blocks = List.copyOf(blocks);
        actions = List.copyOf(actions);
        if (blocks.isEmpty())
            throw new IllegalArgumentException("A logical message requires content");
    }

    public static ReplyMessage content(List<ReplyBlock> blocks) {
        return new ReplyMessage(blocks, List.of(), null);
    }
}
