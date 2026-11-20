package com.nnm.bot.admin.service.system.impl;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.admin.constant.rbac.RbacErrorCode;
import com.nnm.bot.admin.pojo.dto.system.RoleGrantSaveDTO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;

import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.pojo.entity.system.SysMenu;
import com.nnm.bot.admin.pojo.entity.system.SysPermission;
import com.nnm.bot.admin.pojo.entity.system.SysRole;
import com.nnm.bot.admin.pojo.entity.system.SysUser;
import com.nnm.bot.admin.pojo.dto.system.MenuSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.PermissionSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.RoleGrantDTO;
import com.nnm.bot.admin.pojo.dto.system.RoleSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.UserRoleDTO;
import com.nnm.bot.admin.pojo.dto.system.UserSaveDTO;
import com.nnm.bot.admin.pojo.dto.system.UserStatusDTO;
import com.nnm.bot.admin.repository.system.SysMenuRepository;
import com.nnm.bot.admin.repository.system.SysPermissionRepository;
import com.nnm.bot.admin.repository.system.SysRoleRepository;
import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.admin.service.system.OperationAuditService;
import com.nnm.bot.admin.service.system.RbacHierarchyService;
import com.nnm.bot.admin.service.system.RbacManagementService;
import com.nnm.bot.admin.service.system.RbacQueryService;
import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.common.validation.BusinessAssert;
import jakarta.annotation.Resource;
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

/** 在管理范围校验后事务化修改 RBAC 用户、角色、菜单和权限点。 */
@Service
public class RbacManagementServiceImpl implements RbacManagementService {

    @Resource
    private SysUserRepository userRepository;
    @Resource
    private SysRoleRepository roleRepository;
    @Resource
    private SysMenuRepository menuRepository;
    @Resource
    private SysPermissionRepository permissionRepository;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private RbacQueryService queryService;
    @Resource
    private OperationAuditService auditService;
    @Resource
    private RbacHierarchyService hierarchyService;

    @Override
    @Transactional
    public long createUser(long operatorId, UserSaveDTO request) {
        String username = CharSequenceUtil.trim(request.userName());
        BusinessAssert.isFalse(userRepository.existsByUsernameIgnoreCase(username), RbacErrorCode.USERNAME_CONFLICT);
        BusinessAssert.notBlank(request.password(), RbacErrorCode.PASSWORD_REQUIRED);

        SysUser operator = hierarchyService.operator(operatorId);
        Set<SysRole> roles = resolveRoles(request.roleCodes());
        hierarchyService.checkCanAssignRoles(operator, roles);

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        applyUser(user, request);
        user.getRoles().addAll(roles);
        userRepository.save(user);
        auditService.record("USER_CREATE", "USER", user.getId(), "创建用户 " + username);
        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(long operatorId, long userId, UserSaveDTO request) {
        SysUser operator = hierarchyService.operator(operatorId);
        SysUser user = user(userId);
        hierarchyService.checkCanManageUser(operator, user);
        String username = CharSequenceUtil.trim(request.userName());
        userRepository.findByUsernameIgnoreCase(username).filter(existing -> !existing.getId().equals(userId))
            .ifPresent(existing -> {
                throw new BusinessException(RbacErrorCode.USERNAME_CONFLICT);
            });

        user.setUsername(username);
        if (CharSequenceUtil.isNotBlank(request.password())) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        applyUser(user, request);
        if (ObjUtil.isNotNull(request.roleCodes())) {
            Set<SysRole> roles = resolveRoles(request.roleCodes());
            hierarchyService.checkCanAssignRoles(operator, roles);
            user.getRoles().clear();
            user.getRoles().addAll(roles);
        }
        userRepository.save(user);
        auditService.record("USER_UPDATE", "USER", userId, "修改用户 " + username);
    }

    @Override
    @Transactional
    public void updateUserStatus(long operatorId, long userId, UserStatusDTO request) {
        BusinessAssert.isFalse(operatorId == userId && !request.enabled(), RbacErrorCode.CANNOT_DISABLE_SELF);
        SysUser operator = hierarchyService.operator(operatorId);
        SysUser user = user(userId);
        hierarchyService.checkCanManageUser(operator, user);
        user.setEnabled(request.enabled());
        userRepository.save(user);
        if (!request.enabled()) {
            StpUtil.getStpLogic().logout(userId);
        }
        auditService.record(request.enabled() ? "USER_ENABLE" : "USER_DISABLE", "USER", userId,
            request.enabled() ? "启用用户" : "禁用用户");
    }

    @Override
    @Transactional
    public void assignUserRoles(long operatorId, long userId, UserRoleDTO request) {
        SysUser operator = hierarchyService.operator(operatorId);
        SysUser user = user(userId);
        hierarchyService.checkCanManageUser(operator, user);
        Set<SysRole> roles = resolveRoles(request.roleCodes());
        hierarchyService.checkCanAssignRoles(operator, roles);
        user.getRoles().clear();
        user.getRoles().addAll(roles);
        userRepository.save(user);
        auditService.record("USER_ROLE_ASSIGN", "USER", userId, "分配角色 " + request.roleCodes());
    }

    @Override
    @Transactional
    public long createRole(long operatorId, RoleSaveDTO request) {
        SysUser operator = hierarchyService.operator(operatorId);
        int authorityLevel = requestedAuthorityLevel(request, 0);
        hierarchyService.checkRequestedLevel(operator, authorityLevel);
        String code = CharSequenceUtil.trim(request.roleCode());
        if (roleRepository.existsByCodeIgnoreCase(code)) {
            throw new BusinessException(RbacErrorCode.ROLE_CODE_CONFLICT);
        }
        SysRole role = new SysRole();
        applyRole(role, request, authorityLevel);
        roleRepository.save(role);
        auditService.record("ROLE_CREATE", "ROLE", role.getId(), "创建角色 " + code);
        return role.getId();
    }

    @Override
    @Transactional
    public void updateRole(long operatorId, long roleId, RoleSaveDTO request) {
        SysUser operator = hierarchyService.operator(operatorId);
        SysRole role = role(roleId);
        hierarchyService.checkCanManageRole(operator, role);
        int authorityLevel = requestedAuthorityLevel(request, role.getAuthorityLevel());
        hierarchyService.checkRequestedLevel(operator, authorityLevel);
        String code = CharSequenceUtil.trim(request.roleCode());
        roleRepository.findByCode(code).filter(existing -> !existing.getId().equals(roleId)).ifPresent(existing -> {
            throw new BusinessException(RbacErrorCode.ROLE_CODE_CONFLICT);
        });
        applyRole(role, request, authorityLevel);
        roleRepository.save(role);
        auditService.record("ROLE_UPDATE", "ROLE", roleId, "修改角色 " + code);
    }

    @Override
    @Transactional
    public void deleteRole(long operatorId, long roleId) {
        SysUser operator = hierarchyService.operator(operatorId);
        SysRole role = role(roleId);
        hierarchyService.checkCanManageRole(operator, role);
        if ("R_SUPER".equals(role.getCode())) {
            throw new BusinessException(RbacErrorCode.SUPER_ROLE_PROTECTED);
        }
        if (!userRepository.findAllByRoles_Id(roleId).isEmpty()) {
            throw new BusinessException(RbacErrorCode.ROLE_IN_USE);
        }
        roleRepository.delete(role);
        auditService.record("ROLE_DELETE", "ROLE", roleId, "删除角色 " + role.getCode());
    }

    @Override
    public RoleGrantDTO roleGrants(long operatorId, long roleId) {
        SysUser operator = hierarchyService.operator(operatorId);
        SysRole role = roleRepository.findWithGrantsById(roleId)
            .orElseThrow(() -> new BusinessException(RbacErrorCode.ROLE_NOT_FOUND));
        hierarchyService.checkCanManageRole(operator, role);
        return new RoleGrantDTO(queryService.allMenus(),
            role.getMenus().stream().map(SysMenu::getId).collect(Collectors.toSet()),
            role.getPermissions().stream().map(SysPermission::getId).collect(Collectors.toSet()));
    }

    @Override
    @Transactional
    public void assignRoleGrants(long operatorId, long roleId, RoleGrantSaveDTO request) {
        SysUser operator = hierarchyService.operator(operatorId);
        SysRole role = role(roleId);
        hierarchyService.checkCanManageRole(operator, role);
        Set<SysMenu> menus = loadAll(menuRepository.findAllById(request.menuIds()), request.menuIds(), "菜单");
        Set<SysPermission> permissions =
            loadAll(permissionRepository.findAllById(request.permissionIds()), request.permissionIds(), "权限");
        hierarchyService.checkCanAssignMenus(operator, menus);
        hierarchyService.checkCanAssignPermissions(operator, permissions);
        role.getMenus().clear();
        role.getMenus().addAll(menus);
        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);
        roleRepository.save(role);
        auditService.record("ROLE_GRANT_ASSIGN", "ROLE", roleId,
            "分配菜单 " + request.menuIds() + "，权限 " + request.permissionIds());
    }

    @Override
    @Transactional
    public long createMenu(MenuSaveDTO request) {
        if (menuRepository.existsByNameIgnoreCase(CharSequenceUtil.trim(request.name()))) {
            throw new BusinessException(RbacErrorCode.MENU_CODE_CONFLICT);
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
    public void updateMenu(long menuId, MenuSaveDTO request) {
        SysMenu menu = menu(menuId);
        menuRepository.findByName(CharSequenceUtil.trim(request.name()))
            .filter(existing -> !existing.getId().equals(menuId)).ifPresent(existing -> {
                throw new BusinessException(RbacErrorCode.MENU_CODE_CONFLICT);
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
            throw new BusinessException(RbacErrorCode.MENU_HAS_CHILDREN);
        }
        if (permissionRepository.existsByMenuId(menuId)) {
            throw new BusinessException(RbacErrorCode.MENU_HAS_PERMISSIONS);
        }
        List<SysRole> roles = roleRepository.findAll();
        roles.forEach(role -> role.getMenus().removeIf(item -> item.getId().equals(menuId)));
        roleRepository.saveAll(roles);
        menuRepository.delete(menu);
        auditService.record("MENU_DELETE", "MENU", menuId, "删除菜单 " + menu.getName());
    }

    @Override
    @Transactional
    public long createPermission(PermissionSaveDTO request) {
        if (permissionRepository.existsByCodeIgnoreCase(CharSequenceUtil.trim(request.code()))) {
            throw new BusinessException(RbacErrorCode.PERMISSION_CODE_CONFLICT);
        }
        menu(request.menuId());
        SysPermission permission = new SysPermission();
        applyPermission(permission, request);
        permissionRepository.save(permission);
        auditService.record("PERMISSION_CREATE", "PERMISSION", permission.getId(), "创建权限 " + permission.getCode());
        return permission.getId();
    }

    @Override
    @Transactional
    public void updatePermission(long permissionId, PermissionSaveDTO request) {
        SysPermission permission = permission(permissionId);
        permissionRepository.findByCode(CharSequenceUtil.trim(request.code()))
            .filter(existing -> !existing.getId().equals(permissionId)).ifPresent(existing -> {
                throw new BusinessException(RbacErrorCode.PERMISSION_CODE_CONFLICT);
            });
        menu(request.menuId());
        applyPermission(permission, request);
        permissionRepository.save(permission);
        auditService.record("PERMISSION_UPDATE", "PERMISSION", permissionId, "修改权限 " + permission.getCode());
    }

    @Override
    @Transactional
    public void deletePermission(long permissionId) {
        SysPermission permission = permission(permissionId);
        List<SysRole> roles = roleRepository.findAll();
        roles.forEach(role -> role.getPermissions().removeIf(item -> item.getId().equals(permissionId)));
        roleRepository.saveAll(roles);
        permissionRepository.delete(permission);
        auditService.record("PERMISSION_DELETE", "PERMISSION", permissionId, "删除权限 " + permission.getCode());
    }

    private void applyUser(SysUser user, UserSaveDTO request) {
        user.setNickname(trimToNull(request.nickName()));
        user.setPhone(trimToNull(request.userPhone()));
        user.setEmail(trimToNull(request.userEmail()));
        user.setGender(trimToNull(request.userGender()));
        user.setAvatar(trimToNull(request.avatar()));
        if (ObjUtil.isNotNull(request.enabled())) {
            user.setEnabled(request.enabled());
        }
    }

    private void applyRole(SysRole role, RoleSaveDTO request, int authorityLevel) {
        role.setName(CharSequenceUtil.trim(request.roleName()));
        role.setCode(CharSequenceUtil.trim(request.roleCode()));
        role.setDescription(trimToNull(request.description()));
        role.setEnabled(request.enabled());
        role.setDataScope(request.dataScope());
        role.setAuthorityLevel(authorityLevel);
    }

    private int requestedAuthorityLevel(RoleSaveDTO request, int defaultLevel) {
        return ObjUtil.isNull(request.authorityLevel()) ? defaultLevel : request.authorityLevel();
    }

    private void applyMenu(SysMenu menu, MenuSaveDTO request) {
        menu.setParentId(request.parentId());
        menu.setName(CharSequenceUtil.trim(request.name()));
        menu.setPath(CharSequenceUtil.trim(request.path()));
        menu.setComponent(trimToNull(request.component()));
        menu.setTitle(CharSequenceUtil.trim(request.title()));
        menu.setIcon(trimToNull(request.icon()));
        menu.setSortOrder(request.sortOrder());
        menu.setKeepAlive(request.keepAlive());
        menu.setVisible(request.visible());
        menu.setEnabled(request.enabled());
    }

    private void applyPermission(SysPermission permission, PermissionSaveDTO request) {
        permission.setMenuId(request.menuId());
        permission.setCode(CharSequenceUtil.trim(request.code()));
        permission.setName(CharSequenceUtil.trim(request.name()));
        permission.setType(request.type());
        permission.setEnabled(request.enabled());
    }

    private Set<SysRole> resolveRoles(Collection<String> roleCodes) {
        if (CollUtil.isEmpty(roleCodes)) {
            return new LinkedHashSet<>();
        }
        Set<String> codes = roleCodes.stream().filter(Objects::nonNull).map(CharSequenceUtil::trim)
            .filter(CharSequenceUtil::isNotEmpty).collect(Collectors.toSet());
        Set<SysRole> roles = roleRepository.findAll().stream().filter(role -> codes.contains(role.getCode()))
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (roles.size() != codes.size()) {
            throw new BusinessException(RbacErrorCode.ROLE_CODE_UNKNOWN);
        }
        return roles;
    }

    private <T> Set<T> loadAll(List<T> values, Set<Long> ids, String type) {
        if (values.size() != new HashSet<>(ids).size()) {
            throw new BusinessException(RbacErrorCode.INVALID_REFERENCE_LIST);
        }
        return new LinkedHashSet<>(values);
    }

    private void validateParent(Long parentId, Long menuId) {
        if (ObjUtil.isNull(parentId)) {
            return;
        }
        if (Objects.equals(parentId, menuId)) {
            throw new BusinessException(RbacErrorCode.MENU_SELF_PARENT);
        }
        SysMenu parent = menu(parentId);
        Set<Long> visited = new HashSet<>();
        while (ObjUtil.isNotNull(parent.getParentId())) {
            if (!visited.add(parent.getId()) || Objects.equals(parent.getParentId(), menuId)) {
                throw new BusinessException(RbacErrorCode.MENU_PARENT_CYCLE);
            }
            parent = menu(parent.getParentId());
        }
    }

    private SysUser user(long id) {
        return userRepository.findWithAuthoritiesById(id)
            .orElseThrow(() -> new BusinessException(RbacErrorCode.USER_NOT_FOUND));
    }

    private SysRole role(long id) {
        return roleRepository.findById(id).orElseThrow(() -> new BusinessException(RbacErrorCode.ROLE_NOT_FOUND));
    }

    private SysMenu menu(long id) {
        return menuRepository.findById(id).orElseThrow(() -> new BusinessException(RbacErrorCode.MENU_NOT_FOUND));
    }

    private SysPermission permission(long id) {
        return permissionRepository.findById(id)
            .orElseThrow(() -> new BusinessException(RbacErrorCode.PERMISSION_NOT_FOUND));
    }

    private String trimToNull(String value) {
        return CharSequenceUtil.isBlank(value) ? null : CharSequenceUtil.trim(value);
    }
}
