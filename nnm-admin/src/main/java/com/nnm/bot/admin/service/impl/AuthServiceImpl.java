package com.nnm.bot.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.dto.auth.LoginRequest;
import com.nnm.bot.admin.dto.auth.LoginResponse;
import com.nnm.bot.admin.dto.auth.RefreshTokenRequest;
import com.nnm.bot.admin.dto.auth.UserInfoResponse;
import com.nnm.bot.admin.repository.SysUserRepository;
import com.nnm.bot.admin.service.AuthService;
import com.nnm.bot.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private SysUserRepository userRepository;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = userRepository.findByUsernameIgnoreCase(request.userName().trim())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (!user.isEnabled()) {
            throw new BusinessException(401, "账号不可用");
        }

        return login(user.getId());
    }

    @Override
    public LoginResponse refresh(RefreshTokenRequest request) {
        String token = request.token().trim();
        Object loginId = StrUtil.isBlank(token) ? null : StpUtil.getLoginIdByToken(token);
        if (loginId == null || StrUtil.isBlank(String.valueOf(loginId))) {
            throw new BusinessException(401, "登录令牌无效");
        }
        long userId = parseLoginId(loginId);
        SysUser user = userRepository.findWithAuthoritiesById(userId)
                .filter(SysUser::isEnabled)
                .orElseThrow(() -> new BusinessException(401, "账号不可用"));

        StpUtil.renewTimeout(token, SaManager.getConfig().getTimeout());
        return new LoginResponse(token);
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public UserInfoResponse currentUser(long userId) {
        SysUser user = userRepository.findWithAuthoritiesById(userId)
                .filter(SysUser::isEnabled)
                .orElseThrow(() -> new BusinessException(401, "账号不可用"));

        List<String> roles = user.getRoles().stream()
                .filter(role -> role.isEnabled())
                .map(role -> role.getCode())
                .sorted()
                .toList();

        List<String> buttons = user.getRoles().stream()
                .filter(role -> role.isEnabled())
                .flatMap(role -> role.getPermissions().stream())
                .filter(permission -> permission.isEnabled())
                .map(permission -> permission.getCode())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();

        return new UserInfoResponse(
                buttons,
                roles,
                user.getId(),
                ObjectUtil.isNull(user.getNickname()) ? user.getUsername() : user.getNickname(),
                ObjectUtil.isNull(user.getEmail()) ? "" : user.getEmail(),
                user.getAvatar()
        );
    }

    private LoginResponse login(long userId) {
        StpUtil.login(userId);
        String accessToken = StpUtil.getTokenValue();
        return new LoginResponse(accessToken);
    }

    private long parseLoginId(Object loginId) {
        try {
            return Long.parseLong(String.valueOf(loginId));
        } catch (NumberFormatException e) {
            throw new BusinessException(401, "登录令牌无效");
        }
    }

}
