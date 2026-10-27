package com.nnm.bot.core.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/** 机器人本地缓存与共享观察数据的容量、有效期统一配置。 */
@ConfigurationProperties("nnm.cache.bot")
public record BotCacheProperties(
        @DefaultValue("10m")
        Duration replayRetention,
        @DefaultValue("16384")
        int replayCapacity,
        @DefaultValue("24h")
        Duration cooldownRetention,
        @DefaultValue("8192")
        int cooldownCapacity,
        @DefaultValue("60s")
        Duration topologyRefresh,
        @DefaultValue("2s")
        Duration statusRefresh,
        @DefaultValue("25s")
        Duration statusHeartbeat,
        @DefaultValue("30d")
        Duration observationRetention,
        @DefaultValue("30s")
        Duration observationTouchInterval,
        @DefaultValue("8192")
        int observationTouchCapacity) {
    public BotCacheProperties {
        for (Duration value : new Duration[] {replayRetention, cooldownRetention, topologyRefresh,
                statusRefresh, statusHeartbeat, observationRetention, observationTouchInterval}) {
            if (value == null || value.toMillis() < 1) throw new IllegalArgumentException("机器人缓存有效期必须为正数");
        }
        if (replayCapacity < 1 || cooldownCapacity < 1 || observationTouchCapacity < 1) throw new IllegalArgumentException("机器人缓存容量必须为正数");
        if (observationTouchInterval.compareTo(observationRetention) >= 0) {
            throw new IllegalArgumentException("观察时间合并间隔必须小于观察数据有效期");
        }
    }
}
