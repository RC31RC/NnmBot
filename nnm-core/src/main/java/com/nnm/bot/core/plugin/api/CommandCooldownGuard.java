package com.nnm.bot.core.plugin.api;

import com.nnm.bot.core.messaging.model.BotMessageContext;

/**
 * 命令冷却的运行时边界。实现必须保证同一键的并发抢占不会重复放行。
 */
public interface CommandCooldownGuard {

    /**
     * 原子占用当前消息对应的冷却窗口。
     *
     * @param context 当前调用上下文
     * @param descriptor 命令元数据
     * @param cooldownSeconds 冷却时长，单位为秒
     * @return 本次调用取得窗口时为 {@code true}
     */
    boolean tryAcquire(BotMessageContext context, CommandDescriptor descriptor, long cooldownSeconds);
}
