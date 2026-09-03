package com.nnm.bot.warframe.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "nnm.warframe.official-data")
public class WarframeOfficialDataProperties {

    private Path cacheDirectory =
            Path.of(System.getProperty("user.dir"), "data", "warframe", "public-export");

    public Path getCacheDirectory() {
        return cacheDirectory;
    }

    public void setCacheDirectory(Path cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }
}
