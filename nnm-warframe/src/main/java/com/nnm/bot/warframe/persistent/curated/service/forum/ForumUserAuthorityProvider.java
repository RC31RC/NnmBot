package com.nnm.bot.warframe.persistent.curated.service.forum;

import java.util.Collection;
import java.util.Map;

/**
 * 为 Warframe 论坛提供后台用户层级，避免业务模块反向依赖管理模块。
 */
public interface ForumUserAuthorityProvider {

    /**
     * 计算当前用户在论坛操作中实际生效的权限等级。
     *
     * @param userId 用户标识。
     * @return 当前操作计算或读取的数值。
     */
    int effectiveLevel(long userId);

    /**
     * 读取指定用户已经授予的论坛权限等级。
     *
     * @param userIds 用户标识集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    Map<Long, Integer> assignedLevels(Collection<Long> userIds);
}
