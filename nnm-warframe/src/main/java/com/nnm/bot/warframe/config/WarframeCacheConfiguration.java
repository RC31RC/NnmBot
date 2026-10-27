package com.nnm.bot.warframe.config;

import com.nnm.bot.warframe.constant.WarframeCacheNames;
import com.nnm.bot.warframe.config.cache.WarframeCacheProperties;
import com.nnm.bot.warframe.config.cache.WarframeCacheProperties.CacheRegion;
import org.cache2k.Cache2kBuilder;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.atomic.AtomicLong;

@Configuration(proxyBeanMethods = false)
public class WarframeCacheConfiguration {

    private static final AtomicLong CACHE_MANAGER_SEQUENCE = new AtomicLong();

    /**
     * 根据各业务缓存的容量与过期配置注册 cache2k 缓存管理器。
     * @param properties 当前业务配置。
     * @return 缓存管理器对象。
     */
    @Bean
    public CacheManager cacheManager(WarframeCacheProperties properties) {
        properties.validate();
        String managerName = "nnmBotSpringCache-" + CACHE_MANAGER_SEQUENCE.incrementAndGet();
        SpringCache2kCacheManager cacheManager = new SpringCache2kCacheManager(managerName)
                .defaultSetup(builder -> builder
                        .permitNullValues(false)
                        .disableStatistics(false))
                .addCaches(
                        builder -> region(builder, WarframeCacheNames.SUBSCRIPTION_MEMORY, properties.getSubscriptionMemory()),
                        builder -> region(builder, WarframeCacheNames.METADATA, properties.getMetadata()),
                        builder -> region(builder, WarframeCacheNames.MARKET_ORDERS, properties.getMarketOrders()),
                        builder -> region(builder, WarframeCacheNames.MARKET_AUCTIONS, properties.getMarketAuctions()),
                        builder -> region(builder, WarframeCacheNames.MARKET_STATISTICS, properties.getMarketStatistics()),
                        builder -> region(builder, WarframeCacheNames.ITEM_RESOLUTION_INDEX,
                                properties.getItemResolutionIndex()),
                        builder -> region(builder, WarframeCacheNames.IMAGE_INDEX, properties.getImageIndex()),
                        builder -> region(builder, WarframeCacheNames.RIVEN_WEEKLY, properties.getRivenWeekly()),
                        builder -> region(builder, WarframeCacheNames.WORLD_STATE_IMAGES,
                                properties.getWorldStateImages()));
        cacheManager.setAllowUnknownCache(false);
        return cacheManager;
    }

    /**
     * 将单个业务缓存区的容量和生命周期应用到缓存构建器。
     * @param builder 构建器。
     * @param name 当前业务对象的名称。
     * @param properties 当前业务配置。
     * @return 缓存键构建器对象。
     */
    private Cache2kBuilder<?, ?> region(
            Cache2kBuilder<?, ?> builder,
            String name,
            CacheRegion properties) {
        Cache2kBuilder<?, ?> configured = builder.name(name)
                .entryCapacity(properties.getCapacity());
        return properties.getExpireAfterWrite() == null
                ? configured.eternal(true)
                : configured.expireAfterWrite(properties.getExpireAfterWrite());
    }
}
