package com.nnm.bot.core.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 待关联命令和平台回执的内存容量及存活时长。
 *
 * @param correlationWindow 关联命令与后续回执匹配的时间窗口
 * @param receiptRetention 已完成回执的保留时长
 * @param maxPendingCommands 同时等待关联结果的命令上限
 */
@ConfigurationProperties("nnm.bot.routing")
public record CommandRoutingProperties(@DefaultValue("3s") Duration correlationWindow,
    @DefaultValue("2m") Duration receiptRetention, @DefaultValue("2048") int maxPendingCommands) {
    public CommandRoutingProperties {
        if (correlationWindow == null || correlationWindow.toMillis() < 1
            || correlationWindow.compareTo(Duration.ofMinutes(1)) > 0 || receiptRetention == null
            || receiptRetention.compareTo(correlationWindow) < 0 || maxPendingCommands < 1
            || maxPendingCommands > 10000) {
            throw new IllegalArgumentException("Invalid command routing capacity or timing");
        }
    }
}
