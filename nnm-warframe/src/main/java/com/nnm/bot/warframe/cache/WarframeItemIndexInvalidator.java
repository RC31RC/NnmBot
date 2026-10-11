package com.nnm.bot.warframe.cache;

import com.nnm.bot.warframe.messaging.producer.WarframeItemIndexInvalidationMessageProducer;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 在物品、翻译或别名写入成功后使解析索引失效，避免提交前重建索引读到旧数据。
 */
@Component
public class WarframeItemIndexInvalidator {

    @Resource
    private CacheManager cacheManager;
    @Resource
    private ObjectProvider<WarframeItemIndexInvalidationMessageProducer> invalidationProducer;

    /** 事务回滚不触碰缓存；提交成功后由下一次查询按需重建完整索引。 */
    public void invalidateAfterCommit() {
        Runnable invalidate = () -> {
            clearLocal();
            WarframeItemIndexInvalidationMessageProducer producer =
                    invalidationProducer == null ? null : invalidationProducer.getIfAvailable();
            if (producer != null) {
                producer.publish();
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    invalidate.run();
                }
            });
        } else {
            invalidate.run();
        }
    }

    /**
     * 清理当前实例的本地 cache2k 物品解析索引。
     */
    public void clearLocal() {
        Cache cache = cacheManager.getCache(WarframeCacheNames.ITEM_RESOLUTION_INDEX);
        if (cache != null) {
            cache.clear();
        }
    }
}
