package com.nnm.bot.warframe.runtime.config.riven;

import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.net.URI;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.pull.riven-weekly")
public class WarframeRivenWeeklyProperties {
    private Map<String, Source> sources = defaultSources();
    private Duration refreshInterval = Duration.ofHours(4);
    private Duration retryInterval = Duration.ofMinutes(10);
    private Duration refreshCheckInterval = Duration.ofMinutes(15);
    private Duration initialDelay = Duration.ofMinutes(2);
    private boolean enabled = true;
    private int maxRecords = 20_000;

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (sources == null || !sources.keySet().equals(Set.of("pc", "ps4", "xb1", "swi"))
                || invalid(refreshInterval) || invalid(retryInterval) || invalid(refreshCheckInterval)
                || invalid(initialDelay) || maxRecords < 1 || maxRecords > 100_000) {
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.riven-weekly configuration");
        }
        sources.forEach((platform, source) -> {
            String suffix = platform.toUpperCase(Locale.ROOT);
            if (source == null || !("WF_RUNTIME_RIVEN_WEEKLY_OFFICIAL_" + suffix).equals(source.sourceCode)
                    || !("repos/weeklyRivens" + suffix + ".json").equals(source.path)
                    || !"Digital Extremes".equals(source.project) || !"GET".equals(source.method)
                    || !"nnm.http".equals(source.httpPolicy) || !"RUNTIME_SNAPSHOT".equals(source.cachePolicy)
                    || !"SHA-256".equals(source.digest) || CollUtil.isEmpty(source.urls)) {
                throw new IllegalArgumentException("Invalid official weekly Riven source: " + platform);
            }
            for (URI url : source.urls) {
                if (url == null || !List.of("https", "http").contains(url.getScheme())
                        || url.getHost() == null || url.getUserInfo() != null || url.getFragment() != null) {
                    throw new IllegalArgumentException("Weekly Riven source requires an absolute HTTP URL");
                }
            }
        });
    }

    /**
     * Map 条目由 Binder 单独创建；只覆盖一个平台的地址时仍继承其登记身份和其他平台。
     *
     * @param sources 来源。
     */
    public void setSources(Map<String, Source> sources) {
        if (sources == null) {
            this.sources = null;
            return;
        }
        Map<String, Source> merged = defaultSources();
        sources.forEach((platform, source) -> {
            Source defaults = merged.get(platform);
            if (source != null && defaults != null) {
                if (source.sourceCode == null) {
                    source.sourceCode = defaults.sourceCode;
                }
                if (source.project == null) {
                    source.project = defaults.project;
                }
                if (source.path == null) {
                    source.path = defaults.path;
                }
                if (source.urls == null) {
                    source.urls = defaults.urls;
                }
            }
            merged.put(platform, source);
        });
        this.sources = merged;
    }

    /**
     * 创建各平台周紫卡接口的默认来源配置。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private static Map<String, Source> defaultSources() {
        Map<String, Source> sources = new LinkedHashMap<>();
        for (String platform : List.of("pc", "ps4", "xb1", "swi")) {
            String suffix = platform.toUpperCase(Locale.ROOT);
            Source source = new Source();
            source.sourceCode = "WF_RUNTIME_RIVEN_WEEKLY_OFFICIAL_" + suffix;
            source.project = "Digital Extremes";
            source.path = "repos/weeklyRivens" + suffix + ".json";
            source.urls = List.of(URI.create("https://www-static.warframe.com/" + source.path));
            sources.put(platform, source);
        }
        return sources;
    }

    /**
     * 构造包含明确输入位置的解析失败异常。
     *
     * @param value 待处理的字段值。
     * @return 满足构造包含明确输入位置的解析失败异常时为真，否则为假。
     */
    private boolean invalid(Duration value) {
        return value == null || value.isNegative() || value.isZero();
    }

    @Getter
    @Setter
    public static class Source {
        private String sourceCode;
        private String project;
        private String path;
        private String method = "GET";
        private String httpPolicy = "nnm.http";
        private String cachePolicy = "RUNTIME_SNAPSHOT";
        private String digest = "SHA-256";
        private List<URI> urls;
    }
}
