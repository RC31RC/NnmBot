package com.nnm.bot.warframe.runtime.config.worldstate;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** WFCD 使用的独立结构化赤毒任务流；不可误作官方 PHP。 */
@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.pull.kuva-missions")
public class WarframeKuvaMissionProperties {
    private boolean enabled = true;
    private URI url = URI.create("https://10o.io/kuvalog.json");
    private long maxBytes = 4_194_304L;

    /** 校验赤毒任务增量源的配置边界。 */
    @PostConstruct
    public void validate() {
        if (url == null || url.getHost() == null || !"https".equalsIgnoreCase(url.getScheme()) || maxBytes <= 0
            || maxBytes > 8_388_608L)
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.kuva-missions configuration");
    }
}
