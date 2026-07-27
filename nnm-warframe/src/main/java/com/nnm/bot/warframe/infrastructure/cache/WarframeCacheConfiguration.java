package com.nnm.bot.warframe.infrastructure.cache;

import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicLong;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WarframeCacheProperties.class)
public class WarframeCacheConfiguration {

    private static final AtomicLong CACHE_MANAGER_SEQUENCE = new AtomicLong();

    @Bean
    public CacheManager cacheManager(WarframeCacheProperties properties) {
        properties.validate();
        String managerName = "nnmBotSpringCache-" + CACHE_MANAGER_SEQUENCE.incrementAndGet();
        SpringCache2kCacheManager cacheManager = new SpringCache2kCacheManager(managerName)
                .defaultSetup(builder -> builder
                        .permitNullValues(false)
                        .disableStatistics(false))
                .addCaches(
                        builder -> builder
                                .name(WarframeCacheNames.WORLD_STATE)
                                .entryCapacity(properties.getWorldState().getCapacity())
                                .eternal(true),
                        builder -> builder
                                .name(WarframeCacheNames.METADATA)
                                .entryCapacity(properties.getMetadata().getCapacity())
                                .expireAfterWrite(properties.getMetadata().getExpireAfterWrite()),
                        builder -> builder
                                .name(WarframeCacheNames.TRANSLATIONS)
                                .entryCapacity(properties.getTranslations().getCapacity())
                                .expireAfterWrite(properties.getTranslations().getExpireAfterWrite()),
                        builder -> builder
                                .name(WarframeCacheNames.DATA_SOURCE_TAG)
                                .entryCapacity(properties.getDataSourceTag().getCapacity())
                                .expireAfterWrite(properties.getDataSourceTag().getExpireAfterWrite())
                );
        cacheManager.setAllowUnknownCache(false);
        return cacheManager;
    }
}
