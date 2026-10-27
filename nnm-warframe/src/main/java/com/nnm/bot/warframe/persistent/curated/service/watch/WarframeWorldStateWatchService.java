package com.nnm.bot.warframe.persistent.curated.service.watch;

import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;

/** 根据运行时世界状态处理本项目维护的订阅规则。 */
public interface WarframeWorldStateWatchService {

    /**
     * 匹配当前规则、认领事件并向具备发送连接的群组推送通知。
     *
     * @param current 最近成功发布的世界状态。
     */
    void notifyMatches(WorldStateDTO current);
}
