package com.nnm.bot.warframe.config.properties;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "nnm.warframe.api")
public class WarframeApiProperties {

    private URI worldStateUrl = URI.create("https://api.warframe.com/cdn/worldState.php");
    private URI marketBaseUrl = URI.create("https://api.warframe.market/v1");
    private String publicExportIndexUrl =
            "https://origin.warframe.com/PublicExport/index_{language}.txt.lzma";
    private String publicExportManifestUrl =
            "https://content.warframe.com/PublicExport/Manifest/{file}";
    private Map<String, List<URI>> communityDatasetUrls = defaultCommunityDatasetUrls();
    private String language = "zh";
    private String englishLanguage = "en";
    private String platform = "pc";
    private String marketApiKey = "";
    private String marketUserAgent = "NnmBot/1.0";

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

    public String getPublicExportIndexUrl() {
        return publicExportIndexUrl;
    }

    public void setPublicExportIndexUrl(String publicExportIndexUrl) {
        this.publicExportIndexUrl = publicExportIndexUrl;
    }

    public String getPublicExportManifestUrl() {
        return publicExportManifestUrl;
    }

    public void setPublicExportManifestUrl(String publicExportManifestUrl) {
        this.publicExportManifestUrl = publicExportManifestUrl;
    }

    public Map<String, List<URI>> getCommunityDatasetUrls() {
        return communityDatasetUrls;
    }

    public void setCommunityDatasetUrls(Map<String, List<URI>> communityDatasetUrls) {
        this.communityDatasetUrls = communityDatasetUrls;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEnglishLanguage() {
        return englishLanguage;
    }

    public void setEnglishLanguage(String englishLanguage) {
        this.englishLanguage = englishLanguage;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getMarketApiKey() {
        return marketApiKey;
    }

    public void setMarketApiKey(String marketApiKey) {
        this.marketApiKey = marketApiKey;
    }

    public String getMarketUserAgent() {
        return marketUserAgent;
    }

    public void setMarketUserAgent(String marketUserAgent) {
        this.marketUserAgent = marketUserAgent;
    }

    public void validate() {
        requireHttpUrl(worldStateUrl, "nnm.warframe.api.world-state-url");
        requireHttpUrl(marketBaseUrl, "nnm.warframe.api.market-base-url");
        requireHttpUrl(publicExportIndexUrl, "nnm.warframe.api.public-export-index-url");
        requireHttpUrl(publicExportManifestUrl, "nnm.warframe.api.public-export-manifest-url");
        if (communityDatasetUrls == null || communityDatasetUrls.isEmpty()) {
            throw new IllegalArgumentException(
                    "nnm.warframe.api.community-dataset-urls must not be empty");
        }
        communityDatasetUrls.forEach((dataset, urls) ->
                requireHttpUrls(urls,
                        "nnm.warframe.api.community-dataset-urls." + dataset));
        requireText(language, "nnm.warframe.api.language");
        requireText(englishLanguage, "nnm.warframe.api.english-language");
        requireText(platform, "nnm.warframe.api.platform");
        requireText(marketUserAgent, "nnm.warframe.api.market-user-agent");
    }

    private void requireHttpUrl(URI value, String name) {
        if (ObjectUtil.isNull(value)
                || ObjectUtil.isNull(value.getHost())
                || (!"http".equalsIgnoreCase(value.getScheme())
                && !"https".equalsIgnoreCase(value.getScheme()))) {
            throw new IllegalArgumentException(name + " must be an absolute HTTP URL");
        }
    }

    private void requireHttpUrl(String value, String name) {
        try {
            String template = value
                    .replace("{language}", "en")
                    .replace("{file}", "ExportWarframes_en.json!hash");
            requireHttpUrl(URI.create(template), name);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(name + " must be an absolute HTTP URL", exception);
        }
    }

    private void requireText(String value, String name) {
        if (StrUtil.isBlank(value)) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }

    private void requireHttpUrls(List<URI> values, String name) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        for (URI value : values) {
            requireHttpUrl(value, name);
        }
    }

    private static Map<String, List<URI>> defaultCommunityDatasetUrls() {
        Map<String, List<URI>> urls = new LinkedHashMap<>();
        String[] datasets = {
                "translation", "alias", "nodes", "reward_pool", "state_translation",
                "market_element", "market_riven_tion", "market_riven_tion_alias",
                "riven_analyse_trend", "riven_trend"
        };
        for (String dataset : datasets) {
            urls.put(dataset, defaultCommunityUrls(dataset));
        }
        return urls;
    }

    private static List<URI> defaultCommunityUrls(String dataset) {
        return List.of(
                URI.create("https://testingcf.jsdelivr.net/gh/KingPrimes/DataSource@latest/warframe/"
                        + dataset + ".json"),
                URI.create("https://jsd.onmicrosoft.cn/gh/KingPrimes/DataSource@latest/warframe/"
                        + dataset + ".json"),
                URI.create("https://cdn.jsdelivr.net/gh/KingPrimes/DataSource@latest/warframe/"
                        + dataset + ".json"));
    }
}
