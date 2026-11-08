package com.nnm.bot.warframe.persistent.curated.event.watch;

import com.nnm.bot.core.group.event.BotGroupDeletingEvent;
import com.nnm.bot.warframe.persistent.curated.repository.watch.WarframeGroupWatchRuleRepository;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** 在群组删除事务内清理订阅，防止后台轮询继续读取已删除群组的规则。 */
@Component
public class WarframeGroupDeletionListener {
    @Resource
    private WarframeGroupWatchRuleRepository repository;

    /**
     * 同步删除群组专属规则；失败直接传播，使群组删除事务回滚。
     *
     * @param event 群组删除事务发出的清理通知。
     */
    @EventListener
    public void delete(BotGroupDeletingEvent event) {
        repository.deleteAllByGroupId(event.groupId());
        repository.flush();
    }
}
