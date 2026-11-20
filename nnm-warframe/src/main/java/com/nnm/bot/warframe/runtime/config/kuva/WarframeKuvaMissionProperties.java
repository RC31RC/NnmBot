package com.nnm.bot.warframe.runtime.config.kuva;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** 赤毒任务独立结构化数据源及轮询配置。 */
@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.pull.kuva-missions")
public class WarframeKuvaMissionProperties {
    private Duration interval = Duration.ofMinutes(2);
    private URI url = URI.create("https://10o.io/kuvalog.json");
    private long maxBytes = 4_194_304L;

    /** 校验赤毒任务来源的 URL、周期及响应限制。 */
    @PostConstruct
    public void validate() {
        if (interval == null || interval.isZero() || interval.isNegative() || url == null || url.getHost() == null
            || !"https".equalsIgnoreCase(url.getScheme()) || maxBytes <= 0 || maxBytes > 8_388_608L)
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.kuva-missions configuration");
    }
}
