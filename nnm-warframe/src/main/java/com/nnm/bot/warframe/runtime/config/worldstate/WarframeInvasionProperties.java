package com.nnm.bot.warframe.runtime.config.worldstate;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** oracle 入侵任务类型仅用于补充官方 PHP 未提供的双方任务类型。 */
@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.pull.invasion-missions")
public class WarframeInvasionProperties {
    private boolean enabled = true;
    private URI url = URI.create("https://oracle.browse.wf/invasions");
    private long maxBytes = 1_048_576L;

    /** 校验独立补充源配置，不能因此放宽官方 PHP 请求限制。 */
    @PostConstruct
    public void validate() {
        if (url == null || url.getHost() == null || !"https".equalsIgnoreCase(url.getScheme()) || maxBytes <= 0
            || maxBytes > 8_388_608L)
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.invasion-missions configuration");
    }
}
