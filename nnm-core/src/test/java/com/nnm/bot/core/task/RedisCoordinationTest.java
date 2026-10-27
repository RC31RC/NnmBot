package com.nnm.bot.core.task;

import com.nnm.bot.common.task.AdminTaskEvent;
import com.nnm.bot.common.task.AdminTaskService.TaskSubscription;
import com.nnm.bot.common.task.AdminTaskSubmission;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService.Lease;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import org.awaitility.core.ConditionTimeoutException;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RStream;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.config.RdtRetryConfiguration;
import io.github.rdt.redisson.queue.RedissonMessageQueueTemplate;

import java.util.List;

import java.util.Map;

import io.github.rdt.runtime.RuntimeIdentity;

import io.github.rdt.id.IdMode;
import io.github.rdt.id.IdPolicy;
import io.github.rdt.redisson.id.RedissonIdGenerator;
import io.github.rdt.spring.lock.RdtLockAspect;
import java.time.ZoneOffset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import com.nnm.bot.common.constant.TaskConstants.AdminTaskStatus;
import com.nnm.bot.core.config.ClusterProperties;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService;
import io.github.rdt.redisson.state.RedissonSharedStateStore;
import io.github.rdt.lock.DefaultLockManager;
import io.github.rdt.redisson.lock.RedissonLockTemplate;
import io.github.rdt.queue.*;
import io.github.rdt.support.JsonCodec;
import io.github.rdt.support.KeySpace;
import io.github.rdt.support.RdtSupport;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CompletableFuture;
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
    private RedissonSharedStateStore stateA;
    private RedissonSharedStateStore stateB;
    private ThreadPoolTaskScheduler scheduler;

    @BeforeEach
    void connect() {
        a = client();
        b = client();
        stateA = stateStore(a);
        stateB = stateStore(b);
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

    private RedissonSharedStateStore stateStore(RedissonClient client) {
        return new RedissonSharedStateStore(client, keys, new JsonCodec());
    }

    @AfterEach
    void close() {
        scheduler.shutdown();
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
        RedisOwnerLeaseService first = leases(a);
        RedisOwnerLeaseService second = leases(b);
        Lease old = first.acquire("account", "node-a", Duration.ofMillis(200)).orElseThrow();
        assertThat(second.acquire("account", "node-b", Duration.ofSeconds(5))).isEmpty();
        await().atMost(Duration.ofSeconds(3)).until(() -> !first.isHeld(old));
        Lease successor = second.acquire("account", "node-b", Duration.ofSeconds(5)).orElseThrow();
        assertThat(successor.fencingVersion()).isGreaterThan(old.fencingVersion());
        assertThat(first.renew(old, Duration.ofSeconds(5))).isFalse();
        first.release(old);
        assertThat(second.isHeld(successor)).isTrue();
        assertThat(second.renew(successor, Duration.ofSeconds(5))).isTrue();
    }

    @Test
    void atomicIdsRemainUniqueAcrossClientsAboveImportedSnowflakeIds() {
        IdPolicy policy = new IdPolicy(IdMode.ATOMIC, 1, 1, ZoneOffset.UTC);
        RedissonIdGenerator first = new RedissonIdGenerator(a, keys, policy);
        RedissonIdGenerator second = new RedissonIdGenerator(b, keys, policy);
        long floor = 9_007_199_254_740_999L;
        Set<Long> allocated = ConcurrentHashMap.newKeySet();
        IntStream.range(0, 100).parallel().forEach(index ->
                allocated.add((index % 2 == 0 ? first : second).nextAtLeast("articles", floor)));
        assertThat(allocated).hasSize(100).allSatisfy(id -> assertThat(id).isGreaterThanOrEqualTo(floor));
        assertThat(first.nextAtLeast("articles", 1)).isEqualTo(floor + 100);
    }

    private RedisAdminTaskService tasks(RedissonClient client, RedissonSharedStateStore stateStore,
                                       Executor executor, Duration timeout) {
        RedisAdminTaskService service = new RedisAdminTaskService();
        ReflectionTestUtils.setField(service, "stateStore", stateStore);
        DefaultLockManager lockManager = new DefaultLockManager(new RedissonLockTemplate(client, keys));
        AdminTaskCoordination coordination = new AdminTaskCoordination();
        ReflectionTestUtils.setField(coordination, "lockManager", lockManager);
        AspectJProxyFactory factory = new AspectJProxyFactory(coordination);
        factory.setProxyTargetClass(true);
        factory.addAspect(new RdtLockAspect(lockManager));
        ReflectionTestUtils.setField(service, "coordination", factory.getProxy());
        ReflectionTestUtils.setField(service, "properties", new ClusterProperties(Duration.ofMinutes(1), timeout, Duration.ofMillis(30), Duration.ofSeconds(30),
                Duration.ofSeconds(5), Duration.ofDays(1), Duration.ofSeconds(60),
                Duration.ofSeconds(120), Duration.ofSeconds(30), Duration.ofMinutes(2), 128));
        ReflectionTestUtils.setField(service, "executor", executor);
        ReflectionTestUtils.setField(service, "scheduler", scheduler);
        return service;
    }

    @Test
    void taskReservationPermissionResultAndProgressAreShared() {
        List<Runnable> pending = new ArrayList<>();
        RedisAdminTaskService first = tasks(a, stateA, pending::add, Duration.ofSeconds(5));
        RedisAdminTaskService second = tasks(b, stateB, Runnable::run, Duration.ofSeconds(5));
        AdminTaskSubmission submission = first.submit("refresh", "warframe:world-state:update", progress -> {
            progress.report("FETCH", "fetch", 1, 2);
            return Map.of("count", 2);
        });
        assertThat(second.requiredPermission(submission.taskId())).contains("warframe:world-state:update");
        assertThat(second.submit("refresh", "warframe:world-state:update", progress -> {
            throw new AssertionError("Duplicate work must not run");
        }).taskId()).isEqualTo(submission.taskId());
        List<AdminTaskStatus> events = new CopyOnWriteArrayList<>();
        try (TaskSubscription subscription = second.subscribe(submission.taskId(), event -> events.add(event.status()))) {
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
        RedisAdminTaskService first = tasks(a, stateA, ignored -> { }, Duration.ofMillis(80));
        RedisAdminTaskService second = tasks(b, stateB, Runnable::run, Duration.ofMillis(80));
        AdminTaskSubmission submission = first.submit("lost", "test:task:view", progress -> null);
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            AdminTaskEvent result = second.find(submission.taskId()).orElseThrow();
            assertThat(result.status()).isEqualTo(AdminTaskStatus.FAILED);
            assertThat(result.stage()).isEqualTo("TIMED_OUT");
        });
        assertThat(second.findActive("lost")).isEmpty();
    }

    @Test
    void activeTaskDeadlineIsRenewedByProgress() throws Exception {
        List<Runnable> pending = new ArrayList<>();
        RedisAdminTaskService first = tasks(a, stateA, pending::add, Duration.ofMillis(300));
        RedisAdminTaskService second = tasks(b, stateB, Runnable::run, Duration.ofMillis(300));
        CountDownLatch firstProgress = new CountDownLatch(1);
        CountDownLatch secondProgress = new CountDownLatch(1);
        AdminTaskSubmission submission = first.submit("long-refresh", "test:task:update", progress -> {
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
        RedissonLockTemplate locks = new RedissonLockTemplate(client, keys);
        RedisOwnerLeaseService leases = new RedisOwnerLeaseService(client, keys, locks);
        RuntimeIdentity identity = new RuntimeIdentity(node);
        Lease lease = leases.acquire("node:" + node, node, Duration.ofMinutes(5)).orElseThrow();
        identity.acquired(lease.token(), lease.fencingVersion(), () -> leases.isHeld(lease));
        QueuePolicy policy = new QueuePolicy(2, Duration.ofMillis(30), Duration.ofMillis(150), Duration.ofMillis(50), Duration.ofSeconds(2), false, Duration.ZERO, Duration.ZERO, 0, 1, Duration.ofSeconds(1), 16, 4, 8, 32, 262144, 2048, 16777216L, 8388608);
        return new RedissonMessageQueueTemplate(client, keys, new JsonCodec(), policy, locks, dead::add, identity, new RdtRetryConfiguration().rdtRetryClassifier());
    }

    @Test
    void streamCompetesWhileReliableBroadcastReachesBothNodesAndResumesOffline() {
        List<FailedMessage> dead = new CopyOnWriteArrayList<>();
        AtomicInteger work = new AtomicInteger();
        AtomicInteger seenA = new AtomicInteger();
        AtomicInteger seenB = new AtomicInteger();
        try (RedissonMessageQueueTemplate first = queue(a, "a", dead); RedissonMessageQueueTemplate second = queue(b, "b", dead)) {
            first.subscribe(QueueSubscription.concurrent("work", QueueMode.STREAM, "workers", 1), m -> work.incrementAndGet());
            second.subscribe(QueueSubscription.concurrent("work", QueueMode.STREAM, "workers", 1), m -> work.incrementAndGet());
            first.subscribe(QueueSubscription.concurrent("invalidate", QueueMode.RELIABLE_BROADCAST, "cache", 1),
                    m -> seenA.incrementAndGet());
            Subscription offline = second.subscribe(QueueSubscription.concurrent("invalidate", QueueMode.RELIABLE_BROADCAST, "cache", 1),
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
            second.subscribe(QueueSubscription.concurrent("invalidate", QueueMode.RELIABLE_BROADCAST, "cache", 1),
                    m -> seenB.incrementAndGet());
            await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
                assertThat(seenA).hasValue(2);
                assertThat(seenB).hasValue(2);
            });
            assertThat(dead).isEmpty();
        }
    }

    @Test
    void applicationSystemFailuresRetryButBusinessFailuresWithSystemCausesDoNot() {
        List<FailedMessage> dead = new CopyOnWriteArrayList<>();
        AtomicInteger systemCalls = new AtomicInteger();
        AtomicInteger businessCalls = new AtomicInteger();
        BusinessException business = new BusinessException(CommonErrorCode.DATA_CONFLICT, "business rejection",
                new SystemException("a cause must not override the business category"));
        try (RedissonMessageQueueTemplate queue = queue(a, "classified-worker", dead)) {
            queue.subscribe(QueueSubscription.concurrent("system", QueueMode.STREAM, "workers", 1), message -> {
                systemCalls.incrementAndGet();
                throw new SystemException("temporary downstream failure");
            });
            queue.subscribe(QueueSubscription.concurrent("business", QueueMode.STREAM, "workers", 1), message -> {
                businessCalls.incrementAndGet();
                throw business;
            });
            queue.send("system", "work");
            queue.send("business", "work");
            await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
                assertThat(dead).hasSize(2);
                assertThat(systemCalls).hasValue(2);
                assertThat(businessCalls).hasValue(1);
            });
        }
    }

    @Test
    void pendingMessageIsRecoveredAndPoisonMessageReachesDeadLetterStore() {
        List<FailedMessage> dead = new CopyOnWriteArrayList<>();
        AtomicInteger recovered = new AtomicInteger();
        try (RedissonMessageQueueTemplate first = queue(a, "a", dead); RedissonMessageQueueTemplate second = queue(b, "b", dead)) {
            RStream<Object,Object> stream = a.getStream(keys.key("stream", "recover"), StringCodec.INSTANCE);
            stream.createGroup(StreamCreateGroupArgs.name("workers").id(StreamMessageId.ALL).makeStream());
            first.send("recover", "pending");
            assertThat(stream.readGroup("workers", "dead", StreamReadGroupArgs.neverDelivered().count(1))).hasSize(1);
            second.subscribe(QueueSubscription.concurrent("recover", QueueMode.STREAM, "workers", 1),
                    m -> recovered.incrementAndGet());
            second.subscribe(QueueSubscription.concurrent("poison", QueueMode.STREAM, "workers", 1),
                    m -> { throw new IllegalArgumentException("unrecoverable"); });
            first.send("poison", "bad");
            await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
                assertThat(recovered).hasValue(1);
                assertThat(dead).hasSize(1);
            });
        }
    }

    @Test
    void ownerLeaseIsExclusiveAndTakenOverAfterJvmExit() throws Exception {
        String leaseName = "multi-jvm-owner:" + UUID.randomUUID();
        Duration leaseDuration = Duration.ofMillis(700);
        Process holder = null;
        try {
            holder = startWorker("hold", leaseName, "node-a", leaseDuration.toMillis());
            String firstLease = awaitWorkerLine(holder, "LEASE:ACQUIRED:");
            long firstFence = Long.parseLong(firstLease.substring("LEASE:ACQUIRED:".length()));

            Process contender = startWorker("once", leaseName, "node-b", leaseDuration.toMillis());
            assertThat(awaitWorkerLine(contender, "LEASE:")).isEqualTo("LEASE:BLOCKED");
            assertThat(contender.waitFor(5, TimeUnit.SECONDS)).isTrue();
            assertThat(contender.exitValue()).isZero();

            holder.destroyForcibly();
            assertThat(holder.waitFor(5, TimeUnit.SECONDS)).isTrue();

            Process successor = startWorker("takeover", leaseName, "node-c", leaseDuration.toMillis());
            String nextLease = awaitWorkerLine(successor, "LEASE:");
            assertThat(nextLease).startsWith("LEASE:ACQUIRED:");
            long nextFence = Long.parseLong(nextLease.substring("LEASE:ACQUIRED:".length()));
            assertThat(nextFence).isGreaterThan(firstFence);
            assertThat(successor.waitFor(5, TimeUnit.SECONDS)).isTrue();
            assertThat(successor.exitValue()).isZero();
        } finally {
            if (holder != null && holder.isAlive()) {
                holder.destroyForcibly();
                holder.waitFor(5, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    void pendingRecoveryAndRetryExhaustionWorkAcrossJvms() throws Exception {
        String topic = "multi-jvm-stream:" + UUID.randomUUID();
        String metricsPrefix = UUID.randomUUID().toString();
        String readyKey = metricKey(metricsPrefix, "ready");
        String recoveredKey = metricKey(metricsPrefix, "recovered");
        String attemptsKey = metricKey(metricsPrefix, "attempts");
        String deadKey = metricKey(metricsPrefix, "dead");
        List<Process> workers = new ArrayList<>();
        List<Path> workerLogs = new ArrayList<>();
        List<FailedMessage> dead = new CopyOnWriteArrayList<>();
        RAtomicLong ready = a.getAtomicLong(readyKey);
        RAtomicLong recovered = a.getAtomicLong(recoveredKey);
        RAtomicLong attempts = a.getAtomicLong(attemptsKey);
        RAtomicLong deadLetters = a.getAtomicLong(deadKey);
        ready.set(0);
        recovered.set(0);
        attempts.set(0);
        deadLetters.set(0);

        try (RedissonMessageQueueTemplate producer = queue(a, "multi-jvm-producer", dead)) {
            RStream<Object,Object> stream = a.getStream(keys.key("stream", topic), StringCodec.INSTANCE);
            stream.createGroup(StreamCreateGroupArgs.name("workers").id(StreamMessageId.ALL).makeStream());
            producer.send(topic, "pending-before-consumer-start");
            assertThat(stream.readGroup("workers", "dead", StreamReadGroupArgs.neverDelivered().count(1)))
                    .hasSize(1);
            assertThat(stream.listPending("workers", StreamMessageId.MIN, StreamMessageId.MAX, 10)).hasSize(1);

            long expectedReady = ready.get() + 1;
            Process recovering = startQueueWorker("recover", topic, "multi-jvm-recoverer", metricsPrefix, workerLogs);
            workers.add(recovering);
            awaitQueueWorkerReady(ready, expectedReady);
            try {
                await().atMost(Duration.ofSeconds(8)).untilAsserted(() -> assertThat(recovered.get()).isEqualTo(1));
            } catch (ConditionTimeoutException failure) {
                throw new AssertionError("Pending recovery worker output: "
                        + java.nio.file.Files.readString(workerLogs.getLast()), failure);
            }
            assertThat(recovering.waitFor(8, TimeUnit.SECONDS)).isTrue();
            assertThat(recovering.exitValue()).isZero();
            await().atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                    assertThat(stream.listPending("workers", StreamMessageId.MIN, StreamMessageId.MAX, 10))
                            .isEmpty());

            expectedReady = ready.get() + 1;
            Process retrying = startQueueWorker("fail", topic, "multi-jvm-retry", metricsPrefix, workerLogs);
            workers.add(retrying);
            awaitQueueWorkerReady(ready, expectedReady);
            producer.send(topic, "retry-until-dead-letter");
            await().atMost(Duration.ofSeconds(8)).untilAsserted(() -> {
                assertThat(attempts.get()).isEqualTo(2);
                assertThat(deadLetters.get()).isEqualTo(1);
            });
            retrying.destroyForcibly();
            assertThat(retrying.waitFor(5, TimeUnit.SECONDS)).isTrue();
        } finally {
            workers.stream().filter(Process::isAlive).forEach(Process::destroyForcibly);
            for (Process worker : workers) {
                if (worker.isAlive()) worker.waitFor(5, TimeUnit.SECONDS);
            }
            a.getKeys().delete(readyKey, recoveredKey, attemptsKey, deadKey);
            for (Path log : workerLogs) java.nio.file.Files.deleteIfExists(log);
        }
    }

    @Test
    void inFlightCrashIsRecoveredByAnotherJvm() throws Exception {
        String topic = "multi-jvm-crash:" + UUID.randomUUID();
        String metricsPrefix = UUID.randomUUID().toString();
        RAtomicLong ready = a.getAtomicLong(metricKey(metricsPrefix, "ready"));
        RAtomicLong started = a.getAtomicLong(metricKey(metricsPrefix, "started"));
        RAtomicLong recovered = a.getAtomicLong(metricKey(metricsPrefix, "recovered"));
        List<Process> workers = new ArrayList<>();
        List<Path> workerLogs = new ArrayList<>();
        List<FailedMessage> dead = new CopyOnWriteArrayList<>();
        ready.set(0);
        started.set(0);
        recovered.set(0);

        try (RedissonMessageQueueTemplate producer = queue(a, "multi-jvm-crash-producer", dead)) {
            RStream<Object,Object> stream = a.getStream(keys.key("stream", topic), StringCodec.INSTANCE);
            stream.createGroup(StreamCreateGroupArgs.name("workers").id(StreamMessageId.ALL).makeStream());
            producer.send(topic, "crash-during-handler");

            long expectedReady = ready.get() + 1;
            Process crashed = startQueueWorker("crash", topic, "multi-jvm-crashed", metricsPrefix, workerLogs);
            workers.add(crashed);
            awaitQueueWorkerReady(ready, expectedReady);
            await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(started.get()).isEqualTo(1));
            assertThat(stream.listPending("workers", StreamMessageId.MIN, StreamMessageId.MAX, 10)).hasSize(1);

            crashed.destroyForcibly();
            assertThat(crashed.waitFor(5, TimeUnit.SECONDS)).isTrue();

            expectedReady = ready.get() + 1;
            Process replacement = startQueueWorker("recover", topic, "multi-jvm-replacement", metricsPrefix, workerLogs);
            workers.add(replacement);
            awaitQueueWorkerReady(ready, expectedReady);
            try {
                await().atMost(Duration.ofSeconds(12)).untilAsserted(() -> assertThat(recovered.get()).isEqualTo(1));
            } catch (ConditionTimeoutException failure) {
                throw new AssertionError("Crash recovery worker output: "
                        + java.nio.file.Files.readString(workerLogs.getLast()), failure);
            }
            assertThat(replacement.waitFor(8, TimeUnit.SECONDS)).isTrue();
            assertThat(replacement.exitValue()).isZero();
            await().atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                    assertThat(stream.listPending("workers", StreamMessageId.MIN, StreamMessageId.MAX, 10)).isEmpty());
        } finally {
            workers.stream().filter(Process::isAlive).forEach(Process::destroyForcibly);
            for (Process worker : workers) {
                if (worker.isAlive()) worker.waitFor(5, TimeUnit.SECONDS);
            }
            a.getKeys().delete(metricKey(metricsPrefix, "ready"), metricKey(metricsPrefix, "started"),
                    metricKey(metricsPrefix, "recovered"));
            for (Path log : workerLogs) java.nio.file.Files.deleteIfExists(log);
        }
    }

    @Test
    void reliableBroadcastIsRecoveredAfterStableNodeRestarts() throws Exception {
        String topic = "multi-jvm-broadcast:" + UUID.randomUUID();
        String offlinePrefix = UUID.randomUUID().toString();
        String onlinePrefix = UUID.randomUUID().toString();
        RAtomicLong offlineReady = a.getAtomicLong(metricKey(offlinePrefix, "ready"));
        RAtomicLong offlineClose = a.getAtomicLong(metricKey(offlinePrefix, "close"));
        RAtomicLong onlineReady = a.getAtomicLong(metricKey(onlinePrefix, "ready"));
        RAtomicLong recovered = a.getAtomicLong(metricKey(onlinePrefix, "recovered"));
        List<Process> workers = new ArrayList<>();
        List<Path> workerLogs = new ArrayList<>();
        List<FailedMessage> dead = new CopyOnWriteArrayList<>();
        offlineReady.set(0);
        offlineClose.set(0);
        onlineReady.set(0);
        recovered.set(0);

        try (RedissonMessageQueueTemplate producer = queue(a, "multi-jvm-broadcast-producer", dead)) {
            long expectedOfflineReady = offlineReady.get() + 1;
            Process firstRun = startQueueWorker("listen", topic, "stable-broadcast-node", offlinePrefix, workerLogs);
            workers.add(firstRun);
            awaitQueueWorkerReady(offlineReady, expectedOfflineReady);
            offlineClose.set(1);
            assertThat(firstRun.waitFor(8, TimeUnit.SECONDS)).isTrue();
            assertThat(firstRun.exitValue()).isZero();

            producer.reliableBroadcast(topic, "published-while-node-offline");
            long expectedOnlineReady = onlineReady.get() + 1;
            Process restarted = startQueueWorker("broadcast", topic, "stable-broadcast-node", onlinePrefix, workerLogs);
            workers.add(restarted);
            awaitQueueWorkerReady(onlineReady, expectedOnlineReady);
            await().atMost(Duration.ofSeconds(8)).untilAsserted(() -> assertThat(recovered.get()).isEqualTo(1));
            assertThat(restarted.waitFor(8, TimeUnit.SECONDS)).isTrue();
            assertThat(restarted.exitValue()).isZero();
        } finally {
            workers.stream().filter(Process::isAlive).forEach(Process::destroyForcibly);
            for (Process worker : workers) {
                if (worker.isAlive()) worker.waitFor(5, TimeUnit.SECONDS);
            }
            a.getKeys().delete(metricKey(offlinePrefix, "ready"), metricKey(offlinePrefix, "close"),
                    metricKey(onlinePrefix, "ready"), metricKey(onlinePrefix, "recovered"));
            for (Path log : workerLogs) java.nio.file.Files.deleteIfExists(log);
        }
    }

    @Test
    void gracefulQueueShutdownWaitsForInFlightHandlerAndAcknowledgement() throws Exception {
        String topic = "multi-jvm-drain:" + UUID.randomUUID();
        String metricsPrefix = UUID.randomUUID().toString();
        RAtomicLong ready = a.getAtomicLong(metricKey(metricsPrefix, "ready"));
        RAtomicLong started = a.getAtomicLong(metricKey(metricsPrefix, "started"));
        RAtomicLong release = a.getAtomicLong(metricKey(metricsPrefix, "release"));
        RAtomicLong closeRequest = a.getAtomicLong(metricKey(metricsPrefix, "close"));
        RAtomicLong closing = a.getAtomicLong(metricKey(metricsPrefix, "closing"));
        RAtomicLong closed = a.getAtomicLong(metricKey(metricsPrefix, "closed"));
        RAtomicLong drained = a.getAtomicLong(metricKey(metricsPrefix, "drained"));
        List<Process> workers = new ArrayList<>();
        List<Path> workerLogs = new ArrayList<>();
        List<FailedMessage> dead = new CopyOnWriteArrayList<>();
        ready.set(0);
        started.set(0);
        release.set(0);
        closeRequest.set(0);
        closing.set(0);
        closed.set(0);
        drained.set(0);

        try (RedissonMessageQueueTemplate producer = queue(a, "multi-jvm-drain-producer", dead)) {
            RStream<Object,Object> stream = a.getStream(keys.key("stream", topic), StringCodec.INSTANCE);
            stream.createGroup(StreamCreateGroupArgs.name("workers").id(StreamMessageId.ALL).makeStream());
            producer.send(topic, "finish-before-shutdown");
            long expectedReady = ready.get() + 1;
            Process worker = startQueueWorker("drain", topic, "multi-jvm-drainer", metricsPrefix, workerLogs);
            workers.add(worker);
            awaitQueueWorkerReady(ready, expectedReady);
            await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(started.get()).isEqualTo(1));

            closeRequest.set(1);
            await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(closing.get()).isEqualTo(1));
            Thread.sleep(150);
            assertThat(closed.get()).isZero();
            assertThat(stream.listPending("workers", StreamMessageId.MIN, StreamMessageId.MAX, 10)).hasSize(1);

            release.set(1);
            assertThat(worker.waitFor(8, TimeUnit.SECONDS)).isTrue();
            assertThat(worker.exitValue()).isZero();
            assertThat(closed.get()).isEqualTo(1);
            assertThat(drained.get()).isEqualTo(1);
            assertThat(stream.listPending("workers", StreamMessageId.MIN, StreamMessageId.MAX, 10)).isEmpty();
        } finally {
            workers.stream().filter(Process::isAlive).forEach(Process::destroyForcibly);
            for (Process worker : workers) {
                if (worker.isAlive()) worker.waitFor(5, TimeUnit.SECONDS);
            }
            a.getKeys().delete(metricKey(metricsPrefix, "ready"), metricKey(metricsPrefix, "started"),
                    metricKey(metricsPrefix, "release"), metricKey(metricsPrefix, "close"),
                    metricKey(metricsPrefix, "closing"), metricKey(metricsPrefix, "closed"),
                    metricKey(metricsPrefix, "drained"));
            for (Path log : workerLogs) java.nio.file.Files.deleteIfExists(log);
        }
    }

    private void awaitQueueWorkerReady(RAtomicLong ready, long expected) {
        await().atMost(Duration.ofSeconds(8)).untilAsserted(() -> assertThat(ready.get()).isGreaterThanOrEqualTo(expected));
    }

    private Process startWorker(String mode, String leaseName, String node, long leaseMillis) throws Exception {
        String classPath = System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"));
        return new ProcessBuilder(
                javaExecutable(), "-cp", classPath, RedisMultiJvmWorker.class.getName(),
                System.getProperty("rdt.it.redis"), keys.namespace(), mode, leaseName, node,
                Long.toString(leaseMillis))
                .redirectErrorStream(true)
                .start();
    }

    private Process startQueueWorker(String mode, String topic, String node, String metricsPrefix,
                                    List<Path> workerLogs) throws Exception {
        String classPath = System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"));
        Path log = java.nio.file.Files.createTempFile("nnmbot-redis-worker-", ".log");
        workerLogs.add(log);
        return new ProcessBuilder(
                javaExecutable(), "-cp", classPath, RedisQueueMultiJvmWorker.class.getName(),
                System.getProperty("rdt.it.redis"), keys.namespace(), mode, topic, node, metricsPrefix)
                .redirectErrorStream(true)
                .redirectOutput(log.toFile())
                .start();
    }

    private String metricKey(String prefix, String metric) {
        return keys.key("test-metrics", prefix + ":" + metric);
    }

    private String awaitWorkerLine(Process process, String prefix) throws Exception {
        CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> {
            try (BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(prefix)) return line;
                }
                throw new IllegalStateException("Redis worker exited before reporting " + prefix);
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to read Redis worker output", exception);
            }
        });
        try {
            return result.get(10, TimeUnit.SECONDS);
        } catch (Exception exception) {
            process.destroyForcibly();
            throw exception;
        }
    }

    private String javaExecutable() {
        String executable = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")
                ? "java.exe" : "java";
        return java.nio.file.Path.of(System.getProperty("java.home"), "bin", executable).toString();
    }
}
