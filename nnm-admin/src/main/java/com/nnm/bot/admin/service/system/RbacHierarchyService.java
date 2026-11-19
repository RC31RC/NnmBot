package com.nnm.bot.admin.service.system;

import com.nnm.bot.admin.pojo.entity.system.SysRole;
import com.nnm.bot.admin.pojo.entity.system.SysUser;
import com.nnm.bot.admin.pojo.entity.system.SysMenu;
import com.nnm.bot.admin.pojo.entity.system.SysPermission;
import java.util.Collection;
import java.util.Map;

/** 根据数据范围与授权等级判断 RBAC 管理关系。 */
public interface RbacHierarchyService {

    /** 加载执行权限操作的用户。
     * @param operatorId 操作人主键
     * @return 操作人实体
     */
    SysUser operator(long operatorId);

    /** 判断操作人是否可管理目标用户。
     * @param operator 操作人
     * @param target 目标用户
     * @return 可管理时为 {@code true}
     */
    boolean canManageUser(SysUser operator, SysUser target);

    /** 获取用户生效的最高授权等级。
     * @param userId 用户主键
     * @return 生效授权等级
     */
    int effectiveLevel(long userId);

    /** 批量查询用户生效授权等级。
     * @param userIds 用户主键集合
     * @return 用户主键到授权等级的映射
     */
    Map<Long, Integer> assignedLevels(Collection<Long> userIds);

    /** 判断操作人是否可管理目标角色。
     * @param operator 操作人
     * @param role 目标角色
     * @return 可管理时为 {@code true}
     */
    boolean canManageRole(SysUser operator, SysRole role);

    /** 判断操作人是否可向用户分配目标角色。
     * @param operator 操作人
     * @param role 待分配角色
     * @return 可分配时为 {@code true}
     */
    boolean canAssignRole(SysUser operator, SysRole role);

    /** 校验操作人对目标用户的管理权限。
     * @param operator 操作人
     * @param target 目标用户
     */
    void checkCanManageUser(SysUser operator, SysUser target);

    /** 校验操作人对目标角色的管理权限。
     * @param operator 操作人
     * @param role 目标角色
     */
    void checkCanManageRole(SysUser operator, SysRole role);

    /** 校验操作人可分配的角色集合。
     * @param operator 操作人
     * @param roles 待分配角色
     */
    void checkCanAssignRoles(SysUser operator, Collection<SysRole> roles);

    /** 校验请求的授权等级不超出操作人范围。
     * @param operator 操作人
     * @param authorityLevel 请求的授权等级
     */
    void checkRequestedLevel(SysUser operator, int authorityLevel);

    /** 校验菜单授权范围。
     * @param operator 操作人
     * @param menus 待分配菜单
     */
    void checkCanAssignMenus(SysUser operator, Collection<SysMenu> menus);

    /** 校验权限点授权范围。
     * @param operator 操作人
     * @param permissions 待分配权限点
     */
    void checkCanAssignPermissions(SysUser operator, Collection<SysPermission> permissions);
}
