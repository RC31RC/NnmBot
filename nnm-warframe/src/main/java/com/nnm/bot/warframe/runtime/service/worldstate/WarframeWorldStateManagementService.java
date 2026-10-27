package com.nnm.bot.warframe.runtime.service.worldstate;

import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateManagementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateRawVO;

/** 为管理端组装与机器人命令一致的世界数据业务视图。 */
public interface WarframeWorldStateManagementService {

    /**
     * 组装当前世界状态管理视图。
     *
     * @return 当前世界状态各分区及刷新状态的管理视图。
     */
    WarframeWorldStateManagementVO current();

    /**
     * 读取排障使用的原始快照和采集信息。
     *
     * @return 用于排障的世界状态原文及采集信息。
     */
    WarframeWorldStateRawVO raw();
}
