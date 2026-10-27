package com.nnm.bot.core.task;

import io.github.rdt.redisson.lease.RedisOwnerLeaseService.Lease;
import java.util.Optional;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService;
import io.github.rdt.redisson.lock.RedissonLockTemplate;
import io.github.rdt.support.KeySpace;
import java.time.Duration;
import java.util.UUID;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public final class RedisMultiJvmWorker {
    private RedisMultiJvmWorker() {
    }

    public static void main(String[] args) throws Exception {
        String redisAddress = args[0];
        KeySpace keys = new KeySpace(args[1]);
        String mode = args[2];
        String leaseName = args[3];
        String node = args[4];
        Duration leaseDuration = Duration.ofMillis(Long.parseLong(args[5]));

        Config config = new Config().setThreads(2).setNettyThreads(2);
        config.useSingleServer().setAddress(redisAddress)
                .setConnectionMinimumIdleSize(1).setConnectionPoolSize(4)
                .setSubscriptionConnectionMinimumIdleSize(1).setSubscriptionConnectionPoolSize(1);
        RedissonClient client = Redisson.create(config);
        try {
            RedissonLockTemplate locks = new RedissonLockTemplate(client, keys);
            RedisOwnerLeaseService leases = new RedisOwnerLeaseService(client, keys, locks);
            if ("hold".equals(mode)) {
                Lease lease = leases.acquire(leaseName, node, leaseDuration).orElseThrow();
                System.out.println("LEASE:ACQUIRED:" + lease.fencingVersion());
                System.out.flush();
                while (true) {
                    Thread.sleep(Math.max(50, leaseDuration.toMillis() / 5));
                    if (!leases.renew(lease, leaseDuration)) System.exit(3);
                }
            }

            long deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();
            do {
                Optional<Lease> lease = leases.acquire(leaseName, node + ":" + UUID.randomUUID(), leaseDuration);
                if (lease.isPresent()) {
                    System.out.println("LEASE:ACQUIRED:" + lease.get().fencingVersion());
                    System.out.flush();
                    leases.release(lease.get());
                    return;
                }
                if ("once".equals(mode)) {
                    System.out.println("LEASE:BLOCKED");
                    System.out.flush();
                    return;
                }
                Thread.sleep(100);
            } while (System.nanoTime() < deadline);
            System.out.println("LEASE:TIMEOUT");
            System.out.flush();
            System.exit(2);
        } finally {
            client.shutdown();
        }
    }
}
