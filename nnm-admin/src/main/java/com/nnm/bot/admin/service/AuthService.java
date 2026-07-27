package com.nnm.bot.admin.service;

import com.nnm.bot.admin.dto.auth.LoginRequest;
import com.nnm.bot.admin.dto.auth.LoginResponse;
import com.nnm.bot.admin.dto.auth.RefreshTokenRequest;
import com.nnm.bot.admin.dto.auth.UserInfoResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(RefreshTokenRequest request);

    void logout();

    UserInfoResponse currentUser(long userId);
}
