package com.nnm.bot.core.task;

import io.github.rdt.id.IdMode;
import io.github.rdt.id.IdPolicy;
import io.github.rdt.id.RedissonIdGenerator;
import io.github.rdt.spring.lock.RdtLockAspect;
import java.time.ZoneOffset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import com.nnm.bot.common.task.AdminTaskStatus;
import com.nnm.bot.core.config.DistributedProperties;
import io.github.rdt.lease.RedisOwnerLeaseService;
import io.github.rdt.cache.CachePolicy;
import io.github.rdt.cache.RedissonCacheService;
import io.github.rdt.lock.RedissonLockManager;
import io.github.rdt.lock.RedissonLockTemplate;
import io.github.rdt.queue.*;
import io.github.rdt.support.JsonCodec;
import io.github.rdt.support.KeySpace;
import io.github.rdt.support.RdtSupport;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.stream.StreamCreateGroupArgs;
import org.redisson.api.stream.StreamMessageId;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@EnabledIfSystemProperty(named = "rdt.it.redis", matches = ".+")
class RedisCoordinationTest {
    private final KeySpace keys = new KeySpace("nnmbot-test:" + UUID.randomUUID());
    private RedissonClient a;
    private RedissonClient b;
    private RedissonCacheService cacheA;
    private RedissonCacheService cacheB;
    private ThreadPoolTaskScheduler scheduler;

    @BeforeEach
    void connect() {
        a = client();
        b = client();
        cacheA = cache(a);
        cacheB = cache(b);
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.initialize();
    }

    private RedissonClient client() {
        Config config = new Config().setThreads(2).setNettyThreads(2);
        config.useSingleServer().setAddress(System.getProperty("rdt.it.redis"))
                .setConnectionMinimumIdleSize(1).setConnectionPoolSize(8)
                .setSubscriptionConnectionMinimumIdleSize(1).setSubscriptionConnectionPoolSize(2);
        return Redisson.create(config);
    }

    private RedissonCacheService cache(RedissonClient client) {
        return new RedissonCacheService(client, keys, new JsonCodec(),
                new CachePolicy(Duration.ofMinutes(1), Duration.ofSeconds(1), 0,
                        false, Duration.ofSeconds(1), 32), new RedissonLockTemplate(client, keys));
    }

    @AfterEach
    void close() {
        scheduler.shutdown();
        cacheA.close();
        cacheB.close();
        b.shutdown();
        try {
            a.getKeys().deleteByPattern("*rdt:" + RdtSupport.component(keys.namespace()) + ":*");
        } finally {
            a.shutdown();
        }
    }

    private RedisOwnerLeaseService leases(RedissonClient client) {
        return new RedisOwnerLeaseService(client, keys, new RedissonLockTemplate(client, keys));
    }

    @Test
    void expiredOwnerCannotRenewOrReleaseSuccessor() {
        var first = leases(a);
        var second = leases(b);
        var old = first.acquire("account", "node-a", Duration.ofMillis(200)).orElseThrow();
        assertThat(second.acquire("account", "node-b", Duration.ofSeconds(5))).isEmpty();
        await().atMost(Duration.ofSeconds(3)).until(() -> !first.isHeld(old));
        var successor = second.acquire("account", "node-b", Duration.ofSeconds(5)).orElseThrow();
        assertThat(successor.fencingVersion()).isGreaterThan(old.fencingVersion());
        assertThat(first.renew(old, Duration.ofSeconds(5))).isFalse();
        first.release(old);
        assertThat(second.isHeld(successor)).isTrue();
        assertThat(second.renew(successor, Duration.ofSeconds(5))).isTrue();
    }

    @Test
    void atomicIdsRemainUniqueAcrossClientsAboveImportedSnowflakeIds() {
        var policy = new IdPolicy(IdMode.ATOMIC, 1, 1, ZoneOffset.UTC);
        var first = new RedissonIdGenerator(a, keys, policy);
        var second = new RedissonIdGenerator(b, keys, policy);
        long floor = 9_007_199_254_740_999L;
        Set<Long> allocated = ConcurrentHashMap.newKeySet();
        IntStream.range(0, 100).parallel().forEach(index ->
                allocated.add((index % 2 == 0 ? first : second).nextAtLeast("articles", floor)));
        assertThat(allocated).hasSize(100).allSatisfy(id -> assertThat(id).isGreaterThanOrEqualTo(floor));
        assertThat(first.nextAtLeast("articles", 1)).isEqualTo(floor + 100);
    }

    private RedisAdminTaskService tasks(RedissonClient client, RedissonCacheService cache,
                                       Executor executor, Duration timeout) {
        var service = new RedisAdminTaskService();
        ReflectionTestUtils.setField(service, "cache", cache);
        var lockManager = new RedissonLockManager(new RedissonLockTemplate(client, keys));
        var coordination = new AdminTaskCoordination();
        ReflectionTestUtils.setField(coordination, "lockManager", lockManager);
        var factory = new AspectJProxyFactory(coordination);
        factory.setProxyTargetClass(true);
        factory.addAspect(new RdtLockAspect(lockManager));
        ReflectionTestUtils.setField(service, "coordination", factory.getProxy());
        ReflectionTestUtils.setField(service, "properties", new DistributedProperties(true,
                Duration.ofMinutes(1), timeout, Duration.ofMillis(30), Duration.ofSeconds(30),
                Duration.ofSeconds(5), Duration.ofDays(1), Duration.ofDays(2), Duration.ofSeconds(60), 128));
        ReflectionTestUtils.setField(service, "executor", executor);
        ReflectionTestUtils.setField(service, "scheduler", scheduler);
        return service;
    }

    @Test
    void taskReservationPermissionResultAndProgressAreShared() {
        List<Runnable> pending = new ArrayList<>();
        var first = tasks(a, cacheA, pending::add, Duration.ofSeconds(5));
        var second = tasks(b, cacheB, Runnable::run, Duration.ofSeconds(5));
        var submission = first.submit("refresh", "warframe:world-state:update", progress -> {
            progress.report("FETCH", "fetch", 1, 2);
            return Map.of("count", 2);
        });
        assertThat(second.requiredPermission(submission.taskId())).contains("warframe:world-state:update");
        assertThat(second.submit("refresh", "warframe:world-state:update", progress -> {
            throw new AssertionError("Duplicate work must not run");
        }).taskId()).isEqualTo(submission.taskId());
        List<AdminTaskStatus> events = new CopyOnWriteArrayList<>();
        try (var subscription = second.subscribe(submission.taskId(), event -> events.add(event.status()))) {
            pending.getFirst().run();
            await().atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                    assertThat(events).contains(AdminTaskStatus.COMPLETED));
        }
        assertThat(second.find(submission.taskId()).orElseThrow().result()).isEqualTo(Map.of("count", 2));
        assertThat(second.findActive("refresh")).isEmpty();
        assertThat(scheduler.getScheduledThreadPoolExecutor().getQueue()).isEmpty();
    }

    @Test
    void lostWorkerTimesOutWithoutReplayingItsClosure() {
        var first = tasks(a, cacheA, ignored -> { }, Duration.ofMillis(80));
        var second = tasks(b, cacheB, Runnable::run, Duration.ofMillis(80));
        var submission = first.submit("lost", "test:task:view", progress -> null);
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            var result = second.find(submission.taskId()).orElseThrow();
            assertThat(result.status()).isEqualTo(AdminTaskStatus.FAILED);
            assertThat(result.stage()).isEqualTo("TIMED_OUT");
        });
        assertThat(second.findActive("lost")).isEmpty();
    }

    @Test
    void activeTaskDeadlineIsRenewedByProgress() throws Exception {
        List<Runnable> pending = new ArrayList<>();
        var first = tasks(a, cacheA, pending::add, Duration.ofMillis(300));
        var second = tasks(b, cacheB, Runnable::run, Duration.ofMillis(300));
        CountDownLatch firstProgress = new CountDownLatch(1);
        CountDownLatch secondProgress = new CountDownLatch(1);
        var submission = first.submit("long-refresh", "test:task:update", progress -> {
            progress.report("FETCH", "第一阶段", 1, 2);
            firstProgress.countDown();
            assertThat(secondProgress.await(2, TimeUnit.SECONDS)).isTrue();
            progress.report("APPLY", "第二阶段", 2, 2);
            return "done";
        });

        Thread.sleep(180);
        Thread worker = new Thread(pending.getFirst());
        worker.start();
        assertThat(firstProgress.await(2, TimeUnit.SECONDS)).isTrue();
        Thread.sleep(180);
        secondProgress.countDown();
        await().atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                assertThat(second.find(submission.taskId()).orElseThrow().status())
                        .isEqualTo(AdminTaskStatus.COMPLETED));
        worker.join(2_000);
        assertThat(worker.isAlive()).isFalse();
        assertThat(second.findActive("long-refresh")).isEmpty();
    }

    private RedissonMessageQueueTemplate queue(RedissonClient client, String node, List<FailedMessage> dead) {
        return new RedissonMessageQueueTemplate(client, keys, new JsonCodec(),
                new QueuePolicy(2, Duration.ofMillis(30), Duration.ofMillis(150),
                        Duration.ofMillis(50), Duration.ofSeconds(2)),
                new RedissonLockTemplate(client, keys), dead::add, node);
    }

    @Test
    void streamCompetesWhileReliableBroadcastReachesBothNodesAndResumesOffline() {
        List<FailedMessage> dead = new CopyOnWriteArrayList<>();
        AtomicInteger work = new AtomicInteger();
        AtomicInteger seenA = new AtomicInteger();
        AtomicInteger seenB = new AtomicInteger();
        try (var first = queue(a, "a", dead); var second = queue(b, "b", dead)) {
            first.subscribe(new QueueSubscription("work", QueueMode.STREAM, "workers", 1), m -> work.incrementAndGet());
            second.subscribe(new QueueSubscription("work", QueueMode.STREAM, "workers", 1), m -> work.incrementAndGet());
            first.subscribe(new QueueSubscription("invalidate", QueueMode.RELIABLE_BROADCAST, "cache", 1),
                    m -> seenA.incrementAndGet());
            var offline = second.subscribe(new QueueSubscription("invalidate", QueueMode.RELIABLE_BROADCAST, "cache", 1),
                    m -> seenB.incrementAndGet());
            for (int i = 0; i < 8; i++) first.send("work", i);
            first.reliableBroadcast("invalidate", "one");
            await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
                assertThat(work).hasValue(8);
                assertThat(seenA).hasValue(1);
                assertThat(seenB).hasValue(1);
            });
            offline.close();
            first.reliableBroadcast("invalidate", "two");
            second.subscribe(new QueueSubscription("invalidate", QueueMode.RELIABLE_BROADCAST, "cache", 1),
                    m -> seenB.incrementAndGet());
            await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
                assertThat(seenA).hasValue(2);
                assertThat(seenB).hasValue(2);
            });
            assertThat(dead).isEmpty();
        }
    }

    @Test
    void pendingMessageIsRecoveredAndPoisonMessageReachesDeadLetterStore() {
        List<FailedMessage> dead = new CopyOnWriteArrayList<>();
        AtomicInteger recovered = new AtomicInteger();
        try (var first = queue(a, "a", dead); var second = queue(b, "b", dead)) {
            var stream = a.getStream(keys.key("stream", "recover"), StringCodec.INSTANCE);
            stream.createGroup(StreamCreateGroupArgs.name("workers").id(StreamMessageId.ALL).makeStream());
            first.send("recover", "pending");
            assertThat(stream.readGroup("workers", "dead", StreamReadGroupArgs.neverDelivered().count(1))).hasSize(1);
            second.subscribe(new QueueSubscription("recover", QueueMode.STREAM, "workers", 1),
                    m -> recovered.incrementAndGet());
            second.subscribe(new QueueSubscription("poison", QueueMode.STREAM, "workers", 1),
                    m -> { throw new IllegalArgumentException("unrecoverable"); });
            first.send("poison", "bad");
            await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
                assertThat(recovered).hasValue(1);
                assertThat(dead).hasSize(1);
            });
        }
    }
}
