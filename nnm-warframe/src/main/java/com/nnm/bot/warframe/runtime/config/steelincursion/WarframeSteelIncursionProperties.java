package com.nnm.bot.warframe.runtime.config.steelincursion;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** browse.wf 钢铁侵袭排期的独立来源与刷新配置。 */
@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.pull.steel-incursions")
public class WarframeSteelIncursionProperties {
    private URI url = URI.create("https://browse.wf/sp-incursions.txt");
    private Duration refreshInterval = Duration.ofDays(10);
    private Duration retryInterval = Duration.ofMinutes(1);
    private Duration dataWindow = Duration.ofDays(5);
    private long maxResponseBytes = 5 * 1024 * 1024L;

    /** 启动时校验上游地址、独立刷新周期和输入容量限制。 */
    @PostConstruct
    public void validate() {
        if (url == null || url.getHost() == null || !"https".equalsIgnoreCase(url.getScheme()))
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.steel-incursions.url");
        if (invalid(refreshInterval) || invalid(retryInterval) || invalid(dataWindow)
            || dataWindow.toSeconds() % Duration.ofDays(1).toSeconds() != 0 || dataWindow.toDays() > 30
            || maxResponseBytes < 1 || maxResponseBytes >= Integer.MAX_VALUE)
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.steel-incursions configuration");
    }

    /** @param duration 待校验的刷新或重试周期。 @return 周期未配置或不为正数时返回真。 */
    private static boolean invalid(Duration duration) {
        return duration == null || duration.isZero() || duration.isNegative();
    }
}
