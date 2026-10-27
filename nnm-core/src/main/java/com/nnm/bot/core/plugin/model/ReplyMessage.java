package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;

import java.util.List;

/** null composition 表示采用命令策略；显式约束不能被后台配置放宽。 */
public record ReplyMessage(List<ReplyBlock> blocks, List<ReplyAction> actions,
                           CompositionPolicy composition, boolean relatedActions) {
    public ReplyMessage {
        blocks = List.copyOf(blocks);
        actions = List.copyOf(actions);
        if (blocks.isEmpty()) throw new IllegalArgumentException("A logical message requires content");
    }

    public static ReplyMessage content(List<ReplyBlock> blocks) {
        return new ReplyMessage(blocks, List.of(), null, false);
    }
}
