package com.nnm.bot.admin.service.auth.impl;

import com.nnm.bot.admin.constant.AdminAuthErrorCode;
import com.nnm.bot.admin.service.auth.RbacAccountService;

import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.common.error.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/** 在认证边界确认用户账号仍处于启用状态。 */
@Service
public class RbacAccountServiceImpl implements RbacAccountService {

    @Resource
    private SysUserRepository userRepository;

    public void checkEnabled(long userId) {
        if (!userRepository.existsByIdAndEnabledTrue(userId)) {
            throw new BusinessException(AdminAuthErrorCode.ACCOUNT_UNAVAILABLE);
        }
    }
}
