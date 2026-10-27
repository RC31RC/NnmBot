package com.nnm.bot.core.jmcomic.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * JMComic 下载和 PDF 压缩限制。
 */
@ConfigurationProperties(prefix = "nnm.jmcomic")
public class JmComicProperties {

    /** 是否允许执行 JM 下载任务。 */
    private boolean enabled = true;
    /** 项目工作目录下的 JM 临时下载目录。 */
    private Path temporaryDirectory =
            Path.of(System.getProperty("user.dir"), "data", "jmcomic");
    /** 元数据请求超时。 */
    private Duration requestTimeout = Duration.ofSeconds(30);
    /** 单张图片下载超时。 */
    private Duration imageTimeout = Duration.ofSeconds(60);
    /** 上游请求重试次数。 */
    private int retryTimes = 2;
    /** 是否优先尝试上游整本文件下载接口。 */
    private boolean wholeFileEnabled = true;
    /** 是否允许分页下载；关闭时也禁止整本下载失败后的分页兜底。 */
    private boolean pageDownloadEnabled = true;
    /** 整本文件下载超时。 */
    private Duration wholeFileTimeout = Duration.ofMinutes(10);
    /** 可选 JM 账号，用于访问需要登录的整本下载接口。 */
    private String username;
    /** 可选 JM 密码，只允许通过外部配置注入。 */
    private String password;
    /** JMComic 内部图片下载线程数。 */
    private int downloadThreads = 4;
    /** 同一 JVM 允许同时处理的漫画任务数。 */
    private int maxConcurrentTasks = 2;
    /** 单个漫画允许的最大页数。 */
    private int maxPages = 300;
    /** 单次任务允许下载的图片总字节数。 */
    private long maxDownloadBytes = 300L * 1024 * 1024;
    /** 最终 PDF 最大字节数。 */
    private long maxPdfBytes = 100L * 1024 * 1024;
    /** 单张图片允许的最大像素数。 */
    private long maxImagePixels = 40_000_000L;
    /** PDF 中图片压缩后的最大宽度。 */
    private int maxImageWidth = 1600;
    /** JPEG 压缩质量。 */
    private float jpegQuality = 0.72F;
    /** JMComic 客户端缓存字节数。 */
    private int clientCacheBytes = 16 * 1024 * 1024;
    /** 可选 API 域名覆盖列表。 */
    private List<String> apiDomains = new ArrayList<>();
    /** 可选 HTTP 代理主机。 */
    private String proxyHost;
    /** 可选 HTTP 代理端口。 */
    private Integer proxyPort;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Path getTemporaryDirectory() {
        return temporaryDirectory;
    }

    public void setTemporaryDirectory(Path temporaryDirectory) {
        this.temporaryDirectory = temporaryDirectory;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Duration requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Duration getImageTimeout() {
        return imageTimeout;
    }

    public void setImageTimeout(Duration imageTimeout) {
        this.imageTimeout = imageTimeout;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public boolean isWholeFileEnabled() {
        return wholeFileEnabled;
    }

    public void setWholeFileEnabled(boolean wholeFileEnabled) {
        this.wholeFileEnabled = wholeFileEnabled;
    }

    public boolean isPageDownloadEnabled() {
        return pageDownloadEnabled;
    }

    public void setPageDownloadEnabled(boolean pageDownloadEnabled) {
        this.pageDownloadEnabled = pageDownloadEnabled;
    }

    public Duration getWholeFileTimeout() {
        return wholeFileTimeout;
    }

    public void setWholeFileTimeout(Duration wholeFileTimeout) {
        this.wholeFileTimeout = wholeFileTimeout;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = StrUtil.trimToNull(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = StrUtil.isEmpty(password) ? null : password;
    }

    public int getDownloadThreads() {
        return downloadThreads;
    }

    public void setDownloadThreads(int downloadThreads) {
        this.downloadThreads = downloadThreads;
    }

    public int getMaxConcurrentTasks() {
        return maxConcurrentTasks;
    }

    public void setMaxConcurrentTasks(int maxConcurrentTasks) {
        this.maxConcurrentTasks = maxConcurrentTasks;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }

    public long getMaxDownloadBytes() {
        return maxDownloadBytes;
    }

    public void setMaxDownloadBytes(long maxDownloadBytes) {
        this.maxDownloadBytes = maxDownloadBytes;
    }

    public long getMaxPdfBytes() {
        return maxPdfBytes;
    }

    public void setMaxPdfBytes(long maxPdfBytes) {
        this.maxPdfBytes = maxPdfBytes;
    }

    public long getMaxImagePixels() {
        return maxImagePixels;
    }

    public void setMaxImagePixels(long maxImagePixels) {
        this.maxImagePixels = maxImagePixels;
    }

    public int getMaxImageWidth() {
        return maxImageWidth;
    }

    public void setMaxImageWidth(int maxImageWidth) {
        this.maxImageWidth = maxImageWidth;
    }

    public float getJpegQuality() {
        return jpegQuality;
    }

    public void setJpegQuality(float jpegQuality) {
        this.jpegQuality = jpegQuality;
    }

    public int getClientCacheBytes() {
        return clientCacheBytes;
    }

    public void setClientCacheBytes(int clientCacheBytes) {
        this.clientCacheBytes = clientCacheBytes;
    }

    public List<String> getApiDomains() {
        return List.copyOf(apiDomains);
    }

    public void setApiDomains(List<String> apiDomains) {
        this.apiDomains = CollUtil.isEmpty(apiDomains)
                ? new ArrayList<>()
                : apiDomains.stream()
                        .filter(StrUtil::isNotBlank)
                        .map(String::trim)
                        .toList();
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = StrUtil.trimToNull(proxyHost);
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    @PostConstruct
    void validate() {
        if (ObjectUtil.isNull(this.temporaryDirectory)) {
            throw new IllegalStateException("nnm.jmcomic.temporary-directory must be configured");
        }
        if (ObjectUtil.isNull(this.requestTimeout) || this.requestTimeout.isNegative()
                || this.requestTimeout.isZero()) {
            throw new IllegalStateException("nnm.jmcomic.request-timeout must be positive");
        }
        if (ObjectUtil.isNull(this.imageTimeout) || this.imageTimeout.isNegative()
                || this.imageTimeout.isZero()) {
            throw new IllegalStateException("nnm.jmcomic.image-timeout must be positive");
        }
        if (ObjectUtil.isNull(this.wholeFileTimeout)
                || this.wholeFileTimeout.isNegative()
                || this.wholeFileTimeout.isZero()) {
            throw new IllegalStateException("nnm.jmcomic.whole-file-timeout must be positive");
        }
        if (this.retryTimes < 0 || this.downloadThreads <= 0 || this.maxConcurrentTasks <= 0) {
            throw new IllegalStateException("nnm.jmcomic concurrency settings are invalid");
        }
        if (this.maxPages <= 0 || this.maxDownloadBytes <= 0 || this.maxPdfBytes <= 0
                || this.maxImagePixels <= 0 || this.maxImageWidth <= 0
                || this.clientCacheBytes < 0) {
            throw new IllegalStateException("nnm.jmcomic resource limits must be positive");
        }
        if (this.jpegQuality <= 0 || this.jpegQuality > 1) {
            throw new IllegalStateException("nnm.jmcomic.jpeg-quality must be in (0, 1]");
        }
        if (StrUtil.isBlank(this.proxyHost) != ObjectUtil.isNull(this.proxyPort)) {
            throw new IllegalStateException(
                    "nnm.jmcomic proxy-host and proxy-port must be configured together");
        }
        if (ObjectUtil.isNotNull(this.proxyPort)
                && (this.proxyPort <= 0 || this.proxyPort > 65535)) {
            throw new IllegalStateException("nnm.jmcomic.proxy-port is invalid");
        }
        if (StrUtil.isBlank(this.username) != StrUtil.isBlank(this.password)) {
            throw new IllegalStateException(
                    "nnm.jmcomic username and password must be configured together");
        }
    }
}
