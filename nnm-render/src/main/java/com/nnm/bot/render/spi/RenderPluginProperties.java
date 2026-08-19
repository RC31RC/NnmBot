package com.nnm.bot.render.spi;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "render.plugins")
public class RenderPluginProperties {

    private boolean enabled = true;
    /** 外部 JAR 会在当前进程内执行代码，因此必须显式开启发现。 */
    private boolean externalEnabled;
    private Path directory = Path.of("./plugin");
    private String provider;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isExternalEnabled() {
        return externalEnabled;
    }

    public void setExternalEnabled(boolean externalEnabled) {
        this.externalEnabled = externalEnabled;
    }

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    void validate() {
        if (directory == null) {
            throw new IllegalArgumentException("render.plugins.directory must not be null");
        }
    }
}
