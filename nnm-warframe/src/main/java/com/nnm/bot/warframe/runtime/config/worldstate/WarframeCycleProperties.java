package com.nnm.bot.warframe.runtime.config.worldstate;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.query.cycles")
public class WarframeCycleProperties {
    // warframe-worldstate-parser/lib/models/VallisCycle.ts, 核对日期：2026-09-12。
    private Instant vallisWarmEpoch = Instant.parse("2026-02-04T19:46:48Z");
    // warframe-worldstate-parser/lib/models/ZarimanCycle.ts, 核对日期：2026-09-12。
    private Instant zarimanCorpusEpoch = Instant.ofEpochMilli(1_655_182_800_000L);
    // 校正量保持小于半分钟，避免取整把已对齐的赏金推回上一周期。
    private Duration zarimanBountyOffset = Duration.ofSeconds(5);
    // warframe-worldstate-parser/lib/models/DuviriCycle.ts, 核对日期：2026-09-12。
    private Instant duviriEpoch = Instant.EPOCH.plusSeconds(52);
    private Duration cetusAnchorMaxAge = Duration.ofHours(24);

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (vallisWarmEpoch == null || zarimanCorpusEpoch == null || zarimanBountyOffset == null
            || zarimanBountyOffset.isNegative() || zarimanBountyOffset.compareTo(Duration.ofSeconds(30)) >= 0
            || duviriEpoch == null || cetusAnchorMaxAge == null || cetusAnchorMaxAge.isNegative()
            || cetusAnchorMaxAge.isZero()) {
            throw new IllegalArgumentException("Invalid nnm.warframe.query.cycles configuration");
        }
    }
}
