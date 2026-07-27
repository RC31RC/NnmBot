package com.nnm.bot.admin.service;

import com.nnm.bot.admin.repository.SysUserRepository;
import com.nnm.bot.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RbacAccountService {

    private final SysUserRepository userRepository;

    public void checkEnabled(long userId) {
        if (!userRepository.existsByIdAndEnabledTrue(userId)) {
            throw new BusinessException(401, "账号不可用");
        }
    }
}
