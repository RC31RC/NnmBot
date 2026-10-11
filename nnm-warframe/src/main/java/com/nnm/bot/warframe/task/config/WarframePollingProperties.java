package com.nnm.bot.warframe.task.config;

import cn.hutool.core.util.ObjectUtil;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.support.CronExpression;

import java.time.Duration;
import java.time.ZoneId;

@ConfigurationProperties(prefix = "nnm.warframe.polling")
public class WarframePollingProperties {

    private boolean enabled = true;
    private Duration interval = Duration.ofMinutes(2);
    private String cron = "0 */2 * * * *";
    private ZoneId zone = ZoneId.of("Asia/Shanghai");

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Duration getInterval() {
        return interval;
    }

    public void setInterval(Duration interval) {
        this.interval = interval;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public ZoneId getZone() {
        return zone;
    }

    public void setZone(ZoneId zone) {
        this.zone = zone;
    }

    public void validate() {
        if (cron == null || cron.isBlank() || !CronExpression.isValidExpression(cron)
                || zone == null) {
            throw new IllegalArgumentException("Invalid nnm.warframe.polling configuration");
        }
    }
}
