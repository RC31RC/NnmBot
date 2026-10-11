package com.nnm.bot.warframe.service;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.warframe.config.properties.WarframeCommunityDataProperties;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import io.github.rdt.lock.DistributedLockTemplate;
import io.github.rdt.lock.LockHandle;
import io.github.rdt.lock.LockOptions;
import io.github.rdt.lock.LockType;
import io.github.rdt.spring.config.RdtProperties;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Coordinates every write path for one community dataset.
 *
 * <p>The merge path releases the RDT handle from transaction completion, because a
 * method-level lock does not cover the database commit performed by Spring.</p>
 */
@Service
public class WarframeCommunityDatasetLock {

    private static final String KEY_PREFIX = "warframe-community-dataset:";

    @Autowired(required = false)
    private DistributedLockTemplate lockTemplate;
    @Autowired(required = false)
    private RdtProperties rdtProperties;
    @Resource
    private WarframeCommunityDataProperties properties;
    private final ConcurrentHashMap<String, ReentrantLock> localLocks = new ConcurrentHashMap<>();

    public LockHandle acquire(String dataset) {
        String lockDataset = lockDataset(dataset);
        if (lockTemplate == null) {
            if (rdtProperties != null && rdtProperties.enabled()) {
                throw new IllegalStateException(
                        "RDT is enabled but DistributedLockTemplate is unavailable");
            }
            return acquireLocal(lockDataset);
        }
        return lockTemplate.tryLock(
                        KEY_PREFIX + lockDataset,
                        new LockOptions(LockType.WRITE, Duration.ZERO, properties.getLockLease()))
                .orElseThrow(() -> new BusinessException(
                        WarframeManagementErrorCode.COMMUNITY_DATASET_BUSY));
    }

    public void releaseAfterTransaction(LockHandle handle) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            handle.close();
                        }
                    });
        } else {
            handle.close();
        }
    }

    private LockHandle acquireLocal(String dataset) {
        ReentrantLock lock = localLocks.computeIfAbsent(dataset, ignored -> new ReentrantLock());
        if (!lock.tryLock()) {
            throw new BusinessException(WarframeManagementErrorCode.COMMUNITY_DATASET_BUSY);
        }
        return new LockHandle() {
            private boolean held = true;

            @Override
            public boolean isHeld() {
                return held && lock.isHeldByCurrentThread();
            }

            @Override
            public void close() {
                if (held) {
                    held = false;
                    lock.unlock();
                }
            }
        };
    }

    /**
     * 遗物主表、奖励和掉落虽然分别刷新，但共享同一业务对象的写入互斥边界，
     * 避免三个数据集的刷新、采纳或人工删除并发交叉。
     */
    private String lockDataset(String dataset) {
        if ("relic".equals(dataset)
                || "relic_reward".equals(dataset)
                || "relic_drop".equals(dataset)) {
            return "relic";
        }
        return dataset;
    }
}
