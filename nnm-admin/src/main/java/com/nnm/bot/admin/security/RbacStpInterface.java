package com.nnm.bot.admin.security;

import cn.dev33.satoken.stp.StpInterface;
import com.nnm.bot.admin.pojo.entity.system.SysRole;
import com.nnm.bot.admin.pojo.entity.system.SysUser;
import com.nnm.bot.admin.repository.system.SysUserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RbacStpInterface implements StpInterface {

    @Resource
    private SysUserRepository userRepository;

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
        return userRepository.findWithAuthoritiesById(userId).orElseGet(SysUser::new);
    }
}
