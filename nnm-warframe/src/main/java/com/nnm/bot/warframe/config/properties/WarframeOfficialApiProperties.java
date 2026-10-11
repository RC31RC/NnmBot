package com.nnm.bot.warframe.config.properties;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

/**
 * Warframe 官方接口配置。
 *
 * <p>这里仅包含官方 WorldState 和 PublicExport，不承载社区 CDN 或
 * Warframe Market 的地址，避免不同来源在调用层被误认为同一套 API。</p>
 */
@ConfigurationProperties(prefix = "nnm.warframe.official-api")
public class WarframeOfficialApiProperties {

    private URI worldStateUrl = URI.create("https://api.warframe.com/cdn/worldState.php");
    private String publicExportIndexUrl =
            "https://origin.warframe.com/PublicExport/index_{language}.txt.lzma";
    private String publicExportManifestUrl =
            "https://content.warframe.com/PublicExport/Manifest/{file}";
    private String language = "zh";
    private String englishLanguage = "en";

    public URI getWorldStateUrl() {
        return worldStateUrl;
    }

    public void setWorldStateUrl(URI worldStateUrl) {
        this.worldStateUrl = worldStateUrl;
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

    public void validate() {
        requireHttpUrl(worldStateUrl, "nnm.warframe.official-api.world-state-url");
        requireHttpUrl(publicExportIndexUrl,
                "nnm.warframe.official-api.public-export-index-url");
        requireHttpUrl(publicExportManifestUrl,
                "nnm.warframe.official-api.public-export-manifest-url");
        requireText(language, "nnm.warframe.official-api.language");
        requireText(englishLanguage, "nnm.warframe.official-api.english-language");
    }

    private static void requireHttpUrl(URI value, String name) {
        if (value == null || value.getHost() == null
                || (!"http".equalsIgnoreCase(value.getScheme())
                && !"https".equalsIgnoreCase(value.getScheme()))) {
            throw new IllegalArgumentException(name + " must be an absolute HTTP URL");
        }
    }

    private static void requireHttpUrl(String value, String name) {
        try {
            String template = value
                    .replace("{language}", "en")
                    .replace("{file}", "ExportWarframes_en.json!hash");
            requireHttpUrl(URI.create(template), name);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(name + " must be an absolute HTTP URL", exception);
        }
    }

    private static void requireText(String value, String name) {
        if (StrUtil.isBlank(value)) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
