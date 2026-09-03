package com.nnm.bot.warframe.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

@ConfigurationProperties(prefix = "nnm.warframe.forum-storage")
public class WarframeForumStorageProperties {

    private Path directory =
            Path.of(System.getProperty("user.dir"), "data", "warframe-forum");

    private Path temporaryDirectory =
            Path.of(System.getProperty("user.dir"), "data", "warframe-forum-temp");

    private long maxFileBytes = 50L * 1024 * 1024;

    private String publicPath = "/api/public/v1/warframe/forum/files";

    private String temporaryPublicPath = "/api/public/v1/warframe/forum/temp-files";

    private Duration temporaryRetention = Duration.ofHours(2);

    private Set<String> allowedExtensions = new LinkedHashSet<>(Set.of(
            "avif", "doc", "docx", "gif", "jpeg", "jpg", "m4v", "mov", "mp4",
            "pdf", "png", "ppt", "pptx", "txt", "webm", "webp", "xls", "xlsx", "zip"));

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }

    public Path getTemporaryDirectory() {
        return temporaryDirectory;
    }

    public void setTemporaryDirectory(Path temporaryDirectory) {
        this.temporaryDirectory = temporaryDirectory;
    }

    public long getMaxFileBytes() {
        return maxFileBytes;
    }

    public void setMaxFileBytes(long maxFileBytes) {
        this.maxFileBytes = maxFileBytes;
    }

    public String getPublicPath() {
        return publicPath;
    }

    public void setPublicPath(String publicPath) {
        this.publicPath = publicPath;
    }

    public String getTemporaryPublicPath() {
        return temporaryPublicPath;
    }

    public void setTemporaryPublicPath(String temporaryPublicPath) {
        this.temporaryPublicPath = temporaryPublicPath;
    }

    public Duration getTemporaryRetention() {
        return temporaryRetention;
    }

    public void setTemporaryRetention(Duration temporaryRetention) {
        this.temporaryRetention = temporaryRetention;
    }

    public Set<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(Set<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }
}
