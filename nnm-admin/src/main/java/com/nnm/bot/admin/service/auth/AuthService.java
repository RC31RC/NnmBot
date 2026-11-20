package com.nnm.bot.admin.service.auth;

import com.nnm.bot.admin.pojo.dto.auth.LoginDTO;
import com.nnm.bot.admin.pojo.dto.auth.LoginTokenDTO;
import com.nnm.bot.admin.pojo.dto.auth.RefreshTokenDTO;
import com.nnm.bot.admin.pojo.dto.auth.UserInfoDTO;

/** 管理端登录、会话续期及当前用户信息查询。 */
public interface AuthService {

    /**
     * 校验凭据并创建登录会话。
     *
     * @param request 登录凭据
     * @return 新建会话的令牌信息
     */
    LoginTokenDTO login(LoginDTO request);

    /**
     * 使用刷新凭据续期会话。
     *
     * @param request 刷新令牌
     * @return 更新后的令牌信息
     */
    LoginTokenDTO refresh(RefreshTokenDTO request);

    /** 注销当前会话。 */
    void logout();

    /**
     * 查询指定用户的管理端资料及权限视图。
     *
     * @param userId 用户主键
     * @return 当前用户信息
     */
    UserInfoDTO currentUser(long userId);
}
