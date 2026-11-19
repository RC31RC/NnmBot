package com.nnm.bot.warframe.persistent.curated.config.reference;

import com.nnm.bot.common.error.SystemException;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.community")
public class WarframeCommunityProperties {
    private int maxBytes = 4 * 1024 * 1024;
    private int maxRecords = 20_000;
    private int maxValidationIssues = 100;
    private Duration previewTtl = Duration.ofMinutes(30);

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (maxBytes < 1 || maxBytes > 16 * 1024 * 1024 || maxRecords < 1 || maxRecords > 50_000
            || maxValidationIssues < 1 || maxValidationIssues > 1000 || invalid(previewTtl)) {
            throw new SystemException(WarframeManagementErrorCode.COMMUNITY_CONFIGURATION_INVALID,
                "Invalid nnm.warframe.community import limits");
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
