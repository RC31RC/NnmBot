package com.nnm.bot.warframe.persistent.official.config;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.SourceMode;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;

/** 固定版本整包导入预算；网络超时与重试统一使用 nnm.http。 */
@Getter
@Setter
@ConfigurationProperties("nnm.warframe.pull.dataset")
public class WarframeOfficialImportProperties {

    private SourceMode sourceMode = SourceMode.LOCAL;
    private Path localDataDirectory = Path.of(System.getProperty("user.dir"), "..",
            "warframe-datasource-nnm", "data", "latest").normalize();
    private String localRef = "local";
    private String localCommit = "";
    private Duration localVersionTimeout = Duration.ofSeconds(5);
    private URI apiBaseUrl = URI.create("https://api.github.com");
    private URI rawBaseUrl = URI.create("https://raw.githubusercontent.com");
    private String repository = "RC31RC/warframe-datasource-nnm";
    private String tagPrefix = "data-";
    private Path stagingDirectory = Path.of(System.getProperty("user.dir"), "data", "warframe", "dataset-staging");
    private int maxFiles = 128;
    private int maxTagPages = 10;
    private long maxManifestBytes = 1024 * 1024;
    private long maxFileBytes = 128 * 1024 * 1024L;
    private long maxSnapshotBytes = 768 * 1024 * 1024L;
    private Duration previewTtl = Duration.ofHours(6);
    private int maxPendingPreviews = 3;
    private int retainedSnapshots = 2;
    private Duration staleStagingTtl = Duration.ofDays(1);

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        if (sourceMode == null || localDataDirectory == null || StrUtil.isBlank(localDataDirectory.toString())
                || StrUtil.isBlank(localRef) || localCommit == null
                || (StrUtil.isNotBlank(localCommit) && !localCommit.matches("[a-fA-F0-9]{40}"))
                || localVersionTimeout == null || localVersionTimeout.compareTo(Duration.ofMillis(1)) < 0
                || localVersionTimeout.compareTo(Duration.ofMinutes(1)) > 0
                || (sourceMode == SourceMode.REMOTE && (!validHttp(apiBaseUrl) || !validHttp(rawBaseUrl)))
                || repository == null || !repository.matches("[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+")
                || StrUtil.isBlank(tagPrefix)
                || stagingDirectory == null || StrUtil.isBlank(stagingDirectory.toString())
                || maxFiles < 1 || maxFiles > 1024 || maxTagPages < 1 || maxTagPages > 100
                || maxManifestBytes < 1 || maxManifestBytes > 16 * 1024 * 1024L
                || maxFileBytes < 1 || maxSnapshotBytes < maxFileBytes
                || maxPendingPreviews < 1 || maxPendingPreviews > 20
                || retainedSnapshots < 1 || retainedSnapshots > 10
                || staleStagingTtl == null || staleStagingTtl.compareTo(Duration.ofHours(1)) < 0
                || previewTtl == null || previewTtl.isNegative() || previewTtl.isZero()) {
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.dataset configuration");
        }
    }

    /**
     * 判断是否满足有效HTTP条件。
     *
     * @param uri 地址。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean validHttp(URI uri) {
        return uri != null && uri.getHost() != null && uri.getUserInfo() == null
                && uri.getQuery() == null && uri.getFragment() == null
                && ("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()));
    }

}
