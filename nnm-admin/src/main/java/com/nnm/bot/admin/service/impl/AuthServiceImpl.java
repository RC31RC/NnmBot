package com.nnm.bot.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.dto.auth.LoginRequest;
import com.nnm.bot.admin.dto.auth.LoginResponse;
import com.nnm.bot.admin.dto.auth.RefreshTokenRequest;
import com.nnm.bot.admin.dto.auth.UserInfoResponse;
import com.nnm.bot.admin.repository.SysUserRepository;
import com.nnm.bot.admin.service.AuthService;
import com.nnm.bot.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        Object loginId = StpUtil.getLoginIdByToken(request.refreshToken());
        if (loginId == null) {
            throw new BusinessException(401, "刷新令牌无效");
        }

        SysUser user = userRepository.findWithAuthoritiesById(parseLoginId(loginId))
                .filter(SysUser::isEnabled)
                .orElseThrow(() -> new BusinessException(401, "账号不可用"));

        LoginResponse response = login(user.getId());
        StpUtil.logoutByTokenValue(request.refreshToken());
        return response;
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
                user.getNickname() == null ? user.getUsername() : user.getNickname(),
                user.getEmail() == null ? "" : user.getEmail(),
                user.getAvatar()
        );
    }

    private LoginResponse login(long userId) {
        StpUtil.login(userId);
        String token = StpUtil.getTokenValue();
        return new LoginResponse(token, token);
    }

    private long parseLoginId(Object loginId) {
        try {
            return Long.parseLong(String.valueOf(loginId));
        } catch (NumberFormatException e) {
            throw new BusinessException(401, "刷新令牌无效");
        }
    }
}
