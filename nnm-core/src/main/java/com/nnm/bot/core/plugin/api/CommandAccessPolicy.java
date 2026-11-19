package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.messaging.model.BotMessageContext;
import com.nnm.bot.core.channel.constant.BotPlatform;
import com.nnm.bot.core.plugin.constant.SystemCommandCodes;
import java.util.Locale;

/**
 * 命令执行前置过滤 SPI。核心只定义边界，具体 RBAC 数据由管理模块提供。
 */
public interface CommandAccessPolicy {

    /** 判断当前消息上下文是否具备命令访问权限。
     *
     * @param context 当前消息及调用者身份
     * @param descriptor 命令静态元数据
     * @param configuration 命令运行时配置
     * @return 允许结果或稳定拒绝原因
     */
    CommandAccessDecision check(BotMessageContext context, CommandDescriptor descriptor,
        CommandExecutionConfigurationDTO configuration);

    /** 回复实例只检查自身命令能力，不用另一个渠道的身份重新判断调用者权限。
     *
     * @param platform 目标平台
     * @param botAccountId 目标机器人账号
     * @param descriptor 命令静态元数据
     * @return 允许或拒绝结果
     */
    default CommandAccessDecision checkInstance(BotPlatform platform, String botAccountId,
        CommandDescriptor descriptor) {
        return SystemCommandCodes.ALL.contains(descriptor.code().strip().toLowerCase(Locale.ROOT))
            ? CommandAccessDecision.permit() : CommandAccessDecision.denied("未提供机器人命令能力校验");
    }

    /** 以布尔值读取 {@link #check} 的授权结果。
     *
     * @param context 当前消息及调用者身份
     * @param descriptor 命令静态元数据
     * @param configuration 命令运行时配置
     * @return 获准时为 {@code true}
     */
    default boolean allowed(BotMessageContext context, CommandDescriptor descriptor,
        CommandExecutionConfigurationDTO configuration) {
        return this.check(context, descriptor, configuration).allowed();
    }
}
