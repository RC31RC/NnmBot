package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.dto.WarframeWorldStateItem;
import com.nnm.bot.warframe.dto.WarframeWorldStateRawItem;

/** 为管理端组装与机器人命令一致的世界数据业务视图。 */
public interface WarframeWorldStateManagementService {

    WarframeWorldStateItem current();

    WarframeWorldStateRawItem raw();
}
