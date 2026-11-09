package com.nnm.bot.warframe.schedule.persistent.official.worldstate;

import com.nnm.bot.warframe.config.cache.WarframeDatasetCache;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityDatasetLockRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialFileRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 各实例只复核少量元数据版本；不拉取上游、不全量重建语言与代码表。 */
@Component
public class WorldStateCodebookRefreshTask {
    @Resource
    private WarframeOfficialFileRepository official;
    @Resource
    private WarframeCommunityDatasetLockRepository community;
    @Resource
    private WarframeDatasetCache cache;

    /** 首次查询前装载版本，之后按配置补偿遗漏的跨实例通知。 */
    @PostConstruct
    @Scheduled(fixedDelayString = "${nnm.warframe.cache.revision-check-interval:5m}",
        initialDelayString = "${nnm.warframe.cache.revision-check-interval:5m}")
    public void refresh() {
        official.findDataRevisions().forEach(cache::observe);
        community.findDataRevisions().forEach(cache::observe);
    }
}
