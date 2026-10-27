package com.nnm.bot.warframe.config.cache;

import cn.hutool.core.util.ObjectUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

@ConfigurationProperties(prefix = "nnm.warframe.cache")
public class WarframeCacheProperties {

    private final CacheRegion subscriptionMemory = new CacheRegion(128, Duration.ofMinutes(2));
    private final CacheRegion metadata = new CacheRegion(64, Duration.ofHours(6));
    private final CacheRegion worldStateImages = new CacheRegion(32, Duration.ofHours(26));
    private final CacheRegion marketOrders = new CacheRegion(256, Duration.ofSeconds(60));
    private final CacheRegion marketAuctions = new CacheRegion(128, Duration.ofSeconds(60));
    private final CacheRegion marketStatistics = new CacheRegion(64, Duration.ofHours(1));
    private final CacheRegion itemResolutionIndex = new CacheRegion(1, Duration.ofHours(8));
    private final CacheRegion imageIndex = new CacheRegion(2, null);
    private final CacheRegion rivenWeekly = new CacheRegion(4, null);

    /**
     * 读取订阅记忆。
     *
     * @return 缓存星区对象。
     */
    public CacheRegion getSubscriptionMemory() { return subscriptionMemory; }
    /**
     * 读取图片索引。
     *
     * @return 缓存星区对象。
     */
    public CacheRegion getImageIndex() { return imageIndex; }
    /**
     * 读取紫卡每周。
     *
     * @return 缓存星区对象。
     */
    public CacheRegion getRivenWeekly() { return rivenWeekly; }

    /**
     * 读取物品解析状态索引。
     *
     * @return 缓存星区对象。
     */
    public CacheRegion getItemResolutionIndex() { return itemResolutionIndex; }

    /**
     * 读取市场订单。
     *
     * @return 缓存星区对象。
     */
    public CacheRegion getMarketOrders() { return marketOrders; }
    /**
     * 读取市场拍卖。
     *
     * @return 缓存星区对象。
     */
    public CacheRegion getMarketAuctions() { return marketAuctions; }
    /**
     * 读取市场统计。
     *
     * @return 缓存星区对象。
     */
    public CacheRegion getMarketStatistics() { return marketStatistics; }

    /**
     * 读取元数据。
     *
     * @return 缓存星区对象。
     */
    public CacheRegion getMetadata() {
        return metadata;
    }

    /**
     * 读取世界状态图片。
     *
     * @return 缓存星区对象。
     */
    public CacheRegion getWorldStateImages() {
        return worldStateImages;
    }

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        validate(subscriptionMemory, "nnm.warframe.cache.subscription-memory", true);
        validate(metadata, "nnm.warframe.cache.metadata", true);
        validate(worldStateImages, "nnm.warframe.cache.world-state-images", true);
        validate(marketOrders, "nnm.warframe.cache.market-orders", true);
        validate(marketAuctions, "nnm.warframe.cache.market-auctions", true);
        validate(marketStatistics, "nnm.warframe.cache.market-statistics", true);
        validate(itemResolutionIndex, "nnm.warframe.cache.item-resolution-index", true);
        validate(imageIndex, "nnm.warframe.cache.image-index", false);
        if (imageIndex.capacity < 2) {
            throw new IllegalArgumentException("nnm.warframe.cache.image-index.capacity must be at least 2");
        }
        validate(rivenWeekly, "nnm.warframe.cache.riven-weekly", false);
        if (rivenWeekly.capacity < 4) {
            throw new IllegalArgumentException("nnm.warframe.cache.riven-weekly.capacity must be at least 4");
        }
    }

    /**
     * 校验当前配置或输入是否满足业务约束。
     *
     * @param region 星区。
     * @param name 当前业务对象的名称。
     * @param expiryRequired 到期必要。
     */
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

        /**
         * 构造缓存星区，并保存初始化参数。
         *
         * @param capacity 容量。
         * @param expireAfterWrite 过期后续写入。
         */
        CacheRegion(int capacity, Duration expireAfterWrite) {
            this.capacity = capacity;
            this.expireAfterWrite = expireAfterWrite;
        }

        /**
         * 读取容量。
         *
         * @return 当前操作计算或读取的数值。
         */
        public int getCapacity() {
            return capacity;
        }

        /**
         * 设置容量。
         *
         * @param capacity 容量。
         */
        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        /**
         * 读取过期后续写入。
         *
         * @return 时长对象。
         */
        public Duration getExpireAfterWrite() {
            return expireAfterWrite;
        }

        /**
         * 设置过期后续写入。
         *
         * @param expireAfterWrite 过期后续写入。
         */
        public void setExpireAfterWrite(Duration expireAfterWrite) {
            this.expireAfterWrite = expireAfterWrite;
        }
    }
}
