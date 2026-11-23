package com.nnm.bot.warframe.runtime.service.worldstate;

import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateManagementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateFieldInspectionVO;

/** 为管理端组装与机器人命令一致的世界数据业务视图。 */
public interface WarframeWorldStateManagementService {

    /**
     * 组装当前世界状态管理视图。
     *
     * @return 当前世界状态各分区及刷新状态的管理视图。
     */
    WarframeWorldStateManagementVO current();

    /**
     * 读取同一运行时快照中的协议输入模型、业务解析模型和待处理字段诊断。
     *
     * @return 可折叠字段检查视图。
     */
    WarframeWorldStateFieldInspectionVO fields();
}
