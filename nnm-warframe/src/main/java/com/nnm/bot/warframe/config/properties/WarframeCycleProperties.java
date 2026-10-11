package com.nnm.bot.warframe.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.cycles")
public class WarframeCycleProperties {
    // warframe-worldstate-parser/lib/models/VallisCycle.ts, audited 2026-09-12.
    private Instant vallisWarmEpoch = Instant.parse("2026-02-04T19:46:48Z");
    // warframe-worldstate-parser/lib/models/ZarimanCycle.ts, audited 2026-09-12.
    private Instant zarimanCorpusEpoch = Instant.ofEpochMilli(1_655_182_800_000L);
    // Keep the correction below half a minute so rounding cannot shift an aligned bounty backwards.
    private Duration zarimanBountyOffset = Duration.ofSeconds(5);
    // warframe-worldstate-parser/lib/models/DuviriCycle.ts, audited 2026-09-12.
    private Instant duviriEpoch = Instant.EPOCH.plusSeconds(52);
    private Duration cetusAnchorMaxAge = Duration.ofHours(24);

    public void validate() {
        if (vallisWarmEpoch == null || zarimanCorpusEpoch == null || zarimanBountyOffset == null
                || zarimanBountyOffset.isNegative()
                || zarimanBountyOffset.compareTo(Duration.ofSeconds(30)) >= 0 || duviriEpoch == null
                || cetusAnchorMaxAge == null
                || cetusAnchorMaxAge.isNegative() || cetusAnchorMaxAge.isZero()) {
            throw new IllegalArgumentException("Invalid nnm.warframe.cycles configuration");
        }
    }
}
