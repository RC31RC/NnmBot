package com.nnm.bot.admin.service;

import com.nnm.bot.admin.dto.system.MenuSaveRequest;
import com.nnm.bot.admin.dto.system.PermissionSaveRequest;
import com.nnm.bot.admin.dto.system.RoleGrantRequest;
import com.nnm.bot.admin.dto.system.RoleGrantResponse;
import com.nnm.bot.admin.dto.system.RoleSaveRequest;
import com.nnm.bot.admin.dto.system.UserRoleRequest;
import com.nnm.bot.admin.dto.system.UserSaveRequest;
import com.nnm.bot.admin.dto.system.UserStatusRequest;

public interface RbacManagementService {

    long createUser(UserSaveRequest request);

    void updateUser(long userId, UserSaveRequest request);

    void updateUserStatus(long operatorId, long userId, UserStatusRequest request);

    void assignUserRoles(long userId, UserRoleRequest request);

    long createRole(RoleSaveRequest request);

    void updateRole(long roleId, RoleSaveRequest request);

    void deleteRole(long roleId);

    RoleGrantResponse roleGrants(long roleId);

    void assignRoleGrants(long roleId, RoleGrantRequest request);

    long createMenu(MenuSaveRequest request);

    void updateMenu(long menuId, MenuSaveRequest request);

    void deleteMenu(long menuId);

    long createPermission(PermissionSaveRequest request);

    void updatePermission(long permissionId, PermissionSaveRequest request);

    void deletePermission(long permissionId);
}
