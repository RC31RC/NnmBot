package com.nnm.bot.admin.service.auth;

/** 提供认证流程使用的账号状态校验。 */
public interface RbacAccountService {

    /**
     * 确认用户账号处于允许登录的状态。
     *
     * @param userId 用户主键
     */
    void checkEnabled(long userId);
}
