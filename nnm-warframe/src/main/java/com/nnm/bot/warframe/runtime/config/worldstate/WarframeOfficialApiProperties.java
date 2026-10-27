package com.nnm.bot.warframe.runtime.config.worldstate;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.net.URI;

/** 官方实时 PHP；静态成品由固定版本的数据源通道管理。 */
@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.pull.official-api")
public class WarframeOfficialApiProperties {
    private URI worldStateUrl = URI.create("https://api.warframe.com/cdn/worldState.php");
    private String language = "zh";

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (worldStateUrl == null || worldStateUrl.getHost() == null
                || !("https".equalsIgnoreCase(worldStateUrl.getScheme()) || "http".equalsIgnoreCase(worldStateUrl.getScheme()))
                || StrUtil.isBlank(language)) {
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.official-api configuration");
        }
    }
}
