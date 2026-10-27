package com.nnm.bot.admin.config;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.ZoneId;
import org.springframework.scheduling.support.CronExpression;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("nnm.bot.notifications.jobs")
public class NotificationJobProperties {
    private Duration dispatchGrace = Duration.ofMinutes(10);
    private Duration staleTimeout = Duration.ofMinutes(10);
    private Duration historyRetention = Duration.ofDays(7);
    private int maxPendingPerUser = 20;
    private String cron = "*/5 * * * * *";
    private String zone = "Asia/Shanghai";
    @PostConstruct
    public void validate() {
        if (dispatchGrace == null || dispatchGrace.toSeconds() < 1 || dispatchGrace.compareTo(Duration.ofDays(1)) > 0
                || staleTimeout == null || staleTimeout.toMinutes() < 2 || staleTimeout.compareTo(Duration.ofDays(1)) > 0
                || historyRetention == null || historyRetention.toDays() < 1 || historyRetention.toDays() > 90
                || maxPendingPerUser < 1 || maxPendingPerUser > 200) {
            throw new IllegalArgumentException("Invalid notification job lifecycle configuration");
        }
        ZoneId.of(zone);
        CronExpression.parse(cron);
    }
}
