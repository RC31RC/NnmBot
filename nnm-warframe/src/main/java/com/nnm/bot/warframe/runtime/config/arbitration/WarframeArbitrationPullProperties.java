package com.nnm.bot.warframe.runtime.config.arbitration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.support.CronExpression;
import java.net.URI;
import java.time.Duration;
import java.time.ZoneId;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.pull.arbitration")
public class WarframeArbitrationPullProperties {

    private boolean enabled = true;
    private URI url = URI.create("https://browse.wf/arbys.txt");
    private String cron = "0 */5 * * * *";
    private ZoneId zone = ZoneId.of("Asia/Shanghai");
    private Duration refreshInterval = Duration.ofMinutes(30);
    private Duration retryInterval = Duration.ofMinutes(1);
    private Duration historyRetention = Duration.ofDays(90);
    private int maxRecords = 50_000;
    private long maxResponseBytes = 5 * 1024 * 1024L;

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (url == null || url.getScheme() == null || !List.of("http", "https").contains(url.getScheme())
                || url.getHost() == null || url.getUserInfo() != null || url.getFragment() != null
                || cron == null || !CronExpression.isValidExpression(cron) || zone == null
                || invalid(refreshInterval) || invalid(retryInterval) || invalid(historyRetention)
                || maxRecords < 1 || maxRecords > 100_000
                || maxResponseBytes < 1 || maxResponseBytes >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.arbitration configuration");
        }
    }

    /**
     * 构造包含明确输入位置的解析失败异常。
     *
     * @param value 待处理的字段值。
     * @return 满足构造包含明确输入位置的解析失败异常时为真，否则为假。
     */
    private static boolean invalid(Duration value) {
        return value == null || value.isNegative() || value.isZero();
    }
}
