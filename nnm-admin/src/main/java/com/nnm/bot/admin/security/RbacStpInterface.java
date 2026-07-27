package com.nnm.bot.admin.security;

import cn.dev33.satoken.stp.StpInterface;
import com.nnm.bot.admin.domain.SysRole;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RbacStpInterface implements StpInterface {

    private final SysUserRepository userRepository;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return user(loginId).getRoles().stream()
                .filter(SysRole::isEnabled)
                .flatMap(role -> role.getPermissions().stream())
                .filter(permission -> permission.isEnabled())
                .map(permission -> permission.getCode())
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return user(loginId).getRoles().stream()
                .filter(SysRole::isEnabled)
                .map(SysRole::getCode)
                .sorted()
                .toList();
    }

    private SysUser user(Object loginId) {
        long userId;
        try {
            userId = Long.parseLong(String.valueOf(loginId));
        } catch (NumberFormatException e) {
            return new SysUser();
        }
        return userRepository.findByIdWithAuthorities(userId).orElseGet(SysUser::new);
    }
}
