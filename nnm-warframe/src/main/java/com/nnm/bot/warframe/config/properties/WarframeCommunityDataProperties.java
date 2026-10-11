package com.nnm.bot.warframe.config.properties;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.constant.WarframeCommunitySource;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 社区 DataSource CDN 配置，与官方接口和 Warframe Market 完全分离。 */
@ConfigurationProperties(prefix = "nnm.warframe.community-data")
public class WarframeCommunityDataProperties {

    private Map<String, List<DatasetSource>> datasets = new LinkedHashMap<>();
    private Duration previewRetention = Duration.ofMinutes(30);
    private Duration lockLease = Duration.ofMinutes(30);

    public Map<String, List<DatasetSource>> getDatasets() {
        return datasets;
    }

    public void setDatasets(Map<String, List<DatasetSource>> datasets) {
        this.datasets = datasets;
    }

    public Duration getPreviewRetention() {
        return previewRetention;
    }

    public void setPreviewRetention(Duration previewRetention) {
        this.previewRetention = previewRetention;
    }

    public Duration getLockLease() {
        return lockLease;
    }

    public void setLockLease(Duration lockLease) {
        this.lockLease = lockLease;
    }

    @PostConstruct
    public void validate() {
        if (previewRetention == null || previewRetention.isZero() || previewRetention.isNegative()) {
            throw new IllegalArgumentException(
                    "nnm.warframe.community-data.preview-retention must be positive");
        }
        if (lockLease == null || lockLease.isZero() || lockLease.isNegative()) {
            throw new IllegalArgumentException(
                    "nnm.warframe.community-data.lock-lease must be positive");
        }
        if (datasets == null) {
            throw new IllegalArgumentException(
                    "nnm.warframe.community-data.datasets must not be null");
        }
        datasets.forEach((dataset, sources) -> {
            WarframeCommunityDataset registeredDataset = parseDataset(dataset);
            if (sources == null || sources.isEmpty()) {
                throw new IllegalArgumentException(
                        "nnm.warframe.community-data.datasets."
                                + dataset + " must contain sources");
            }
            Set<String> sourceCodes = new HashSet<>();
            for (DatasetSource datasetSource : sources) {
                if (datasetSource == null || StrUtil.isBlank(datasetSource.getSourceCode())
                        || datasetSource.getCandidateUrls().isEmpty()
                        || datasetSource.getCandidateUrls().stream().anyMatch(
                                WarframeCommunityDataProperties::isInvalidHttpUrl)) {
                    throw new IllegalArgumentException(
                            "nnm.warframe.community-data.datasets."
                                    + dataset + " contains an invalid source or URL");
                }
                WarframeCommunitySource source = parseSource(
                        registeredDataset.code(), datasetSource.getSourceCode());
                if (!registeredDataset.code().equals(source.datasetCode())) {
                    throw new IllegalArgumentException(
                            "nnm.warframe.community-data.datasets."
                                    + dataset + " source belongs to another dataset: "
                                    + datasetSource.getSourceCode());
                }
                if (!sourceCodes.add(source.code())) {
                    throw new IllegalArgumentException(
                            "nnm.warframe.community-data.datasets."
                                    + dataset + " contains duplicate source: "
                                    + datasetSource.getSourceCode());
                }
            }
        });
    }

    private static boolean isInvalidHttpUrl(URI url) {
        return url == null || url.getHost() == null
                || (!"http".equalsIgnoreCase(url.getScheme())
                && !"https".equalsIgnoreCase(url.getScheme()));
    }

    private static WarframeCommunityDataset parseDataset(String value) {
        try {
            return WarframeCommunityDataset.parse(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "nnm.warframe.community-data references an unregistered dataset: "
                            + value, exception);
        }
    }

    private static WarframeCommunitySource parseSource(String datasetCode, String value) {
        try {
            return WarframeCommunitySource.parse(datasetCode, value);
        } catch (IllegalArgumentException exception) {
            try {
                WarframeCommunitySource source = WarframeCommunitySource.parse(value);
                throw new IllegalArgumentException(
                        "nnm.warframe.community-data source belongs to another dataset: "
                                + datasetCode + " / " + source.datasetCode(), exception);
            } catch (IllegalArgumentException sourceException) {
                if (sourceException.getMessage() != null
                        && sourceException.getMessage().contains("belongs to another dataset")) {
                    throw sourceException;
                }
            }
            throw new IllegalArgumentException(
                "nnm.warframe.community-data references an unregistered source: "
                        + datasetCode + " / " + value, exception);
        }
    }

    public static class DatasetSource {
        private String sourceCode;
        private URI url;
        private List<URI> urls = List.of();
        private String world;

        public String getSourceCode() {
            return sourceCode;
        }

        public void setSourceCode(String sourceCode) {
            this.sourceCode = sourceCode;
        }

        public URI getUrl() {
            return url;
        }

        public void setUrl(URI url) {
            this.url = url;
        }

        /**
         * 同一上游文件的候选地址，按配置顺序尝试；保留 url 兼容旧配置。
         */
        public List<URI> getUrls() {
            return urls;
        }

        public void setUrls(List<URI> urls) {
            this.urls = urls == null ? List.of() : List.copyOf(urls);
        }

        public List<URI> getCandidateUrls() {
            if (urls != null && !urls.isEmpty()) {
                return urls;
            }
            return url == null ? List.of() : List.of(url);
        }

        public String getWorld() {
            return world;
        }

        public void setWorld(String world) {
            this.world = world;
        }
    }
}
