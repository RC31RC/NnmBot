package com.nnm.bot.warframe.persistent.curated.event;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.transaction.AfterCommitExecutor;
import com.nnm.bot.warframe.config.cache.DatasetRevisionDTO;
import com.nnm.bot.warframe.config.cache.WarframeDatasetCache;
import com.nnm.bot.warframe.constant.WarframeCacheErrorCode;
import com.nnm.bot.warframe.constant.WarframeDatasetCacheNames;
import com.nnm.bot.warframe.mq.message.persistent.curated.reference.WarframeCommunityDatasetInvalidationMessage;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityDatasetLockRepository;
import io.github.rdt.queue.RdtMessageSendManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** 社区表分别推进版本，不再清空官方名称、图片等无关缓存。 */
@Component
@Slf4j
public class WarframeCommunityDataChangePublisher {
    @Resource
    private WarframeCommunityDatasetLockRepository locks;
    @Resource
    private WarframeDatasetCache cache;
    @Resource
    private RdtMessageSendManager messages;
    @Resource
    private AfterCommitExecutor afterCommitExecutor;

    /**
     * 持有社区数据集锁的业务事务内递增版本，成功提交后广播。
     *
     * @param dataset 实际变更的社区数据集。
     */
    public void invalidateAfterCommit(WarframeCommunityDataset dataset) {
        var row = locks.findByDataset(dataset.code()).orElseThrow(
            () -> new SystemException(WarframeCacheErrorCode.DATASET_VERSION_MISSING, "数据集版本锁行未初始化，请执行本轮 SQL"));
        row.setDataRevision(Math.incrementExact(row.getDataRevision()));
        locks.save(row);
        afterCommitExecutor.executeOnce(WarframeDatasetCacheNames.community(dataset), () -> {
            var change = new DatasetRevisionDTO(WarframeDatasetCacheNames.community(dataset), row.getDataRevision());
            cache.observe(change);
            try {
                messages.send(new WarframeCommunityDatasetInvalidationMessage(change));
            } catch (RuntimeException exception) {
                log.warn("社区数据已提交，版本广播失败 dataset={} revision={}", change.dataset(), change.revision(), exception);
            }
        });
    }
}
