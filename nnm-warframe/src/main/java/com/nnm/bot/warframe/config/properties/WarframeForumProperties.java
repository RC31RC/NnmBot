package com.nnm.bot.warframe.config.properties;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "nnm.warframe.forum")
public class WarframeForumProperties {

    private URI publicBaseUrl = URI.create("http://localhost:3006");

    private String publicDetailPath = "/#/warframe/forum/detail";

    private long snowflakeWorkerId = 1;

    private long snowflakeDataCenterId = 1;

    public URI getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(URI publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public String getPublicDetailPath() {
        return publicDetailPath;
    }

    public void setPublicDetailPath(String publicDetailPath) {
        this.publicDetailPath = publicDetailPath;
    }

    public long getSnowflakeWorkerId() {
        return snowflakeWorkerId;
    }

    public void setSnowflakeWorkerId(long snowflakeWorkerId) {
        this.snowflakeWorkerId = snowflakeWorkerId;
    }

    public long getSnowflakeDataCenterId() {
        return snowflakeDataCenterId;
    }

    public void setSnowflakeDataCenterId(long snowflakeDataCenterId) {
        this.snowflakeDataCenterId = snowflakeDataCenterId;
    }

    public void validate() {
        if (publicBaseUrl == null
                || publicBaseUrl.getHost() == null
                || (!"http".equalsIgnoreCase(publicBaseUrl.getScheme())
                && !"https".equalsIgnoreCase(publicBaseUrl.getScheme()))) {
            throw new IllegalArgumentException(
                    "nnm.warframe.forum.public-base-url must be an absolute HTTP URL");
        }
        if (StrUtil.isBlank(publicDetailPath)
                || !publicDetailPath.startsWith("/#/warframe/forum/detail")) {
            throw new IllegalArgumentException(
                    "nnm.warframe.forum.public-detail-path must be a hash route under "
                            + "/#/warframe/forum/detail");
        }
        if (snowflakeWorkerId < 0 || snowflakeWorkerId > 31) {
            throw new IllegalArgumentException(
                    "nnm.warframe.forum.snowflake-worker-id must be between 0 and 31");
        }
        if (snowflakeDataCenterId < 0 || snowflakeDataCenterId > 31) {
            throw new IllegalArgumentException(
                    "nnm.warframe.forum.snowflake-data-center-id must be between 0 and 31");
        }
    }

    public String publicArticleUrl(long articleId) {
        String base = publicBaseUrl.toString().replaceAll("/+$", "");
        String path = publicDetailPath.replaceAll("/+$", "");
        return base + path + "/" + articleId;
    }

    public String publicArticleUri(long articleId) {
        return publicDetailPath.replaceAll("/+$", "") + "/" + articleId;
    }
}
