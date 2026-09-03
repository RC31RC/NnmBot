package com.nnm.bot.integration.warframe;

import com.nnm.bot.admin.service.RbacHierarchyService;
import com.nnm.bot.warframe.application.WarframeForumUserAuthorityProvider;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * 在应用组装层连接后台 RBAC 与 Warframe 论坛，保持两个业务模块单向独立。
 */
@Component
public class AdminWarframeForumUserAuthorityProvider
        implements WarframeForumUserAuthorityProvider {

    @Resource
    private RbacHierarchyService hierarchyService;

    @Override
    public int effectiveLevel(long userId) {
        return hierarchyService.effectiveLevel(userId);
    }

    @Override
    public Map<Long, Integer> assignedLevels(Collection<Long> userIds) {
        return hierarchyService.assignedLevels(userIds);
    }
}
