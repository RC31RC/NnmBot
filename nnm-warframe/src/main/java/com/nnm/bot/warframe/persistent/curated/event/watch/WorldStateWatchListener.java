package com.nnm.bot.warframe.persistent.curated.event.watch;

import com.nnm.bot.warframe.persistent.curated.service.watch.WarframeWorldStateWatchService;
import com.nnm.bot.warframe.runtime.event.worldstate.WorldStateUpdatedEvent;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** 将本实例世界状态更新事件转交给订阅业务服务。 */
@Component
public class WorldStateWatchListener {

    @Resource
    private WarframeWorldStateWatchService watchService;

    /**
     * 处理当前实例发布的成功快照，不重新拉取上游数据。
     *
     * @param event 当前实例的世界状态更新事件。
     */
    @EventListener
    public void onWorldStateUpdated(WorldStateUpdatedEvent event) {
        if (event != null && event.current() != null) {
            watchService.notifyMatches(event.current());
        }
    }
}
