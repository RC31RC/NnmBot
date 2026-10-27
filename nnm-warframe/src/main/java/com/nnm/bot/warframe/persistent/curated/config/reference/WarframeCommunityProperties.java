package com.nnm.bot.warframe.persistent.curated.config.reference;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.community")
public class WarframeCommunityProperties {
    private int maxBytes = 4 * 1024 * 1024;
    private int maxRecords = 20_000;
    private Duration previewTtl = Duration.ofMinutes(30);
    private Duration connectTimeout = Duration.ofSeconds(5);
    private Duration readTimeout = Duration.ofSeconds(20);
    private List<String> allowedHosts = List.of();

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (maxBytes < 1 || maxBytes > 16 * 1024 * 1024 || maxRecords < 1 || maxRecords > 50_000
                || invalid(previewTtl) || invalid(connectTimeout) || invalid(readTimeout)
                || allowedHosts == null || allowedHosts.stream().anyMatch(host -> host == null
                || !host.matches("[a-zA-Z0-9.-]+") || host.startsWith(".") || host.endsWith("."))) {
            throw new IllegalArgumentException("Invalid nnm.warframe.community import limits or host policy");
        }
    }

    /**
     * 构造包含明确输入位置的解析失败异常。
     *
     * @param duration 时长。
     * @return 满足构造包含明确输入位置的解析失败异常时为真，否则为假。
     */
    private static boolean invalid(Duration duration) {
        return duration == null || duration.isNegative() || duration.isZero();
    }
}
