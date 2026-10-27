package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandAccessDecision;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import java.util.Locale;

/**
 * 命令执行前置过滤 SPI。核心只定义边界，具体 RBAC 数据由管理模块提供。比如有一些命令是只有管理员才能调用
 */
public interface CommandAccessPolicy {

    CommandAccessDecision check(BotMessageContext context,
                                 CommandDescriptor descriptor,
                                 CommandExecutionConfiguration configuration);

    /** 回复实例只检查自身命令能力，不用另一个渠道的身份重新判断调用者权限。 */
    default CommandAccessDecision checkInstance(BotPlatform platform, String botAccountId,
                                                CommandDescriptor descriptor) {
        return SystemCommandCodes.ALL.contains(descriptor.code().strip().toLowerCase(Locale.ROOT))
                ? CommandAccessDecision.permit()
                : CommandAccessDecision.denied("未提供机器人命令能力校验");
    }

    default boolean allowed(BotMessageContext context,
                            CommandDescriptor descriptor,
                            CommandExecutionConfiguration configuration) {
        return this.check(context, descriptor, configuration).allowed();
    }
}
