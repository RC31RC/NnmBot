package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.plugin.constant.CommandExecutionConstants.CommandIngressResult;

import com.nnm.bot.core.messaging.model.BotMessageContext;
import java.util.function.BooleanSupplier;

/** 从消息接入边界完成快速命令匹配与有限队列接纳。 */
public interface CommandIngressService {

    /** 仅内存匹配与非等待接纳。匹配异常向调用方传播，不伪装成未匹配。 */
    CommandIngressResult accept(BotMessageContext context, BooleanSupplier connectionValid);
}
