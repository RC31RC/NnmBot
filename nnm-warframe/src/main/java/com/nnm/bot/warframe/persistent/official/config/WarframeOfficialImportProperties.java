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
import java.util.List;

/** 固定 tag 的逐文件导入配置；大小仅限制压缩原件，网络超时与重试统一使用 nnm.http。 */
@Getter
@Setter
@ConfigurationProperties("nnm.warframe.pull.dataset")
public class WarframeOfficialImportProperties {

    private SourceMode sourceMode = SourceMode.REMOTE;
    private Path localDataDirectory =
        Path.of(System.getProperty("user.dir"), "..", "warframe-datasource-nnm", "data", "latest").normalize();
    private String localRef = "local";
    private String localCommit = "";
    private Duration localVersionTimeout = Duration.ofSeconds(5);
    private URI apiBaseUrl = URI.create("https://api.github.com");
    private List<URI> cdnBaseUrls = List.of(URI.create("https://cdn.jsdelivr.net"),
        URI.create("https://testingcf.jsdelivr.net"), URI.create("https://jsd.onmicrosoft.cn"));
    private String githubToken = "";
    private String repository = "RC31RC/warframe-datasource-nnm";
    private Path stagingDirectory = Path.of(System.getProperty("user.dir"), "data", "warframe", "dataset-staging");
    private int maxFiles = 128;
    private int maxTagPages = 10;
    private long maxVersionResponseBytes = 1024 * 1024;
    private long maxFileBytes = 20_000_000L;
    private Duration previewTtl = Duration.ofMinutes(30);
    private Duration activeTaskRetention = Duration.ofHours(3);
    private int maxPendingPreviews = 128;
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
            || (sourceMode == SourceMode.REMOTE && (!validHttp(apiBaseUrl) || cdnBaseUrls == null
                || cdnBaseUrls.isEmpty() || cdnBaseUrls.stream().anyMatch(uri -> !validHttps(uri))))
            || githubToken == null || !githubToken.equals(githubToken.trim()) || githubToken.contains("\n")
            || githubToken.contains("\r") || repository == null
            || !repository.matches("[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+") || stagingDirectory == null
            || StrUtil.isBlank(stagingDirectory.toString()) || maxFiles < 1 || maxFiles > 1024 || maxTagPages < 1
            || maxTagPages > 100 || maxVersionResponseBytes < 1 || maxVersionResponseBytes > 16 * 1024 * 1024L
            || maxFileBytes < 1 || maxFileBytes > 20_000_000L || maxPendingPreviews < 1 || maxPendingPreviews > maxFiles
            || retainedSnapshots < 1 || retainedSnapshots > 10 || staleStagingTtl == null
            || staleStagingTtl.compareTo(Duration.ofHours(1)) < 0 || previewTtl == null || previewTtl.isNegative()
            || previewTtl.isZero() || activeTaskRetention == null || activeTaskRetention.isNegative()
            || activeTaskRetention.isZero()) {
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.dataset configuration");
        }
    }

    /**
     * 判断是否满足有效HTTP条件。
     *
     * @param uri 地址。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean validHttps(URI uri) {
        return uri != null && uri.getHost() != null && uri.getUserInfo() == null && uri.getQuery() == null
            && uri.getFragment() == null && "https".equalsIgnoreCase(uri.getScheme());
    }

    private static boolean validHttp(URI uri) {
        return uri != null && uri.getHost() != null && uri.getUserInfo() == null && uri.getQuery() == null
            && uri.getFragment() == null
            && ("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()));
    }

}
