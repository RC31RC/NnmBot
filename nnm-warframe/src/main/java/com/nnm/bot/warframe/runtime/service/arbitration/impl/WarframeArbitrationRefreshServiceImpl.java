package com.nnm.bot.warframe.runtime.service.arbitration.impl;

import com.nnm.bot.warframe.runtime.cache.arbitration.WarframeArbitrationSnapshotStore;
import com.nnm.bot.warframe.runtime.client.arbitration.WarframeArbitrationClient;
import com.nnm.bot.warframe.runtime.config.arbitration.WarframeArbitrationSourceProperties;
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
    private WarframeArbitrationSourceProperties properties;
    @Resource
    private Clock warframeClock;

    /** 仅在成功刷新间隔到期时请求上游；失败由定时任务按 retryInterval 再次触发。 */
    @Override
    public void refresh() {
        refreshLock.lock();
        try {
            Instant now = warframeClock.instant();
            Instant lastFetchedAt = snapshots.current().fetchedAt();
            if (lastFetchedAt != null && now.isBefore(lastFetchedAt.plus(properties.getRefreshInterval()))) {
                return;
            }
            try {
                var entries = parser.parse(client.fetch());
                Instant completedAt = warframeClock.instant();
                snapshots.replace(entries, completedAt);
            } catch (RuntimeException exception) {
                log.warn("仲裁排期刷新失败，保留最近一次有效内存快照 source={} retryAfter={}", properties.getUrl(),
                    properties.getRetryInterval(), exception);
            }
        } finally {
            refreshLock.unlock();
        }
    }
}
