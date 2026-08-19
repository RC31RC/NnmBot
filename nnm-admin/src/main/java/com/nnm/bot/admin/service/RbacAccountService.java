package com.nnm.bot.admin.service;

import com.nnm.bot.admin.repository.SysUserRepository;
import com.nnm.bot.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RbacAccountService {

    @Resource
    private SysUserRepository userRepository;

    public void checkEnabled(long userId) {
        if (!userRepository.existsByIdAndEnabledTrue(userId)) {
            throw new BusinessException(401, "账号不可用");
        }
    }
}
