package com.nnm.bot.warframe.config.properties;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

/** Warframe Market v1/v2 接口配置。 */
@ConfigurationProperties(prefix = "nnm.warframe.market-api")
public class WarframeMarketApiProperties {

    /** v2 是当前物品目录和订单的主协议，v1 仅保留给仍未迁移的接口。 */
    private String version = "v2";
    private URI v1BaseUrl = URI.create("https://api.warframe.market/v1");
    private URI v2BaseUrl = URI.create("https://api.warframe.market/v2");
    private String language = "zh-hans";
    private String platform = "pc";
    private String apiKey = "";
    private String userAgent = "NnmBot/1.0";
    /** WM 普通订单每页条数；拍卖查询保持独立的分页规则。 */
    private int orderPageSize = 5;

    public int getOrderPageSize() {
        return orderPageSize;
    }

    public void setOrderPageSize(int orderPageSize) {
        this.orderPageSize = orderPageSize;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public URI getV1BaseUrl() {
        return v1BaseUrl;
    }

    public void setV1BaseUrl(URI v1BaseUrl) {
        this.v1BaseUrl = v1BaseUrl;
    }

    public URI getV2BaseUrl() {
        return v2BaseUrl;
    }

    public void setV2BaseUrl(URI v2BaseUrl) {
        this.v2BaseUrl = v2BaseUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void validate() {
        if (orderPageSize < 1 || orderPageSize > 10) {
            throw new IllegalArgumentException("nnm.warframe.market-api.order-page-size must be between 1 and 10");
        }
        requireHttpUrl(v1BaseUrl, "nnm.warframe.market-api.v1-base-url");
        requireHttpUrl(v2BaseUrl, "nnm.warframe.market-api.v2-base-url");
        if (!"v1".equalsIgnoreCase(version) && !"v2".equalsIgnoreCase(version)) {
            throw new IllegalArgumentException(
                    "nnm.warframe.market-api.version must be v1 or v2");
        }
        requireText(language, "nnm.warframe.market-api.language");
        requireText(platform, "nnm.warframe.market-api.platform");
        requireText(userAgent, "nnm.warframe.market-api.user-agent");
    }

    private static void requireHttpUrl(URI value, String name) {
        if (value == null || value.getHost() == null
                || (!"http".equalsIgnoreCase(value.getScheme())
                && !"https".equalsIgnoreCase(value.getScheme()))) {
            throw new IllegalArgumentException(name + " must be an absolute HTTP URL");
        }
    }

    private static void requireText(String value, String name) {
        if (StrUtil.isBlank(value)) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
