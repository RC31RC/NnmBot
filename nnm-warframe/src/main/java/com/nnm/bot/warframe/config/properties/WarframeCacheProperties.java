package com.nnm.bot.warframe.config.properties;

import cn.hutool.core.util.ObjectUtil;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "nnm.warframe.cache")
public class WarframeCacheProperties {

    private final CacheRegion worldState = new CacheRegion(1, null);
    private final CacheRegion metadata = new CacheRegion(64, Duration.ofHours(6));
    private final CacheRegion translations = new CacheRegion(16, Duration.ofHours(12));
    private final CacheRegion dataSourceTag = new CacheRegion(8, Duration.ofMinutes(2));
    private final CacheRegion worldStateImages = new CacheRegion(32, Duration.ofHours(26));

    public CacheRegion getWorldState() {
        return worldState;
    }

    public CacheRegion getMetadata() {
        return metadata;
    }

    public CacheRegion getTranslations() {
        return translations;
    }

    public CacheRegion getDataSourceTag() {
        return dataSourceTag;
    }

    public CacheRegion getWorldStateImages() {
        return worldStateImages;
    }

    public void validate() {
        validate(worldState, "nnm.warframe.cache.world-state", false);
        validate(metadata, "nnm.warframe.cache.metadata", true);
        validate(translations, "nnm.warframe.cache.translations", true);
        validate(dataSourceTag, "nnm.warframe.cache.data-source-tag", true);
        validate(worldStateImages, "nnm.warframe.cache.world-state-images", true);
    }

    private void validate(CacheRegion region, String name, boolean expiryRequired) {
        if (region.capacity < 1) {
            throw new IllegalArgumentException(name + ".capacity must be at least 1");
        }
        if (expiryRequired
                && (ObjectUtil.isNull(region.expireAfterWrite)
                || region.expireAfterWrite.isZero()
                || region.expireAfterWrite.isNegative())) {
            throw new IllegalArgumentException(name + ".expire-after-write must be positive");
        }
    }

    public static class CacheRegion {

        private int capacity;
        private Duration expireAfterWrite;

        CacheRegion(int capacity, Duration expireAfterWrite) {
            this.capacity = capacity;
            this.expireAfterWrite = expireAfterWrite;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public Duration getExpireAfterWrite() {
            return expireAfterWrite;
        }

        public void setExpireAfterWrite(Duration expireAfterWrite) {
            this.expireAfterWrite = expireAfterWrite;
        }
    }
}
