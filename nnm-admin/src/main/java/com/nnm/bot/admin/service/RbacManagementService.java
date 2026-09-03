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

    long createUser(long operatorId, UserSaveRequest request);

    void updateUser(long operatorId, long userId, UserSaveRequest request);

    void updateUserStatus(long operatorId, long userId, UserStatusRequest request);

    void assignUserRoles(long operatorId, long userId, UserRoleRequest request);

    long createRole(long operatorId, RoleSaveRequest request);

    void updateRole(long operatorId, long roleId, RoleSaveRequest request);

    void deleteRole(long operatorId, long roleId);

    RoleGrantResponse roleGrants(long operatorId, long roleId);

    void assignRoleGrants(long operatorId, long roleId, RoleGrantRequest request);

    long createMenu(MenuSaveRequest request);

    void updateMenu(long menuId, MenuSaveRequest request);

    void deleteMenu(long menuId);

    long createPermission(PermissionSaveRequest request);

    void updatePermission(long permissionId, PermissionSaveRequest request);

    void deletePermission(long permissionId);
}
