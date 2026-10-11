package com.nnm.bot.warframe.config.properties;

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
@ConfigurationProperties(prefix = "nnm.warframe.arbitration")
public class WarframeArbitrationProperties {
    private URI url = URI.create("https://browse.wf/arbys.txt");
    private String cron = "0 0 4 * * *";
    private Duration refreshInterval = Duration.ofDays(7);
    private Duration retryInterval = Duration.ofMinutes(1);
    private Duration historyRetention = Duration.ofDays(90);
    private ZoneId zone = ZoneId.of("Asia/Shanghai");
    private int previewDays = 7;
    private int previewLimit = 10;
    // browse.wf/arbys.txt currently contains several years of hourly records.
    private int maxRecords = 50_000;
    // NyxBot Arbitration.isWorth; operations may calibrate these without recompilation.
    private List<String> efficientMissionTypes = List.of("防御", "拦截", "Defense", "Interception");
    private List<String> efficientPlanets = List.of("谷神星", "水星", "Ceres", "Mercury");
    private List<String> efficientEnemies = List.of("Grineer", "Infestation", "Infested");

    @PostConstruct
    public void validate() {
        if (url == null || !List.of("http", "https").contains(url.getScheme())
                || url.getHost() == null || zone == null
                || cron == null || cron.isBlank() || !CronExpression.isValidExpression(cron)
                || invalid(refreshInterval) || invalid(retryInterval) || invalid(historyRetention)
                || previewDays < 1 || previewDays > 30 || previewLimit < 1 || previewLimit > 100
                || maxRecords < 24 || maxRecords > 100_000
                || empty(efficientMissionTypes) || empty(efficientPlanets) || empty(efficientEnemies)) {
            throw new IllegalArgumentException("Invalid nnm.warframe.arbitration configuration");
        }
    }

    private boolean invalid(Duration value) {
        return value == null || value.isNegative() || value.isZero();
    }

    private boolean empty(List<String> values) {
        return values == null || values.isEmpty()
                || values.stream().anyMatch(value -> value == null || value.isBlank());
    }
}
