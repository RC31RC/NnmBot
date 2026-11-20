package com.nnm.bot.admin.service.system;

import com.nnm.bot.admin.pojo.dto.system.RoleGrantSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.MenuSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.PermissionSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.RoleGrantDTO;
import com.nnm.bot.admin.pojo.dto.system.RoleSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.UserRoleDTO;
import com.nnm.bot.admin.pojo.dto.system.UserSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.UserStatusDTO;

/** 执行后台用户、角色、菜单和权限点的 RBAC 变更。 */
public interface RbacManagementService {

    /**
     * 创建后台用户。
     *
     * @param operatorId 操作人主键
     * @param request 用户资料及初始角色
     * @return 新用户主键
     */
    long createUser(long operatorId, UserSaveDTO request);

    /**
     * 更新后台用户资料。
     *
     * @param operatorId 操作人主键
     * @param userId 目标用户主键
     * @param request 更新内容
     */
    void updateUser(long operatorId, long userId, UserSaveDTO request);

    /**
     * 更新用户启用状态。
     *
     * @param operatorId 操作人主键
     * @param userId 目标用户主键
     * @param request 状态变更
     */
    void updateUserStatus(long operatorId, long userId, UserStatusDTO request);

    /**
     * 替换用户角色集合。
     *
     * @param operatorId 操作人主键
     * @param userId 目标用户主键
     * @param request 目标角色集合
     */
    void assignUserRoles(long operatorId, long userId, UserRoleDTO request);

    /**
     * 创建系统角色。
     *
     * @param operatorId 操作人主键
     * @param request 角色资料
     * @return 新角色主键
     */
    long createRole(long operatorId, RoleSaveDTO request);

    /**
     * 更新系统角色。
     *
     * @param operatorId 操作人主键
     * @param roleId 目标角色主键
     * @param request 更新内容
     */
    void updateRole(long operatorId, long roleId, RoleSaveDTO request);

    /**
     * 删除系统角色。
     *
     * @param operatorId 操作人主键
     * @param roleId 目标角色主键
     */
    void deleteRole(long operatorId, long roleId);

    /**
     * 查询角色当前授权集合。
     *
     * @param operatorId 操作人主键
     * @param roleId 目标角色主键
     * @return 角色授权详情
     */
    RoleGrantDTO roleGrants(long operatorId, long roleId);

    /**
     * 替换角色的菜单与权限点授权。
     *
     * @param operatorId 操作人主键
     * @param roleId 目标角色主键
     * @param request 目标授权集合
     */
    void assignRoleGrants(long operatorId, long roleId, RoleGrantSaveDTO request);

    /**
     * 创建管理菜单。
     *
     * @param request 菜单资料
     * @return 新菜单主键
     */
    long createMenu(MenuSaveDTO request);

    /**
     * 更新管理菜单。
     *
     * @param menuId 菜单主键
     * @param request 更新内容
     */
    void updateMenu(long menuId, MenuSaveDTO request);

    /**
     * 删除管理菜单。
     *
     * @param menuId 菜单主键
     */
    void deleteMenu(long menuId);

    /**
     * 创建权限点。
     *
     * @param request 权限点资料
     * @return 新权限点主键
     */
    long createPermission(PermissionSaveDTO request);

    /**
     * 更新权限点。
     *
     * @param permissionId 权限点主键
     * @param request 更新内容
     */
    void updatePermission(long permissionId, PermissionSaveDTO request);

    /**
     * 删除权限点。
     *
     * @param permissionId 权限点主键
     */
    void deletePermission(long permissionId);
}
