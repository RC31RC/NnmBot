package com.nnm.bot.core.message.service;

import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import java.util.Optional;

/** 图片命令的按需输入选择；普通命令不会调用此服务。 */
public interface MessageInputService {

    /**
     * 优先返回本条第一张图片，其次显式引用图片；不执行 OCR。
     *
     * @param invocation 已匹配且取得执行权的命令
     * @return 可供业务下载的图片；没有图且没有显式引用时为空
     */
    Optional<BotIncomingAttachment> firstImage(CommandInvocation invocation);
}
