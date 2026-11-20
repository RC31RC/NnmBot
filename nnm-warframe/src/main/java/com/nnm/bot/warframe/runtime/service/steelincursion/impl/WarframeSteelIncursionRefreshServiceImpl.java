package com.nnm.bot.warframe.runtime.service.steelincursion.impl;

import com.nnm.bot.warframe.runtime.cache.steelincursion.WarframeSteelIncursionSnapshotStore;
import com.nnm.bot.warframe.runtime.client.steelincursion.WarframeSteelIncursionClient;
import com.nnm.bot.warframe.runtime.config.steelincursion.WarframeSteelIncursionProperties;
import com.nnm.bot.warframe.runtime.parser.steelincursion.WarframeSteelIncursionParser;
import com.nnm.bot.warframe.runtime.service.steelincursion.WarframeSteelIncursionRefreshService;
import jakarta.annotation.Resource;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** 按成功刷新周期管理钢铁侵袭快照；失败后由定时任务按重试间隔再次触发。 */
@Slf4j
@Service
public class WarframeSteelIncursionRefreshServiceImpl implements WarframeSteelIncursionRefreshService {
    private final ReentrantLock refreshLock = new ReentrantLock();

    @Resource
    private WarframeSteelIncursionClient client;
    @Resource
    private WarframeSteelIncursionParser parser;
    @Resource
    private WarframeSteelIncursionSnapshotStore snapshots;
    @Resource
    private WarframeSteelIncursionProperties properties;
    @Resource
    private Clock warframeClock;

    /** 拉取仅在 refreshInterval 到期时发生；失败会在下一次 retryInterval 调度时重试。 */
    @Override
    public void refresh() {
        refreshLock.lock();
        try {
            Instant now = warframeClock.instant();
            Instant fetchedAt = snapshots.current().fetchedAt();
            if (fetchedAt != null && now.isBefore(fetchedAt.plus(properties.getRefreshInterval())))
                return;
            try {
                var entries = parser.parse(client.fetch());
                snapshots.replace(entries, warframeClock.instant());
            } catch (RuntimeException exception) {
                log.warn("钢铁侵袭排期刷新失败，保留最近一次有效内存快照 source={} retryAfter={}", properties.getUrl(),
                    properties.getRetryInterval(), exception);
            }
        } finally {
            refreshLock.unlock();
        }
    }
}
