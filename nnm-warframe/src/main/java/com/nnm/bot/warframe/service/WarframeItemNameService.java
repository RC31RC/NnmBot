package com.nnm.bot.warframe.service;

import com.nnm.bot.warframe.application.WarframeReferenceResolution;

import java.util.Collection;
import java.util.Map;

/** 内部业务键的精确名称投影，不使用路径截断或玩家输入模糊匹配。 */
public interface WarframeItemNameService {
    Map<String, WarframeReferenceResolution> resolveAll(Collection<String> itemKeys);
}
