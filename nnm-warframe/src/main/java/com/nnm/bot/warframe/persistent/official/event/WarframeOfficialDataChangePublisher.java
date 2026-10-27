package com.nnm.bot.warframe.persistent.official.event;

import com.nnm.bot.common.transaction.AfterCommitExecutor;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/** 在官方数据成功提交后通知依赖方，写服务不引用其他数据来源的缓存。 */
@Component
public class WarframeOfficialDataChangePublisher {

    @Resource
    private AfterCommitExecutor afterCommitExecutor;
    @Resource
    private ApplicationEventPublisher events;

    /**
     * 当前事务提交后发布数据变化；回滚时不触发查询索引失效。
     */
    public void publishAfterCommit() {
        afterCommitExecutor.execute(() -> events.publishEvent(new WarframeOfficialDataChangedEvent()));
    }
}
