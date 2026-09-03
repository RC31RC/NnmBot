package com.nnm.bot.scheduler.config;

import cn.hutool.core.util.ObjectUtil;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "nnm.warframe.polling")
public class WarframePollingProperties {

    private boolean enabled = true;
    private Duration interval = Duration.ofMinutes(2);

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

    public void validate() {
        if (ObjectUtil.isNull(interval) || interval.isZero() || interval.isNegative()) {
            throw new IllegalArgumentException("nnm.warframe.polling.interval must be positive");
        }
    }
}
