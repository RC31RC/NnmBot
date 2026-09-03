package com.nnm.bot.warframe.application;

import java.util.Collection;
import java.util.Map;

/**
 * 为 Warframe 论坛提供后台用户层级，避免业务模块反向依赖管理模块。
 */
public interface WarframeForumUserAuthorityProvider {

    int effectiveLevel(long userId);

    Map<Long, Integer> assignedLevels(Collection<Long> userIds);
}
