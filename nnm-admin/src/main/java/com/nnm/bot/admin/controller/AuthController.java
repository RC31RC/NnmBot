package com.nnm.bot.admin.controller;

import com.nnm.bot.admin.dto.auth.LoginRequest;
import com.nnm.bot.admin.dto.auth.LoginResponse;
import com.nnm.bot.admin.dto.auth.RefreshTokenRequest;
import com.nnm.bot.admin.service.AuthService;
import com.nnm.bot.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/v1/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.ok();
    }
}
