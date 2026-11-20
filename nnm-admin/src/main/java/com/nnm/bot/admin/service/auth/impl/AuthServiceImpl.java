package com.nnm.bot.admin.service.auth.impl;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.admin.constant.AdminAuthErrorCode;
import com.nnm.bot.admin.pojo.dto.auth.LoginDTO;
import cn.hutool.core.util.ObjUtil;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.pojo.entity.system.SysUser;
import com.nnm.bot.admin.pojo.dto.auth.LoginTokenDTO;
import com.nnm.bot.admin.pojo.dto.auth.RefreshTokenDTO;
import com.nnm.bot.admin.pojo.dto.auth.UserInfoDTO;
import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.admin.service.auth.AuthService;
import com.nnm.bot.common.error.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/** 校验后台账号凭据并维护 Sa-Token 登录会话。 */
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private SysUserRepository userRepository;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginTokenDTO login(LoginDTO request) {
        SysUser user = userRepository.findByUsernameIgnoreCase(CharSequenceUtil.trim(request.userName()))
            .orElseThrow(() -> new BusinessException(AdminAuthErrorCode.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(AdminAuthErrorCode.INVALID_CREDENTIALS);
        }
        if (!user.isEnabled()) {
            throw new BusinessException(AdminAuthErrorCode.ACCOUNT_UNAVAILABLE);
        }

        return login(user.getId());
    }

    @Override
    public LoginTokenDTO refresh(RefreshTokenDTO request) {
        String token = CharSequenceUtil.trim(request.token());
        Object loginId = CharSequenceUtil.isBlank(token) ? null : StpUtil.getLoginIdByToken(token);
        if (loginId == null || CharSequenceUtil.isBlank(String.valueOf(loginId))) {
            throw new BusinessException(AdminAuthErrorCode.TOKEN_INVALID);
        }
        long userId = parseLoginId(loginId);
        SysUser user = userRepository.findWithAuthoritiesById(userId).filter(SysUser::isEnabled)
            .orElseThrow(() -> new BusinessException(AdminAuthErrorCode.ACCOUNT_UNAVAILABLE));

        StpUtil.renewTimeout(token, SaManager.getConfig().getTimeout());
        return new LoginTokenDTO(token);
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public UserInfoDTO currentUser(long userId) {
        SysUser user = userRepository.findWithAuthoritiesById(userId).filter(SysUser::isEnabled)
            .orElseThrow(() -> new BusinessException(AdminAuthErrorCode.ACCOUNT_UNAVAILABLE));

        List<String> roles =
            user.getRoles().stream().filter(role -> role.isEnabled()).map(role -> role.getCode()).sorted().toList();

        List<String> buttons = user.getRoles().stream().filter(role -> role.isEnabled())
            .flatMap(role -> role.getPermissions().stream()).filter(permission -> permission.isEnabled())
            .map(permission -> permission.getCode()).distinct().sorted(Comparator.naturalOrder()).toList();

        return new UserInfoDTO(buttons, roles, user.getId(),
            ObjUtil.isNull(user.getNickname()) ? user.getUsername() : user.getNickname(),
            ObjUtil.isNull(user.getEmail()) ? "" : user.getEmail(), user.getAvatar());
    }

    private LoginTokenDTO login(long userId) {
        StpUtil.login(userId);
        String accessToken = StpUtil.getTokenValue();
        return new LoginTokenDTO(accessToken);
    }

    private long parseLoginId(Object loginId) {
        try {
            return Long.parseLong(String.valueOf(loginId));
        } catch (NumberFormatException e) {
            throw new BusinessException(AdminAuthErrorCode.TOKEN_INVALID);
        }
    }

}
