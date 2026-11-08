package com.nnm.bot.core.plugin.model;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandErrorCode;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.message.model.BotMessageReference;
import java.util.List;

/**
 * 注册表完成命令匹配后传给插件的稳定调用参数。
 *
 * @param context 当前消息上下文
 * @param commandCode 匹配到的稳定命令编码
 * @param matchedAlias 实际匹配到的命令名称
 * @param arguments 命令参数
 */
public record CommandInvocation(BotMessageContext context, String commandCode, String matchedAlias, String arguments) {

    public CommandInvocation {
        // 编码与别名只能来自已校验的命令注册表；缺失是内部匹配链路故障，不是用户参数错误。
        if (context == null) {
            throw new SystemException(CommandErrorCode.MATCH_RESULT_INVALID, "命令匹配结果缺少消息上下文");
        }
        commandCode = requireText(commandCode, "命令编码");
        matchedAlias = requireText(matchedAlias, "命中别名");
        arguments = arguments == null ? "" : arguments.trim();
    }

    /** 返回本条消息的附件元数据，不执行下载。 */
    public List<BotIncomingAttachment> attachments() {
        return context.attachments();
    }

    /** 返回显式引用元数据，不自动读取原消息。 */
    public BotMessageReference reference() {
        return context.reference();
    }

    private static String requireText(String value, String field) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new SystemException(CommandErrorCode.MATCH_RESULT_INVALID, "命令匹配结果缺少" + field);
        }
        return normalized;
    }
}
