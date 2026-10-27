package com.nnm.bot.core.config;

import org.redisson.config.SingleServerConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/** Session storage has its own connection pool; logical DBs do not isolate memory or eviction. */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DataRedisProperties.class)
public class NnmRedisConfiguration {
    @Bean(destroyMethod = "shutdown")
    public RedissonClient nnmSessionRedis(DataRedisProperties redis) {
        Config config = new Config().setThreads(2).setNettyThreads(1)
                .setUsername(redis.getUsername())
                .setPassword(redis.getPassword() == null || redis.getPassword().isBlank() ? null : redis.getPassword());
        SingleServerConfig server = config.useSingleServer()
                .setAddress((redis.getSsl().isEnabled() ? "rediss://" : "redis://") + redis.getHost() + ":" + redis.getPort())
                .setDatabase(redis.getDatabase())
                .setConnectionMinimumIdleSize(1).setConnectionPoolSize(8)
                .setSubscriptionConnectionMinimumIdleSize(1).setSubscriptionConnectionPoolSize(2);
        if (redis.getTimeout() != null) server.setTimeout(Math.toIntExact(redis.getTimeout().toMillis()));
        if (redis.getConnectTimeout() != null) server.setConnectTimeout(Math.toIntExact(redis.getConnectTimeout().toMillis()));
        return Redisson.create(config);
    }

    @Bean
    @Primary
    public RedisConnectionFactory nnmSessionRedisConnectionFactory(
            @Qualifier("nnmSessionRedis")
            RedissonClient session) {
        return new RedissonConnectionFactory(session);
    }
}
