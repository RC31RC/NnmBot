package com.nnm.bot.warframe.infrastructure.cache;

import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WarframeCacheConfigurationTest {

    @Test
    void configuresCache2kWithOnlyKnownWarframeCaches() {
        try (AnnotationConfigApplicationContext context = createContext();
             AnnotationConfigApplicationContext secondContext = createContext()) {
            CacheManager cacheManager = context.getBean(CacheManager.class);

            assertThat(cacheManager).isInstanceOf(SpringCache2kCacheManager.class);
            assertThat(secondContext.getBean(CacheManager.class))
                    .isInstanceOf(SpringCache2kCacheManager.class)
                    .isNotSameAs(cacheManager);
            assertThat(cacheManager.getCacheNames()).containsExactlyInAnyOrder(
                    WarframeCacheNames.WORLD_STATE,
                    WarframeCacheNames.METADATA,
                    WarframeCacheNames.TRANSLATIONS,
                    WarframeCacheNames.DATA_SOURCE_TAG
            );
            assertThat(cacheManager.getCache("unknown")).isNull();

            Cache worldStateCache = cacheManager.getCache(WarframeCacheNames.WORLD_STATE);
            assertThat(worldStateCache).isNotNull();
            worldStateCache.put("current", "world-state");
            assertThat(worldStateCache.get("current", String.class)).isEqualTo("world-state");
        }
    }

    @Test
    void rejectsInvalidCacheCapacity() {
        WarframeCacheProperties properties = new WarframeCacheProperties();
        properties.getMetadata().setCapacity(0);

        assertThatThrownBy(() -> new WarframeCacheConfiguration().cacheManager(properties))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("metadata.capacity");
    }

    private AnnotationConfigApplicationContext createContext() {
        return new AnnotationConfigApplicationContext(WarframeCacheConfiguration.class);
    }
}
