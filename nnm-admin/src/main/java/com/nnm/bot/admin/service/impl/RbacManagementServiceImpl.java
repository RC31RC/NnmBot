package com.nnm.bot.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.domain.SysMenu;
import com.nnm.bot.admin.domain.SysPermission;
import com.nnm.bot.admin.domain.SysRole;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.dto.system.MenuSaveRequest;
import com.nnm.bot.admin.dto.system.PermissionSaveRequest;
import com.nnm.bot.admin.dto.system.RoleGrantRequest;
import com.nnm.bot.admin.dto.system.RoleGrantResponse;
import com.nnm.bot.admin.dto.system.RoleSaveRequest;
import com.nnm.bot.admin.dto.system.UserRoleRequest;
import com.nnm.bot.admin.dto.system.UserSaveRequest;
import com.nnm.bot.admin.dto.system.UserStatusRequest;
import com.nnm.bot.admin.repository.SysMenuRepository;
import com.nnm.bot.admin.repository.SysPermissionRepository;
import com.nnm.bot.admin.repository.SysRoleRepository;
import com.nnm.bot.admin.repository.SysUserRepository;
import com.nnm.bot.admin.service.OperationAuditService;
import com.nnm.bot.admin.service.RbacManagementService;
import com.nnm.bot.admin.service.RbacQueryService;
import com.nnm.bot.common.error.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.DataNotFoundException;
import com.nnm.bot.common.validation.BusinessAssert;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RbacManagementServiceImpl implements RbacManagementService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final SysMenuRepository menuRepository;
    private final SysPermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final RbacQueryService queryService;
    private final OperationAuditService auditService;

    @Override
    @Transactional
    public long createUser(UserSaveRequest request) {
        String username = request.userName().trim();
        BusinessAssert.isFalse(
                userRepository.existsByUsernameIgnoreCase(username),
                CommonErrorCode.DATA_CONFLICT,
                "用户名已存在"
        );
        BusinessAssert.notBlank(
                request.password(),
                CommonErrorCode.PARAM_INVALID,
                "新增用户必须设置密码"
        );

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        applyUser(user, request);
        user.getRoles().addAll(resolveRoles(request.roleCodes()));
        userRepository.save(user);
        auditService.record("USER_CREATE", "USER", user.getId(), "创建用户 " + username);
        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(long userId, UserSaveRequest request) {
        SysUser user = user(userId);
        String username = request.userName().trim();
        userRepository.findByUsernameIgnoreCase(username)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new BusinessException(409, "用户名已存在");
                });

        user.setUsername(username);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        applyUser(user, request);
        if (request.roleCodes() != null) {
            user.getRoles().clear();
            user.getRoles().addAll(resolveRoles(request.roleCodes()));
        }
        userRepository.save(user);
        auditService.record("USER_UPDATE", "USER", userId, "修改用户 " + username);
    }

    @Override
    @Transactional
    public void updateUserStatus(long operatorId, long userId, UserStatusRequest request) {
        BusinessAssert.isFalse(
                operatorId == userId && !request.enabled(),
                CommonErrorCode.PARAM_INVALID,
                "不能禁用当前登录账号"
        );
        SysUser user = user(userId);
        user.setEnabled(request.enabled());
        userRepository.save(user);
        if (!request.enabled()) {
            StpUtil.getStpLogic().logout(userId);
        }
        auditService.record(
                request.enabled() ? "USER_ENABLE" : "USER_DISABLE",
                "USER",
                userId,
                request.enabled() ? "启用用户" : "禁用用户"
        );
    }

    @Override
    @Transactional
    public void assignUserRoles(long userId, UserRoleRequest request) {
        SysUser user = user(userId);
        user.getRoles().clear();
        user.getRoles().addAll(resolveRoles(request.roleCodes()));
        userRepository.save(user);
        auditService.record("USER_ROLE_ASSIGN", "USER", userId, "分配角色 " + request.roleCodes());
    }

    @Override
    @Transactional
    public long createRole(RoleSaveRequest request) {
        String code = request.roleCode().trim();
        if (roleRepository.existsByCodeIgnoreCase(code)) {
            throw new BusinessException(409, "角色编码已存在");
        }
        SysRole role = new SysRole();
        applyRole(role, request);
        roleRepository.save(role);
        auditService.record("ROLE_CREATE", "ROLE", role.getId(), "创建角色 " + code);
        return role.getId();
    }

    @Override
    @Transactional
    public void updateRole(long roleId, RoleSaveRequest request) {
        SysRole role = role(roleId);
        String code = request.roleCode().trim();
        roleRepository.findByCode(code)
                .filter(existing -> !existing.getId().equals(roleId))
                .ifPresent(existing -> {
                    throw new BusinessException(409, "角色编码已存在");
                });
        applyRole(role, request);
        roleRepository.save(role);
        auditService.record("ROLE_UPDATE", "ROLE", roleId, "修改角色 " + code);
    }

    @Override
    @Transactional
    public void deleteRole(long roleId) {
        SysRole role = role(roleId);
        if ("R_SUPER".equals(role.getCode())) {
            throw new BusinessException(400, "超级管理员角色不能删除");
        }
        if (!userRepository.findAllByRoles_Id(roleId).isEmpty()) {
            throw new BusinessException(409, "角色仍被用户使用，不能删除");
        }
        roleRepository.delete(role);
        auditService.record("ROLE_DELETE", "ROLE", roleId, "删除角色 " + role.getCode());
    }

    @Override
    public RoleGrantResponse roleGrants(long roleId) {
        SysRole role = roleRepository.findWithGrantsById(roleId)
                .orElseThrow(() -> new DataNotFoundException("角色不存在"));
        return new RoleGrantResponse(
                queryService.allMenus(),
                role.getMenus().stream().map(SysMenu::getId).collect(Collectors.toSet()),
                role.getPermissions().stream().map(SysPermission::getId).collect(Collectors.toSet())
        );
    }

    @Override
    @Transactional
    public void assignRoleGrants(long roleId, RoleGrantRequest request) {
        SysRole role = role(roleId);
        Set<SysMenu> menus = loadAll(menuRepository.findAllById(request.menuIds()), request.menuIds(), "菜单");
        Set<SysPermission> permissions = loadAll(
                permissionRepository.findAllById(request.permissionIds()),
                request.permissionIds(),
                "权限"
        );
        role.getMenus().clear();
        role.getMenus().addAll(menus);
        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);
        roleRepository.save(role);
        auditService.record(
                "ROLE_GRANT_ASSIGN",
                "ROLE",
                roleId,
                "分配菜单 " + request.menuIds() + "，权限 " + request.permissionIds()
        );
    }

    @Override
    @Transactional
    public long createMenu(MenuSaveRequest request) {
        if (menuRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new BusinessException(409, "菜单标识已存在");
        }
        validateParent(request.parentId(), null);
        SysMenu menu = new SysMenu();
        applyMenu(menu, request);
        menuRepository.save(menu);
        auditService.record("MENU_CREATE", "MENU", menu.getId(), "创建菜单 " + menu.getName());
        return menu.getId();
    }

    @Override
    @Transactional
    public void updateMenu(long menuId, MenuSaveRequest request) {
        SysMenu menu = menu(menuId);
        menuRepository.findByName(request.name().trim())
                .filter(existing -> !existing.getId().equals(menuId))
                .ifPresent(existing -> {
                    throw new BusinessException(409, "菜单标识已存在");
                });
        validateParent(request.parentId(), menuId);
        applyMenu(menu, request);
        menuRepository.save(menu);
        auditService.record("MENU_UPDATE", "MENU", menuId, "修改菜单 " + menu.getName());
    }

    @Override
    @Transactional
    public void deleteMenu(long menuId) {
        SysMenu menu = menu(menuId);
        if (menuRepository.existsByParentId(menuId)) {
            throw new BusinessException(409, "请先删除子菜单");
        }
        if (permissionRepository.existsByMenuId(menuId)) {
            throw new BusinessException(409, "请先删除菜单下的按钮权限");
        }
        List<SysRole> roles = roleRepository.findAll();
        roles.forEach(role -> role.getMenus().removeIf(item -> item.getId().equals(menuId)));
        roleRepository.saveAll(roles);
        menuRepository.delete(menu);
        auditService.record("MENU_DELETE", "MENU", menuId, "删除菜单 " + menu.getName());
    }

    @Override
    @Transactional
    public long createPermission(PermissionSaveRequest request) {
        if (permissionRepository.existsByCodeIgnoreCase(request.code().trim())) {
            throw new BusinessException(409, "权限标识已存在");
        }
        menu(request.menuId());
        SysPermission permission = new SysPermission();
        applyPermission(permission, request);
        permissionRepository.save(permission);
        auditService.record(
                "PERMISSION_CREATE",
                "PERMISSION",
                permission.getId(),
                "创建权限 " + permission.getCode()
        );
        return permission.getId();
    }

    @Override
    @Transactional
    public void updatePermission(long permissionId, PermissionSaveRequest request) {
        SysPermission permission = permission(permissionId);
        permissionRepository.findByCode(request.code().trim())
                .filter(existing -> !existing.getId().equals(permissionId))
                .ifPresent(existing -> {
                    throw new BusinessException(409, "权限标识已存在");
                });
        menu(request.menuId());
        applyPermission(permission, request);
        permissionRepository.save(permission);
        auditService.record(
                "PERMISSION_UPDATE",
                "PERMISSION",
                permissionId,
                "修改权限 " + permission.getCode()
        );
    }

    @Override
    @Transactional
    public void deletePermission(long permissionId) {
        SysPermission permission = permission(permissionId);
        List<SysRole> roles = roleRepository.findAll();
        roles.forEach(role -> role.getPermissions().removeIf(item -> item.getId().equals(permissionId)));
        roleRepository.saveAll(roles);
        permissionRepository.delete(permission);
        auditService.record(
                "PERMISSION_DELETE",
                "PERMISSION",
                permissionId,
                "删除权限 " + permission.getCode()
        );
    }

    private void applyUser(SysUser user, UserSaveRequest request) {
        user.setNickname(trimToNull(request.nickName()));
        user.setPhone(trimToNull(request.userPhone()));
        user.setEmail(trimToNull(request.userEmail()));
        user.setGender(trimToNull(request.userGender()));
        user.setAvatar(trimToNull(request.avatar()));
        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }
    }

    private void applyRole(SysRole role, RoleSaveRequest request) {
        role.setName(request.roleName().trim());
        role.setCode(request.roleCode().trim());
        role.setDescription(trimToNull(request.description()));
        role.setEnabled(request.enabled());
        role.setDataScope(request.dataScope());
    }

    private void applyMenu(SysMenu menu, MenuSaveRequest request) {
        menu.setParentId(request.parentId());
        menu.setName(request.name().trim());
        menu.setPath(request.path().trim());
        menu.setComponent(trimToNull(request.component()));
        menu.setTitle(request.title().trim());
        menu.setIcon(trimToNull(request.icon()));
        menu.setSortOrder(request.sortOrder());
        menu.setKeepAlive(request.keepAlive());
        menu.setVisible(request.visible());
        menu.setEnabled(request.enabled());
    }

    private void applyPermission(SysPermission permission, PermissionSaveRequest request) {
        permission.setMenuId(request.menuId());
        permission.setCode(request.code().trim());
        permission.setName(request.name().trim());
        permission.setType(request.type());
        permission.setEnabled(request.enabled());
    }

    private Set<SysRole> resolveRoles(Collection<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return new LinkedHashSet<>();
        }
        Set<String> codes = roleCodes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .collect(Collectors.toSet());
        Set<SysRole> roles = roleRepository.findAll().stream()
                .filter(role -> codes.contains(role.getCode()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (roles.size() != codes.size()) {
            throw new BusinessException(400, "包含不存在的角色编码");
        }
        return roles;
    }

    private <T> Set<T> loadAll(List<T> values, Set<Long> ids, String type) {
        if (values.size() != new HashSet<>(ids).size()) {
            throw new BusinessException(400, type + "列表包含不存在的数据");
        }
        return new LinkedHashSet<>(values);
    }

    private void validateParent(Long parentId, Long menuId) {
        if (parentId == null) {
            return;
        }
        if (Objects.equals(parentId, menuId)) {
            throw new BusinessException(400, "菜单不能将自己设为父级");
        }
        SysMenu parent = menu(parentId);
        Set<Long> visited = new HashSet<>();
        while (parent.getParentId() != null) {
            if (!visited.add(parent.getId()) || Objects.equals(parent.getParentId(), menuId)) {
                throw new BusinessException(400, "菜单父级关系不能形成循环");
            }
            parent = menu(parent.getParentId());
        }
    }

    private SysUser user(long id) {
        return userRepository.findByIdWithAuthorities(id)
                .orElseThrow(() -> new DataNotFoundException("用户不存在"));
    }

    private SysRole role(long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("角色不存在"));
    }

    private SysMenu menu(long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("菜单不存在"));
    }

    private SysPermission permission(long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("权限不存在"));
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
