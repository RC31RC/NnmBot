package com.nnm.bot.warframe.runtime.config.arbitration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.net.URI;
import java.time.Duration;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.pull.arbitration")
public class WarframeArbitrationSourceProperties {

    private URI url = URI.create("https://browse.wf/arbys.txt");
    /** 成功拉取后的刷新间隔；失败使用独立重试间隔。 */
    private Duration refreshInterval = Duration.ofDays(10);
    /** 本实例检查刷新状态的间隔，同时作为失败后的重试等待时间。 */
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
            || invalid(refreshInterval) || invalid(retryInterval) || invalid(historyRetention) || maxRecords < 1
            || maxRecords > 100_000 || maxResponseBytes < 1 || maxResponseBytes >= Integer.MAX_VALUE) {
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
