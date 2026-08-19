package com.nnm.bot.admin.service.impl;

import com.nnm.bot.admin.domain.DataScope;
import com.nnm.bot.admin.domain.SysMenu;
import com.nnm.bot.admin.domain.SysPermission;
import com.nnm.bot.admin.domain.SysRole;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.dto.system.AuthItem;
import com.nnm.bot.admin.dto.system.MenuItem;
import com.nnm.bot.admin.dto.system.MenuMeta;
import com.nnm.bot.admin.dto.system.PermissionItem;
import com.nnm.bot.admin.dto.system.RoleListItem;
import com.nnm.bot.admin.dto.system.UserListItem;
import com.nnm.bot.admin.repository.SysPermissionRepository;
import com.nnm.bot.admin.repository.SysRoleRepository;
import com.nnm.bot.admin.repository.SysUserRepository;
import com.nnm.bot.admin.repository.SysMenuRepository;
import com.nnm.bot.admin.service.RbacQueryService;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.response.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RbacQueryServiceImpl implements RbacQueryService {

    @Resource
    private SysUserRepository userRepository;
    @Resource
    private SysRoleRepository roleRepository;
    @Resource
    private SysPermissionRepository permissionRepository;
    @Resource
    private SysMenuRepository menuRepository;

    @Override
    public PageResult<UserListItem> users(long operatorId, String username, int current, int size) {
        SysUser operator = userRepository.findWithAuthoritiesById(operatorId)
                .filter(SysUser::isEnabled)
                .orElseThrow(() -> new BusinessException(401, "账号不可用"));
        boolean canViewAll = operator.getRoles().stream()
                .filter(SysRole::isEnabled)
                .anyMatch(role -> role.getDataScope() == DataScope.ALL);
        if (!canViewAll) {
            boolean matches = username == null
                    || username.isBlank()
                    || operator.getUsername().toLowerCase().contains(username.trim().toLowerCase());
            List<UserListItem> records = current <= 1 && matches
                    ? List.of(toUserItem(operator))
                    : List.of();
            return PageResult.of(
                    records,
                    Math.max(current, 1),
                    Math.min(Math.max(size, 1), 100),
                    matches ? 1 : 0
            );
        }

        PageRequest pageable = pageable(current, size, "createdAt");
        Page<SysUserRepository.IdOnly> page = username == null || username.isBlank()
                ? userRepository.findPageBy(pageable)
                : userRepository.findPageByUsernameContainingIgnoreCase(username.trim(), pageable);

        List<Long> userIds = page.getContent().stream()
                .map(SysUserRepository.IdOnly::getId)
                .toList();
        Map<Long, SysUser> usersById = userRepository.findPageDetailsByIdIn(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, user -> user));
        List<UserListItem> records = userIds.stream()
                .map(usersById::get)
                .filter(Objects::nonNull)
                .map(this::toUserItem)
                .toList();
        return PageResult.of(
                records,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements()
        );
    }

    @Override
    public PageResult<RoleListItem> roles(String keyword, int current, int size) {
        PageRequest pageable = pageable(current, size, "createdAt");
        Page<SysRole> page = keyword == null || keyword.isBlank()
                ? roleRepository.findAll(pageable)
                : roleRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                        keyword.trim(),
                        keyword.trim(),
                        pageable
                );

        List<RoleListItem> records = page.getContent().stream()
                .map(role -> new RoleListItem(
                        role.getId(),
                        role.getName(),
                        role.getCode(),
                        role.getDescription(),
                        role.isEnabled(),
                        role.getDataScope().name(),
                        role.getCreatedAt()
                ))
                .toList();
        return PageResult.of(
                records,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements()
        );
    }

    @Override
    public List<PermissionItem> permissions() {
        return permissionRepository.findAllByOrderByCodeAsc().stream()
                .map(permission -> new PermissionItem(
                        permission.getId(),
                        permission.getCode(),
                        permission.getName(),
                        permission.getType().name(),
                        permission.isEnabled()
                ))
                .toList();
    }

    @Override
    public List<MenuItem> menus(long userId) {
        SysUser user = userRepository.findWithAuthoritiesById(userId)
                .filter(SysUser::isEnabled)
                .orElseThrow(() -> new BusinessException(401, "账号不可用"));

        Set<SysMenu> menus = user.getRoles().stream()
                .filter(SysRole::isEnabled)
                .flatMap(role -> role.getMenus().stream())
                .filter(SysMenu::isEnabled)
                .collect(Collectors.toSet());

        Set<String> grantedPermissionCodes = user.getRoles().stream()
                .filter(SysRole::isEnabled)
                .flatMap(role -> role.getPermissions().stream())
                .filter(SysPermission::isEnabled)
                .map(SysPermission::getCode)
                .collect(Collectors.toSet());

        Map<Long, List<SysPermission>> permissionsByMenu = permissionRepository.findAllByOrderByCodeAsc().stream()
                .filter(SysPermission::isEnabled)
                .filter(permission -> permission.getMenuId() != null)
                .filter(permission -> grantedPermissionCodes.contains(permission.getCode()))
                .collect(Collectors.groupingBy(SysPermission::getMenuId));

        Map<Long, List<SysMenu>> childrenByParent = menus.stream()
                .filter(menu -> menu.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        return menus.stream()
                .filter(menu -> menu.getParentId() == null || menus.stream()
                        .noneMatch(parent -> Objects.equals(parent.getId(), menu.getParentId())))
                .sorted(menuComparator())
                .map(menu -> toMenuItem(menu, childrenByParent, permissionsByMenu))
                .toList();
    }

    @Override
    public List<MenuItem> allMenus() {
        Set<SysMenu> menus = Set.copyOf(menuRepository.findAllByOrderBySortOrderAscIdAsc());
        Map<Long, List<SysPermission>> permissionsByMenu = permissionRepository.findAllByOrderByCodeAsc().stream()
                .filter(permission -> permission.getMenuId() != null)
                .collect(Collectors.groupingBy(SysPermission::getMenuId));
        Map<Long, List<SysMenu>> childrenByParent = menus.stream()
                .filter(menu -> menu.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId));
        return menus.stream()
                .filter(menu -> menu.getParentId() == null || menus.stream()
                        .noneMatch(parent -> Objects.equals(parent.getId(), menu.getParentId())))
                .sorted(menuComparator())
                .map(menu -> toMenuItem(menu, childrenByParent, permissionsByMenu))
                .toList();
    }

    private MenuItem toMenuItem(
            SysMenu menu,
            Map<Long, List<SysMenu>> childrenByParent,
            Map<Long, List<SysPermission>> permissionsByMenu
    ) {
        List<AuthItem> authList = permissionsByMenu.getOrDefault(menu.getId(), List.of()).stream()
                .map(permission -> new AuthItem(
                        permission.getId(),
                        permission.getName(),
                        permission.getCode(),
                        permission.isEnabled()
                ))
                .toList();
        List<MenuItem> children = childrenByParent.getOrDefault(menu.getId(), List.of()).stream()
                .sorted(menuComparator())
                .map(child -> toMenuItem(child, childrenByParent, permissionsByMenu))
                .toList();

        return new MenuItem(
                menu.getId(),
                menu.getParentId(),
                menu.getPath(),
                menu.getName(),
                menu.getComponent(),
                new MenuMeta(
                        menu.getTitle(),
                        menu.getIcon(),
                        menu.isKeepAlive(),
                        menu.isFixedTab(),
                        menu.isHideTab(),
                        menu.isFullPage(),
                        menu.isIframe(),
                        menu.getLink(),
                        menu.getActivePath(),
                        menu.getTextBadge(),
                        menu.getSortOrder(),
                        menu.isEnabled(),
                        !menu.isVisible(),
                        authList.isEmpty() ? null : authList
                ),
                children.isEmpty() ? null : children
        );
    }

    private PageRequest pageable(int current, int size, String sortProperty) {
        int page = Math.max(current, 1) - 1;
        int pageSize = Math.min(Math.max(size, 1), 100);
        return PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, sortProperty));
    }

    private Comparator<SysMenu> menuComparator() {
        return Comparator.comparingInt(SysMenu::getSortOrder).thenComparing(SysMenu::getId);
    }

    private UserListItem toUserItem(SysUser user) {
        return new UserListItem(
                user.getId(),
                value(user.getAvatar()),
                user.isEnabled() ? "1" : "4",
                user.getUsername(),
                value(user.getGender()),
                user.getNickname() == null ? user.getUsername() : user.getNickname(),
                value(user.getPhone()),
                value(user.getEmail()),
                user.getRoles().stream()
                        .map(SysRole::getCode)
                        .sorted()
                        .toList(),
                "system",
                user.getCreatedAt(),
                "system",
                user.getUpdatedAt()
        );
    }

    private String value(String value) {
        return value == null ? "" : value;
    }
}
