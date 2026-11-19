package com.nnm.bot.core.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.Assert;

/** 群通知等待、结果轮询及通知资源的保留策略。
 *
 * @param responseTimeout 等待单个接收方回复的最长时长
 * @param retention 通知结果的保留时长
 * @param pollInterval 管理端进度轮询间隔
 * @param resourceRetention 通知附件的保留时长
 * @param maxScheduleDelay 允许预约的最远时长，限制为 1 到 90 天
 */
@ConfigurationProperties("nnm.bot.notifications")
public record BotNotificationProperties(@DefaultValue("60s") Duration responseTimeout,
    @DefaultValue("15m") Duration retention, @DefaultValue("300ms") Duration pollInterval,
    @DefaultValue("24h") Duration resourceRetention, @DefaultValue("30d") Duration maxScheduleDelay) {
    public BotNotificationProperties {
        Assert.isTrue(maxScheduleDelay != null && maxScheduleDelay.toDays() >= 1 && maxScheduleDelay.toDays() <= 90,
            "notification max schedule delay must be 1–90 days");
        Assert.isTrue(responseTimeout != null && responseTimeout.toMillis() >= 1000,
            "notification timeout must be at least 1s");
        Assert.isTrue(retention != null && retention.compareTo(responseTimeout) > 0,
            "notification retention must exceed timeout");
        Assert.isTrue(pollInterval != null && pollInterval.toMillis() >= 100,
            "notification poll interval must be at least 100ms");
        Assert.isTrue(resourceRetention != null && resourceRetention.compareTo(retention) > 0,
            "notification resource retention must exceed result retention");
    }
}
