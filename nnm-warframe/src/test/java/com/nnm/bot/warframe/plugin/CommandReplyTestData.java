package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.ReplyBlock;

import java.util.stream.Collectors;

/** 插件测试读取显式块，不给生产结果模型保留旧固定槽位接口。 */
public final class CommandReplyTestData {
    private CommandReplyTestData() {}

    public static String text(CommandResult result) {
        return result.replies().stream().flatMap(reply -> reply.blocks().stream())
                .filter(ReplyBlock.Text.class::isInstance).map(block -> ((ReplyBlock.Text) block).content())
                .collect(Collectors.joining("\n"));
    }

    public static Object data(CommandResult result) {
        return result.replies().stream().flatMap(reply -> reply.blocks().stream())
                .filter(ReplyBlock.DataView.class::isInstance).map(block -> ((ReplyBlock.DataView<?>) block).data())
                .findFirst().orElse(null);
    }
}
