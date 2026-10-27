package com.nnm.bot.admin.service.auth;

import com.nnm.bot.admin.pojo.dto.auth.LoginDTO;
import com.nnm.bot.admin.pojo.dto.auth.LoginTokenDTO;
import com.nnm.bot.admin.pojo.dto.auth.RefreshTokenDTO;
import com.nnm.bot.admin.pojo.dto.auth.UserInfoDTO;

public interface AuthService {

    LoginTokenDTO login(LoginDTO request);

    LoginTokenDTO refresh(RefreshTokenDTO request);

    void logout();

    UserInfoDTO currentUser(long userId);
}
