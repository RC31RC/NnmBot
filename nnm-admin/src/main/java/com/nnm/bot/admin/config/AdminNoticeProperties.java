package com.nnm.bot.admin.config;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 管理端通知的默认历史存活时间和单条公告最长有效期。 */
@Getter
@Setter
@Component
@ConfigurationProperties("nnm.admin.notices")
public class AdminNoticeProperties {
    private Duration taskHistoryRetention = Duration.ofDays(7);
    private Duration maxManualRetention = Duration.ofDays(90);

    @PostConstruct
    public void validate() {
        if (taskHistoryRetention == null || taskHistoryRetention.isNegative() || taskHistoryRetention.isZero()
            || maxManualRetention == null || maxManualRetention.isNegative() || maxManualRetention.isZero()) {
            throw new IllegalArgumentException("Invalid admin notice retention configuration");
        }
    }
}
