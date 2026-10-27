package com.nnm.bot.core.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("nnm.bot.routing")
public record CommandRoutingProperties(
        @DefaultValue("3s")
        Duration correlationWindow,
        @DefaultValue("2m")
        Duration receiptRetention,
        @DefaultValue("2048")
        int maxPendingCommands) {
    public CommandRoutingProperties {
        if (correlationWindow == null || correlationWindow.toMillis() < 1
                || correlationWindow.compareTo(Duration.ofMinutes(1)) > 0
                || receiptRetention == null || receiptRetention.compareTo(correlationWindow) < 0
                || maxPendingCommands < 1 || maxPendingCommands > 10000) {
            throw new IllegalArgumentException("Invalid command routing capacity or timing");
        }
    }
}
