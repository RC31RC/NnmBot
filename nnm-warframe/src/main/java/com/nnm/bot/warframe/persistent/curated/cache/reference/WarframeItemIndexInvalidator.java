package com.nnm.bot.warframe.persistent.curated.cache.reference;

import com.nnm.bot.common.transaction.AfterCommitExecutor;
import com.nnm.bot.warframe.constant.WarframeCacheNames;
import com.nnm.bot.warframe.mq.message.persistent.curated.reference.WarframeItemIndexInvalidationMessage;
import com.nnm.bot.warframe.persistent.official.event.WarframeOfficialDataChangedEvent;
import io.github.rdt.queue.RdtMessageSendManager;
import jakarta.annotation.Resource;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

/**
 * 在物品、翻译或别名写入成功后使解析索引失效，避免提交前重建索引读到旧数据。
 */
@Component
public class WarframeItemIndexInvalidator {

    @Resource
    private CacheManager cacheManager;
    @Resource
    private RdtMessageSendManager messages;
    @Resource
    private AfterCommitExecutor afterCommitExecutor;

    /**
     * 官方名称或物品变更提交后，使依赖它们的玩家输入解析索引失效。
     *
     * @param event 官方数据已成功提交的本地事件。
     */
    @EventListener
    public void onOfficialDataChanged(WarframeOfficialDataChangedEvent event) {
        invalidateNow();
    }

    /** 事务回滚不触碰缓存；提交成功后由下一次查询按需重建完整索引。 */
    public void invalidateAfterCommit() {
        afterCommitExecutor.execute(this::invalidateNow);
    }

    /**
     * 立即使本实例相关业务缓存失效。
     */
    public void invalidateNow() {
        clearLocal();
        messages.send(new WarframeItemIndexInvalidationMessage("invalidate"));
    }

    /**
     * 清理当前实例的本地 cache2k 物品解析索引。
     */
    public void clearLocal() {
        for (String name : new String[]{WarframeCacheNames.ITEM_RESOLUTION_INDEX, WarframeCacheNames.IMAGE_INDEX}) {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        }
    }
}
