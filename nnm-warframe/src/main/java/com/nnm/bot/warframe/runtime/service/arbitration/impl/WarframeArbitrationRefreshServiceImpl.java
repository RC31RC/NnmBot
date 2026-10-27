package com.nnm.bot.warframe.runtime.service.arbitration.impl;

import com.nnm.bot.warframe.runtime.cache.arbitration.WarframeArbitrationSnapshotStore;
import com.nnm.bot.warframe.runtime.client.arbitration.WarframeArbitrationClient;
import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationPullProperties;
import com.nnm.bot.warframe.runtime.parser.arbitration.WarframeArbitrationScheduleParser;
import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationRefreshService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class WarframeArbitrationRefreshServiceImpl implements WarframeArbitrationRefreshService {
    private final ReentrantLock refreshLock = new ReentrantLock();

    @Resource
    private WarframeArbitrationClient client;
    @Resource
    private WarframeArbitrationScheduleParser parser;
    @Resource
    private WarframeArbitrationSnapshotStore snapshots;
    @Resource
    private WarframeArbitrationPullProperties properties;
    @Resource
    private Clock warframeClock;

    private volatile Instant nextAttemptAt = Instant.MIN;

    /**
     * 获取当前上游数据并更新本地有效状态。
     */
    @Override
    public void refresh() {
        refreshLock.lock();
        try {
            Instant now = warframeClock.instant();
            if (now.isBefore(nextAttemptAt)) return;
            Instant lastFetchedAt = snapshots.current().fetchedAt();
            if (lastFetchedAt != null && now.isBefore(lastFetchedAt.plus(properties.getRefreshInterval()))) {
                nextAttemptAt = lastFetchedAt.plus(properties.getRefreshInterval());
                return;
            }
            try {
                snapshots.replace(parser.parse(client.fetch()), now);
                nextAttemptAt = now.plus(properties.getRefreshInterval());
            } catch (RuntimeException exception) {
                nextAttemptAt = now.plus(properties.getRetryInterval());
                log.warn("仲裁排期刷新失败，保留最近一次有效内存快照", exception);
            }
        } finally {
            refreshLock.unlock();
        }
    }
}
