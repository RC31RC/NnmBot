package com.nnm.bot.warframe.persistent.official.event;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.transaction.AfterCommitExecutor;
import com.nnm.bot.warframe.config.cache.DatasetRevisionDTO;
import com.nnm.bot.warframe.config.cache.WarframeDatasetCache;
import com.nnm.bot.warframe.constant.WarframeCacheErrorCode;
import com.nnm.bot.warframe.constant.WarframeDatasetCacheNames;
import com.nnm.bot.warframe.mq.message.persistent.official.dataset.WarframeOfficialDatasetInvalidationMessage;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialFileRepository;
import io.github.rdt.queue.RdtMessageSendManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** 业务写入与版本递增同事务，提交后只通知对应数据集，不同步重读业务数据。 */
@Component
@Slf4j
public class WarframeOfficialDataChangePublisher {
    @Resource
    private AfterCommitExecutor afterCommitExecutor;
    @Resource
    private WarframeOfficialFileRepository files;
    @Resource
    private WarframeDatasetCache cache;
    @Resource
    private RdtMessageSendManager messages;

    /**
     * 在当前持有文件锁的写事务中推进版本；回滚不改变缓存，广播失败不误报业务失败。
     *
     * @param domain 实际发生业务内容变化的 JSON 领域。
     */
    public void publishAfterCommit(WarframeOfficialDataDomain domain) {
        var file = files.findFirstByProductPath(domain.productPath()).orElseThrow(
            () -> new SystemException(WarframeCacheErrorCode.DATASET_VERSION_MISSING, "数据集版本锁行未初始化，请执行本轮 SQL"));
        file.setDataRevision(Math.incrementExact(file.getDataRevision()));
        files.save(file);
        afterCommitExecutor.executeOnce(WarframeDatasetCacheNames.official(domain), () -> {
            var change = new DatasetRevisionDTO(WarframeDatasetCacheNames.official(domain), file.getDataRevision());
            cache.observe(change);
            try {
                messages.send(new WarframeOfficialDatasetInvalidationMessage(change));
            } catch (RuntimeException exception) {
                // 无需可靠事务消息；其他实例的版本复核会补偿本次通知遗漏。
                log.warn("官方数据已提交，版本广播失败 dataset={} revision={}", change.dataset(), change.revision(), exception);
            }
        });
    }
}
