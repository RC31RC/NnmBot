package com.nnm.bot.warframe.persistent.curated.config.forum;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

public class ForumStorageProperties {

    private long maxFileBytes = 50L * 1024 * 1024;

    private String publicPath = "/api/public/v1/warframe/forum/files";

    private String temporaryPublicPath = "/api/public/v1/warframe/forum/temp-files";

    private Duration temporaryRetention = Duration.ofHours(2);

    /** 各实例分别清理自己的临时附件。 */
    private Duration cleanupInterval = Duration.ofHours(2);

    private Set<String> allowedExtensions = new LinkedHashSet<>(Set.of(
            "avif", "doc", "docx", "gif", "jpeg", "jpg", "m4v", "mov", "mp4",
            "pdf", "png", "ppt", "pptx", "txt", "webm", "webp", "xls", "xlsx", "zip"));

    /**
     * 读取最大文件字节。
     *
     * @return 当前操作计算或读取的数值。
     */
    public long getMaxFileBytes() {
        return maxFileBytes;
    }

    /**
     * 设置最大文件字节。
     *
     * @param maxFileBytes 最大文件字节。
     */
    public void setMaxFileBytes(long maxFileBytes) {
        this.maxFileBytes = maxFileBytes;
    }

    /**
     * 读取公开路径。
     *
     * @return 公开路径对应的文本。
     */
    public String getPublicPath() {
        return publicPath;
    }

    /**
     * 设置公开路径。
     *
     * @param publicPath 公开路径。
     */
    public void setPublicPath(String publicPath) {
        this.publicPath = publicPath;
    }

    /**
     * 读取临时公开路径。
     *
     * @return 临时公开路径对应的文本。
     */
    public String getTemporaryPublicPath() {
        return temporaryPublicPath;
    }

    /**
     * 设置临时公开路径。
     *
     * @param temporaryPublicPath 临时公开路径。
     */
    public void setTemporaryPublicPath(String temporaryPublicPath) {
        this.temporaryPublicPath = temporaryPublicPath;
    }

    /**
     * 读取临时保留时长。
     *
     * @return 时长对象。
     */
    public Duration getTemporaryRetention() {
        return temporaryRetention;
    }

    /**
     * 设置临时保留时长。
     *
     * @param temporaryRetention 临时保留时长。
     */
    public void setTemporaryRetention(Duration temporaryRetention) {
        this.temporaryRetention = temporaryRetention;
    }

    /**
     * 读取各实例分别清理自己的临时附件。。
     *
     * @return 各实例分别清理自己的临时附件。。
     */
    public Duration getCleanupInterval() {
        return cleanupInterval;
    }

    /**
     * 设置各实例分别清理自己的临时附件。。
     *
     * @param cleanupInterval 清理间隔。
     */
    public void setCleanupInterval(Duration cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    public void validate() {
        if (maxFileBytes < 1 || temporaryRetention == null || temporaryRetention.isNegative()
                || temporaryRetention.isZero() || cleanupInterval == null
                || cleanupInterval.isNegative() || cleanupInterval.isZero()) {
            throw new IllegalArgumentException("Invalid nnm.warframe.forum.storage configuration");
        }
    }

    /**
     * 读取允许扩展名。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public Set<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    /**
     * 设置允许扩展名。
     *
     * @param allowedExtensions 允许扩展名集合。
     */
    public void setAllowedExtensions(Set<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }
}
