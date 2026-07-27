package com.nnm.bot.admin.security;

import com.nnm.bot.admin.domain.SysPermission;
import com.nnm.bot.admin.domain.SysRole;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.repository.SysUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RbacStpInterfaceTest {

    @Mock
    private SysUserRepository userRepository;

    private RbacStpInterface stpInterface;

    @BeforeEach
    void setUp() {
        stpInterface = new RbacStpInterface(userRepository);
    }

    @Test
    void permissionListContainsOnlyEnabledPermissionsFromEnabledRoles() {
        SysPermission userView = permission("system:user:view", true);
        SysPermission roleView = permission("system:role:view", true);
        SysPermission disabled = permission("system:user:delete", false);

        SysRole admin = role("R_ADMIN", true);
        admin.getPermissions().add(userView);
        admin.getPermissions().add(disabled);

        SysRole disabledRole = role("R_DISABLED", false);
        disabledRole.getPermissions().add(roleView);

        SysUser user = new SysUser();
        user.getRoles().add(admin);
        user.getRoles().add(disabledRole);
        given(userRepository.findByIdWithAuthorities(1L)).willReturn(Optional.of(user));

        assertThat(stpInterface.getPermissionList(1L, "login"))
                .containsExactly("system:user:view");
    }

    @Test
    void roleListContainsOnlyEnabledRolesInStableOrder() {
        SysUser user = new SysUser();
        user.getRoles().add(role("R_USER", true));
        user.getRoles().add(role("R_ADMIN", true));
        user.getRoles().add(role("R_DISABLED", false));
        given(userRepository.findByIdWithAuthorities(1L)).willReturn(Optional.of(user));

        assertThat(stpInterface.getRoleList(1L, "login"))
                .containsExactly("R_ADMIN", "R_USER");
    }

    private SysRole role(String code, boolean enabled) {
        SysRole role = new SysRole();
        role.setCode(code);
        role.setEnabled(enabled);
        return role;
    }

    private SysPermission permission(String code, boolean enabled) {
        SysPermission permission = new SysPermission();
        permission.setCode(code);
        permission.setEnabled(enabled);
        return permission;
    }
}
