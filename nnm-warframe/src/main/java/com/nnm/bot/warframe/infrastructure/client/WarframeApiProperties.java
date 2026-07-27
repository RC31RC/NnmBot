package com.nnm.bot.warframe.infrastructure.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "nnm.warframe.api")
public class WarframeApiProperties {

    private URI worldStateUrl = URI.create("https://api.warframestat.us/pc");
    private URI marketBaseUrl = URI.create("https://api.warframe.market/v1");
    private String language = "zh";
    private String platform = "pc";

    public URI getWorldStateUrl() {
        return worldStateUrl;
    }

    public void setWorldStateUrl(URI worldStateUrl) {
        this.worldStateUrl = worldStateUrl;
    }

    public URI getMarketBaseUrl() {
        return marketBaseUrl;
    }

    public void setMarketBaseUrl(URI marketBaseUrl) {
        this.marketBaseUrl = marketBaseUrl;
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

    void validate() {
        requireHttpUrl(worldStateUrl, "nnm.warframe.api.world-state-url");
        requireHttpUrl(marketBaseUrl, "nnm.warframe.api.market-base-url");
        requireText(language, "nnm.warframe.api.language");
        requireText(platform, "nnm.warframe.api.platform");
    }

    private void requireHttpUrl(URI value, String name) {
        if (value == null
                || value.getHost() == null
                || (!"http".equalsIgnoreCase(value.getScheme())
                && !"https".equalsIgnoreCase(value.getScheme()))) {
            throw new IllegalArgumentException(name + " must be an absolute HTTP URL");
        }
    }

    private void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
