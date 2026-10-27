package com.nnm.bot.core.task;

import io.github.rdt.redisson.lease.RedisOwnerLeaseService.Lease;
import org.redisson.api.RAtomicLong;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.config.RdtRetryConfiguration;
import io.github.rdt.queue.QueueMode;
import io.github.rdt.queue.QueuePolicy;
import io.github.rdt.queue.QueueSubscription;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService;
import io.github.rdt.redisson.lock.RedissonLockTemplate;
import io.github.rdt.redisson.queue.RedissonMessageQueueTemplate;
import io.github.rdt.runtime.RuntimeIdentity;
import io.github.rdt.support.JsonCodec;
import io.github.rdt.support.KeySpace;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public final class RedisQueueMultiJvmWorker {
    private RedisQueueMultiJvmWorker() {
    }

    public static void main(String[] args) throws Exception {
        String redisAddress = args[0];
        KeySpace keys = new KeySpace(args[1]);
        String mode = args[2];
        String topic = args[3];
        String node = args[4];
        String metricsPrefix = args[5];

        Config config = new Config().setThreads(2).setNettyThreads(2).setLockWatchdogTimeout(3_000);
        config.useSingleServer().setAddress(redisAddress)
                .setConnectionMinimumIdleSize(1).setConnectionPoolSize(4)
                .setSubscriptionConnectionMinimumIdleSize(1).setSubscriptionConnectionPoolSize(1);
        RedissonClient client = Redisson.create(config);
        RedissonLockTemplate locks = new RedissonLockTemplate(client, keys);
        RedisOwnerLeaseService leases = new RedisOwnerLeaseService(client, keys, locks);
        Lease nodeLease = leases.acquire("node:" + node, node, Duration.ofMinutes(5)).orElseThrow();
        try {
            RuntimeIdentity identity = new RuntimeIdentity(node);
            identity.acquired(nodeLease.token(), nodeLease.fencingVersion(), () -> leases.isHeld(nodeLease));
            QueuePolicy policy = new QueuePolicy(2, Duration.ofMillis(30), Duration.ofMillis(150),
                    Duration.ofMillis(50), "drain".equals(mode) ? Duration.ofSeconds(5) : Duration.ofSeconds(2),
                    false, Duration.ZERO, Duration.ZERO,
                    0, 1, Duration.ofSeconds(1), 16, 4, 8, 32, 262144, 2048, 16777216L, 8388608);
            CountDownLatch completed = new CountDownLatch(1);
            RAtomicLong recovered = client.getAtomicLong(metricKey(keys, metricsPrefix, "recovered"));
            RAtomicLong attempts = client.getAtomicLong(metricKey(keys, metricsPrefix, "attempts"));
            RAtomicLong dead = client.getAtomicLong(metricKey(keys, metricsPrefix, "dead"));
            RAtomicLong ready = client.getAtomicLong(metricKey(keys, metricsPrefix, "ready"));
            RAtomicLong started = client.getAtomicLong(metricKey(keys, metricsPrefix, "started"));
            RAtomicLong release = client.getAtomicLong(metricKey(keys, metricsPrefix, "release"));
            RAtomicLong closeRequest = client.getAtomicLong(metricKey(keys, metricsPrefix, "close"));
            RAtomicLong closing = client.getAtomicLong(metricKey(keys, metricsPrefix, "closing"));
            RAtomicLong closed = client.getAtomicLong(metricKey(keys, metricsPrefix, "closed"));
            RAtomicLong drained = client.getAtomicLong(metricKey(keys, metricsPrefix, "drained"));
            QueueMode queueMode = mode.equals("listen") || mode.equals("broadcast")
                    ? QueueMode.RELIABLE_BROADCAST : QueueMode.STREAM;
            String group = queueMode == QueueMode.RELIABLE_BROADCAST ? "cache" : "workers";
            try (RedissonMessageQueueTemplate queue = new RedissonMessageQueueTemplate(client, keys, new JsonCodec(), policy,
                    locks, message -> dead.incrementAndGet(), identity,
                    new RdtRetryConfiguration().rdtRetryClassifier())) {
                queue.subscribe(QueueSubscription.concurrent(topic, queueMode, group, 1), message -> {
                    switch (mode) {
                        case "recover", "broadcast" -> {
                            recovered.incrementAndGet();
                            completed.countDown();
                        }
                        case "listen" -> recovered.incrementAndGet();
                        case "crash" -> {
                            attempts.incrementAndGet();
                            started.incrementAndGet();
                            while (true) Thread.sleep(1_000);
                        }
                        case "drain" -> {
                            started.incrementAndGet();
                            while (release.get() == 0) Thread.sleep(10);
                            drained.incrementAndGet();
                        }
                        case "fail" -> {
                            attempts.incrementAndGet();
                            throw new SystemException("retry across JVM until exhausted");
                        }
                        default -> throw new IllegalArgumentException("Unknown worker mode: " + mode);
                    }
                });
                ready.incrementAndGet();
                System.out.println("WORKER:READY");
                System.out.flush();
                if ("recover".equals(mode) || "broadcast".equals(mode)) {
                    completed.await(8, java.util.concurrent.TimeUnit.SECONDS);
                    Thread.sleep(300);
                    return;
                }
                if ("listen".equals(mode)) {
                    while (closeRequest.get() == 0) Thread.sleep(20);
                    return;
                }
                if ("drain".equals(mode)) {
                    long closeDeadline = System.nanoTime() + Duration.ofSeconds(8).toNanos();
                    while (closeRequest.get() == 0 && System.nanoTime() < closeDeadline) Thread.sleep(20);
                    closing.incrementAndGet();
                    queue.close();
                    closed.incrementAndGet();
                    return;
                }
                if ("crash".equals(mode)) {
                    while (true) Thread.sleep(1_000);
                }
                long deadline = System.nanoTime() + Duration.ofSeconds(8).toNanos();
                while (dead.get() == 0 && System.nanoTime() < deadline) Thread.sleep(50);
            }
        } finally {
            try {
                leases.release(nodeLease);
            } finally {
                client.shutdown();
            }
        }
    }

    private static String metricKey(KeySpace keys, String prefix, String metric) {
        return keys.key("test-metrics", prefix + ":" + metric);
    }
}
