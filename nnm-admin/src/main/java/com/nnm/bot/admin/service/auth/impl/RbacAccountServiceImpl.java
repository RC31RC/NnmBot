package com.nnm.bot.admin.service.auth.impl;

import com.nnm.bot.admin.service.auth.RbacAccountService;

import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RbacAccountServiceImpl implements RbacAccountService {

    @Resource
    private SysUserRepository userRepository;

    public void checkEnabled(long userId) {
        if (!userRepository.existsByIdAndEnabledTrue(userId)) {
            throw new BusinessException(401, "账号不可用");
        }
    }
}
