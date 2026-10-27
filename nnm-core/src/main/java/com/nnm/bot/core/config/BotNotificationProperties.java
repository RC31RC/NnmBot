package com.nnm.bot.core.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.Assert;

@ConfigurationProperties("nnm.bot.notifications")
public record BotNotificationProperties(
        @DefaultValue("60s")
        Duration responseTimeout,
        @DefaultValue("15m")
        Duration retention,
        @DefaultValue("300ms")
        Duration pollInterval,
        @DefaultValue("24h")
        Duration resourceRetention,
        @DefaultValue("30d")
        Duration maxScheduleDelay,
        @DefaultValue("1h")
        Duration resourceCleanupInterval) {
    public BotNotificationProperties {
        Assert.isTrue(maxScheduleDelay != null && maxScheduleDelay.toDays() >= 1 && maxScheduleDelay.toDays() <= 90,
                "notification max schedule delay must be 1–90 days");
        Assert.isTrue(resourceCleanupInterval != null && resourceCleanupInterval.toMinutes() >= 1,
                "notification resource cleanup interval must be at least 1m");
        Assert.isTrue(responseTimeout != null && responseTimeout.toMillis() >= 1000, "notification timeout must be at least 1s");
        Assert.isTrue(retention != null && retention.compareTo(responseTimeout) > 0, "notification retention must exceed timeout");
        Assert.isTrue(pollInterval != null && pollInterval.toMillis() >= 100, "notification poll interval must be at least 100ms");
        Assert.isTrue(resourceRetention != null && resourceRetention.compareTo(retention) > 0,
                "notification resource retention must exceed result retention");
    }
}
