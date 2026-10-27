package com.nnm.bot.warframe.config.cache;

import org.springframework.cache.Cache.ValueRetrievalException;
import jakarta.annotation.Resource;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/** 通过 cache2k 协调已登记业务缓存的按键惰性加载。 */
@Component
public class WarframeCacheReader {

    @Resource
    private CacheManager cacheManager;

    /**
     * 读取指定字段或键对应的数据。
     *
     * @param regionName 星区名称。
     * @param key 当前记录或字段的查找键。
     * @param loader 加载器。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    public <T> Optional<T> get(String regionName, Object key, Supplier<Optional<T>> loader) {
        if (!cacheManager.getCacheNames().contains(regionName)) {
            throw new IllegalArgumentException("Unregistered Warframe cache region: " + regionName);
        }
        Cache cache = Objects.requireNonNull(cacheManager.getCache(regionName), regionName);
        try {
            return Optional.ofNullable(cache.get(key,
                    () -> loader.get().orElseThrow(UnavailableResult::new)));
        } catch (ValueRetrievalException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof UnavailableResult) {
                return Optional.empty();
            }
            if (cause instanceof RuntimeException runtimeFailure) {
                throw runtimeFailure;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw exception;
        }
    }

    private static final class UnavailableResult extends RuntimeException {
        /**
         * 禁止实例化，仅通过本类的静态方法使用此能力。
         */
        private UnavailableResult() {
            super(null, null, false, false);
        }
    }
}
