package com.nnm.bot.admin.service.system;

import com.nnm.bot.admin.pojo.dto.system.RoleGrantSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.MenuSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.PermissionSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.RoleGrantDTO;
import com.nnm.bot.admin.pojo.dto.system.RoleSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.UserRoleDTO;
import com.nnm.bot.admin.pojo.dto.system.UserSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.UserStatusDTO;

public interface RbacManagementService {

    long createUser(long operatorId, UserSaveDTO request);

    void updateUser(long operatorId, long userId, UserSaveDTO request);

    void updateUserStatus(long operatorId, long userId, UserStatusDTO request);

    void assignUserRoles(long operatorId, long userId, UserRoleDTO request);

    long createRole(long operatorId, RoleSaveDTO request);

    void updateRole(long operatorId, long roleId, RoleSaveDTO request);

    void deleteRole(long operatorId, long roleId);

    RoleGrantDTO roleGrants(long operatorId, long roleId);

    void assignRoleGrants(long operatorId, long roleId, RoleGrantSaveDTO request);

    long createMenu(MenuSaveDTO request);

    void updateMenu(long menuId, MenuSaveDTO request);

    void deleteMenu(long menuId);

    long createPermission(PermissionSaveDTO request);

    void updatePermission(long permissionId, PermissionSaveDTO request);

    void deletePermission(long permissionId);
}
