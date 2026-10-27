package com.nnm.bot.support;

import io.github.rdt.queue.RdtMessageSendManager;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService;
import io.github.rdt.spring.lease.NodeIdentityLease;
import io.github.rdt.state.ExpiringMapStore;
import io.github.rdt.state.SharedStateStore;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Transaction tests exercise JPA with mocked Redis boundaries; production always requires Redis. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(OfflineRedisConfiguration.class)
@TestPropertySource(properties = "nnm.executor.await-termination=1s")
@MockitoBean(types = {RedisOwnerLeaseService.class, NodeIdentityLease.class,
        RdtMessageSendManager.class, SharedStateStore.class, ExpiringMapStore.class})
@MockitoBean(name = "rdtRuntimeRedis", types = RedissonClient.class)
@MockitoBean(name = "rdtCacheRedis", types = RedissonClient.class)
@MockitoBean(name = "nnmSessionRedis", types = RedissonClient.class)
@MockitoBean(name = "nnmSessionRedisConnectionFactory", types = RedisConnectionFactory.class)
public @interface OfflineRedis { }
