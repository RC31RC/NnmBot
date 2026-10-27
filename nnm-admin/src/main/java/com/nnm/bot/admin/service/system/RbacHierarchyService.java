package com.nnm.bot.admin.service.system;

import com.nnm.bot.admin.pojo.entity.system.SysRole;
import com.nnm.bot.admin.pojo.entity.system.SysUser;
import com.nnm.bot.admin.pojo.entity.system.SysMenu;
import com.nnm.bot.admin.pojo.entity.system.SysPermission;
import java.util.Collection;
import java.util.Map;

public interface RbacHierarchyService {
    SysUser operator(long operatorId);

    boolean canManageUser(SysUser operator, SysUser target);

    int effectiveLevel(long userId);

    Map<Long, Integer> assignedLevels(Collection<Long> userIds);

    boolean canManageRole(SysUser operator, SysRole role);

    boolean canAssignRole(SysUser operator, SysRole role);

    void checkCanManageUser(SysUser operator, SysUser target);

    void checkCanManageRole(SysUser operator, SysRole role);

    void checkCanAssignRoles(SysUser operator, Collection<SysRole> roles);

    void checkRequestedLevel(SysUser operator, int authorityLevel);

    void checkCanAssignMenus(SysUser operator, Collection<SysMenu> menus);

    void checkCanAssignPermissions(SysUser operator, Collection<SysPermission> permissions);
}
